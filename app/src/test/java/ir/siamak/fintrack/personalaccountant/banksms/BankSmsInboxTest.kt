package ir.siamak.fintrack.personalaccountant.banksms

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.nio.file.Files
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class BankSmsInboxTest {
    private val transaction = "بانک نمونه\nحساب: 12345678\nواریز: 100000 ریال\nمانده: 900000 ریال"

    private fun withInbox(test: suspend (BankSmsInbox, androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences>) -> Unit) = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val file = Files.createTempFile("fintrack-sms", ".preferences_pb").toFile()
        val store = PreferenceDataStoreFactory.create(scope = scope) { file }
        try { test(BankSmsInbox(store),store) }
        finally { scope.cancel(); scope.coroutineContext.job.join(); file.delete() }
    }

    @Test fun automaticCaptureRequiresOptInAndBankTransactionEvidence() = withInbox { inbox,_ ->
        assertFalse(inbox.capture(transaction,"Bank",1,true))
        inbox.setEnabled(true)
        assertFalse(inbox.capture("خرید: 100000 تومان با تخفیف ویژه","Ad",2,true))
        assertFalse(inbox.capture("بانک نمونه\nخرید: 100000 ریال","Ad",3,true))
        assertTrue(inbox.capture(transaction,"Bank",4,true))
        assertEquals(1,inbox.state.first().pending.size)
    }

    @Test fun blockingPersistsAndRemovesAllPendingFromThatSenderOnly() = withInbox { inbox,store ->
        inbox.setEnabled(true)
        assertTrue(inbox.capture(transaction,"+98 5000",1,true))
        assertTrue(inbox.capture(transaction.replace("100000","200000"),"۰۰۹۸۵۰۰۰",2,true))
        assertTrue(inbox.capture(transaction.replace("100000","300000"),"+98 5001",3,true))
        inbox.discard(inbox.state.first().pending.first().id,true)
        val reopened = BankSmsInbox(store)
        assertEquals(listOf("+98 5000"),reopened.state.first().blockedSenders)
        assertEquals(listOf("+98 5001"),reopened.state.first().pending.map { it.sender })
        assertFalse(reopened.capture(transaction.replace("100000","400000"),"00985000",4,true))
        assertEquals(1,reopened.state.first().pending.size)
    }

    @Test fun unblockingAllowsNewMessagesWithoutRestoringDiscardedOnes() = withInbox { inbox,store ->
        inbox.setEnabled(true)
        inbox.capture(transaction,"Bank",1,true)
        inbox.discard(inbox.state.first().pending.single().id,true)
        BankSmsInbox(store).unblock(" bank ")
        assertTrue(inbox.state.first().blockedSenders.isEmpty())
        assertTrue(inbox.state.first().pending.isEmpty())
        assertFalse(inbox.capture(transaction,"Bank",2,true))
        assertTrue(inbox.capture(transaction.replace("100000","200000"),"BANK",3,true))
    }

    @Test fun ignoringOneMessageDoesNotBlockTheSender() = withInbox { inbox,_ ->
        inbox.setEnabled(true)
        inbox.capture(transaction,"Bank",1,true)
        inbox.discard(inbox.state.first().pending.single().id,false)
        assertTrue(inbox.state.first().blockedSenders.isEmpty())
        assertTrue(inbox.capture(transaction.replace("100000","200000"),"Bank",2,true))
    }

    @Test fun manualEntryStillAllowsAmbiguousMessagesAndCannotBlockManualSource() = withInbox { inbox,_ ->
        assertTrue(inbox.capture("بانک نمونه خرید 100 ریال","ورود دستی",1,false))
        inbox.discard(inbox.state.first().pending.single().id,true)
        assertTrue(inbox.state.first().blockedSenders.isEmpty())
    }

    @Test fun legacyQueueFiltersAdsButKeepsTransactionsAndManualEntries() = withInbox { inbox,store ->
        val ad = "بانک نمونه خرید 100 ریال با تخفیف ویژه"
        val legacy = """{"enabled":true,"pending":[{"id":"ad","body":"$ad","sender":"Ads","receivedAt":1,"parsed":${Json.encodeToString(BankSmsParser.parse(ad)!!)}}],"reviewed":[]}"""
        store.edit { it[stringPreferencesKey("bank_sms_review_v1")] = legacy }
        assertTrue(inbox.state.first().pending.isEmpty())
        assertTrue(inbox.capture(transaction,"Bank",2,true))
        assertTrue(inbox.capture("بانک نمونه خرید 100 ریال","ورود دستی",3,false))
        assertEquals(2,inbox.state.first().pending.size)
        assertFalse(store.data.first()[stringPreferencesKey("bank_sms_review_v1")]!!.contains("Ads"))
    }

    @Test fun senderMatchingUsesWholeAddressAndNormalizesDigitsAndCase() {
        assertTrue(BankSmsSenders.isBlocked("۰۰۹۸۵۰۰۰",listOf("+98 5000")))
        assertTrue(BankSmsSenders.isBlocked(" BANK ",listOf("Bank")))
        assertFalse(BankSmsSenders.isBlocked("9850001",listOf("985000")))
        assertFalse(BankSmsSenders.isBlocked("",listOf("")))
    }
}
