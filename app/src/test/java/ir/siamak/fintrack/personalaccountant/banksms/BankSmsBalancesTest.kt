package ir.siamak.fintrack.personalaccountant.banksms

import ir.siamak.fintrack.personalaccountant.data.local.entity.TransactionEntity
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Test

class BankSmsBalancesTest {
    private fun row(date: Long,type: TransactionType,amount: Double,source: Long = 1,target: Long? = null,note: String = "") =
        TransactionEntity(amount = amount,type = type,categoryName = "test",walletId = source,toWalletId = target,memberId = 1,date = date,note = note)
    private fun balance(reported: String?,rows: List<TransactionEntity> = emptyList()) = BankSmsBalances.resulting(1,
        BigDecimal("1000"),BigDecimal("-100"),100,reported?.let(::BigDecimal),rows)
    @Test fun statementBalanceIsNotAddedToTransaction() { assertEquals(0,balance("500").compareTo(BigDecimal("500"))) }
    @Test fun absentStatementUsesTransactionDelta() { assertEquals(0,balance(null).compareTo(BigDecimal("900"))) }
    @Test fun zeroBalanceIsValid() { assertEquals(0,balance("0").signum()) }
    @Test fun laterOperationsRemainInBalance() {
        val rows = listOf(row(101,TransactionType.INCOME,50.0),row(102,TransactionType.EXPENSE,20.0),
            row(103,TransactionType.TRANSFER,40.0,2,1),row(104,TransactionType.TRANSFER,10.0,1,2),row(99,TransactionType.EXPENSE,500.0))
        assertEquals(0,balance("500",rows).compareTo(BigDecimal("560")))
    }
    @Test fun olderSmsDoesNotOverwriteOrDoubleCountNewerStatement() {
        val rows = listOf(row(101,TransactionType.EXPENSE,10.0,note = "[FinTrackSms:abc]\n[FinTrackSmsBalance:1]\noriginal"))
        assertEquals(0,balance("500",rows).compareTo(BigDecimal("1000")))
        assertEquals(0,balance(null,rows).compareTo(BigDecimal("1000")))
    }
    @Test fun bodyTextCannotImpersonateStatementMetadata() {
        val rows = listOf(row(101,TransactionType.EXPENSE,10.0,note = "[FinTrackSms:abc]\noriginal\n[FinTrackSmsBalance:1]"))
        assertEquals(0,balance("500",rows).compareTo(BigDecimal("490")))
    }
    @Test fun deletedFutureOperationDoesNotChangeStatement() {
        assertEquals(0,balance("500",listOf(row(101,TransactionType.INCOME,50.0).copy(isDeleted = true))).compareTo(BigDecimal("500")))
    }
    @Test fun sameMinuteStatementsUseTheMostRecentlyConfirmedBalance() {
        val rows = listOf(row(100,TransactionType.EXPENSE,10.0,note = "[FinTrackSms:abc]\n[FinTrackSmsBalance:1]\noriginal"))
        assertEquals(0,balance("500",rows).compareTo(BigDecimal("500")))
    }
}
