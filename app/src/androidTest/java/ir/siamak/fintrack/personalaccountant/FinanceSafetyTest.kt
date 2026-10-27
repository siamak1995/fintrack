package ir.siamak.fintrack.personalaccountant

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import ir.siamak.fintrack.personalaccountant.data.local.database.AppDatabase
import ir.siamak.fintrack.personalaccountant.data.local.entity.*
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.personalaccountant.banksms.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.*
import org.junit.Assert.*
import java.math.BigDecimal
import java.time.*

class FinanceSafetyTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var db: AppDatabase
    private lateinit var inbox: BankSmsInbox
    private lateinit var scope: CoroutineScope
    private lateinit var directory: java.io.File
    @Before fun setup() = runBlocking {
        db = Room.inMemoryDatabaseBuilder(context,AppDatabase::class.java).build()
        db.walletDao().ensureCash()
        db.walletDao().insertWallet(WalletEntity(id = 10,name = "بانک یک",balance = 500000.0,color = "#000000",cardNumber = "6037991234561234"))
        db.walletDao().insertWallet(WalletEntity(id = 11,name = "بانک دو",balance = 100000.0,color = "#000000"))
        db.memberDao().insertMember(MemberEntity(id = 1,name = "خودم",relation = "self",color = "#000000",icon = "person"))
        directory = java.io.File(context.cacheDir,"sms-test-" + java.util.UUID.randomUUID()).apply { mkdirs() }
        scope = CoroutineScope(SupervisorJob()+Dispatchers.IO)
        inbox = BankSmsInbox(PreferenceDataStoreFactory.create(scope = scope,produceFile = { java.io.File(directory,"review.preferences_pb") }))
    }
    @After fun close() = runBlocking { scope.coroutineContext[Job]?.cancelAndJoin(); db.close(); directory.deleteRecursively(); Unit }
    private suspend fun candidate(body: String): BankSmsCandidate {
        assertTrue(inbox.capture(body,"بانک",1793000000000,automatic = false))
        return inbox.state.first().pending.last()
    }
    @Test fun consentInboxNeverChangesBalanceUntilApprovalAndReplayIsIdempotent() = runBlocking {
        val row = candidate("بانک نمونه\nواریز: 75000 ریال\nحساب: 1234")
        assertEquals(500000.0,db.walletDao().getWalletById(10)!!.balance,0.0)
        val approve = ImportBankSms(db,inbox)
        approve(row,BigDecimal("7500"),BankSmsKind.INCOME,10,null,1)
        approve(row,BigDecimal("7500"),BankSmsKind.INCOME,10,null,1)
        assertEquals(507500.0,db.walletDao().getWalletById(10)!!.balance,0.0)
        assertEquals(1,db.transactionDao().getAllTransactionsSync().size)
        assertTrue(inbox.state.first().pending.isEmpty())
    }
    @Test fun transferUpdatesBothBalancesAtomicallyAndRejectsSameAccount() = runBlocking {
        val row = candidate("بانک نمونه انتقال وجه مبلغ 250000 ریال")
        val approve = ImportBankSms(db,inbox)
        assertTrue(runCatching { approve(row,BigDecimal("25000"),BankSmsKind.TRANSFER,10,10,1) }.isFailure)
        assertEquals(500000.0,db.walletDao().getWalletById(10)!!.balance,0.0)
        assertTrue(db.transactionDao().getAllTransactionsSync().isEmpty())
        approve(row,BigDecimal("25000"),BankSmsKind.TRANSFER,10,11,1)
        assertEquals(475000.0,db.walletDao().getWalletById(10)!!.balance,0.0)
        assertEquals(125000.0,db.walletDao().getWalletById(11)!!.balance,0.0)
        assertEquals(TransactionType.TRANSFER,db.transactionDao().getAllTransactionsSync().single().type)
    }
    @Test fun cashIsASingleProtectedDefaultAndArchivingPreservesTransactions() = runBlocking {
        repeat(3) { db.walletDao().ensureCash() }
        val cash = db.walletDao().getAllWallets().first().single { it.isCash }
        assertTrue(runCatching { db.walletDao().deleteWallet(cash) }.isFailure)
        val row = candidate("بانک نمونه واریز 100 تومان حساب 1234")
        ImportBankSms(db,inbox)(row,BigDecimal("100"),BankSmsKind.INCOME,10,null,1)
        db.walletDao().deleteWallet(db.walletDao().getWalletById(10)!!)
        assertTrue(db.walletDao().getWalletById(10)!!.isDeleted)
        assertFalse(db.walletDao().getAllWallets().first().any { it.id == 10L })
        assertEquals(1,db.transactionDao().getAllTransactionsSync().size)
    }
    @Test fun approvedSmsCreatesAccountAndUsesStatementBalanceExactlyOnce() = runBlocking {
        val row = candidate("بلو\n220,000 ریال بابت خرید شارژ از حساب شما پرید.\nموجودی:12,222,569 ریال")
        val approve = ImportBankSms(db,inbox)
        assertFalse(db.walletDao().getAllWallets().first().any { it.name == "بانک بلو" })
        approve(row,BigDecimal("22000"),BankSmsKind.EXPENSE,0,null,1,BigDecimal("1222256.9"),"بانک بلو")
        approve(row,BigDecimal("22000"),BankSmsKind.EXPENSE,0,null,1,BigDecimal("1222256.9"),"بانک بلو")
        val bank = db.walletDao().getAllWallets().first().single { it.name == "بانک بلو" }
        assertEquals(1222256.9,bank.balance,0.0)
        assertEquals(1,db.transactionDao().getAllTransactionsSync().size)
        val next = candidate("بلو\nواریز:100 ریال\nمانده:12,222,669 ریال").copy(receivedAt = row.receivedAt + 1000)
        approve(next,BigDecimal("10"),BankSmsKind.INCOME,0,null,1,BigDecimal("1222266.9"),"بانک بلو")
        assertEquals(1,db.walletDao().getAllWallets().first().count { it.name == "بانک بلو" })
        assertEquals(1222266.9,db.walletDao().getWalletById(bank.id)!!.balance,0.0)
    }
    @Test fun failedApprovalRollsBackAutomaticAccountCreation() = runBlocking {
        val row = candidate("بلو\nخرید100ریال")
        assertTrue(runCatching { ImportBankSms(db,inbox)(row,BigDecimal("10"),BankSmsKind.EXPENSE,0,null,1,BigDecimal("-1"),"بانک بلو") }.isFailure)
        assertFalse(db.walletDao().getAllWallets().first().any { it.name == "بانک بلو" })
        assertTrue(db.transactionDao().getAllTransactionsSync().isEmpty())
    }
    @Test fun transferCanCreateTheMissingBankAccountWithinApproval() = runBlocking {
        val row = candidate("بلو\nانتقال وجه مبلغ250000ریال\nواریز\nمانده:800000ریال")
        ImportBankSms(db,inbox)(row,BigDecimal("25000"),BankSmsKind.TRANSFER,10,0,1,BigDecimal("80000"),"بانک بلو",true)
        val bank = db.walletDao().getAllWallets().first().single { it.name == "بانک بلو" }
        assertEquals(475000.0,db.walletDao().getWalletById(10)!!.balance,0.0)
        assertEquals(80000.0,bank.balance,0.0)
        assertEquals(bank.id,db.transactionDao().getAllTransactionsSync().single().toWalletId)
    }
    @Test fun dailyQueryIncludesBothMidnightsCorrectly() = runBlocking {
        val start = LocalDate.parse("2026-10-26").atStartOfDay(ZoneId.of("Asia/Tehran")).toInstant().toEpochMilli()
        val next = LocalDate.parse("2026-10-27").atStartOfDay(ZoneId.of("Asia/Tehran")).toInstant().toEpochMilli()
        listOf(start-1,start,next-1,next).forEach { timestamp -> db.transactionDao().insertTransaction(TransactionEntity(amount = 100.0,type = TransactionType.INCOME,
            categoryName = "آزمایش",walletId = 10,toWalletId = null,memberId = 1,date = timestamp,note = "روز")) }
        assertEquals(listOf(next-1,start),db.transactionDao().getTransactionsByDateRangeSync(start,next-1).map { it.date })
    }
}
