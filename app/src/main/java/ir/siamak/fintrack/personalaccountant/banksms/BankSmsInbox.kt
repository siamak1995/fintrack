package ir.siamak.fintrack.personalaccountant.banksms

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable data class BankSmsCandidate(val id: String, val body: String, val sender: String, val receivedAt: Long, val parsed: ParsedBankSms,
    val automatic: Boolean = sender != "ورود دستی")
@Serializable data class BankSmsInboxState(val enabled: Boolean = false, val pending: List<BankSmsCandidate> = emptyList(), val reviewed: List<String> = emptyList(),
    val blockedSenders: List<String> = emptyList())

object BankSmsSenders {
    fun key(sender: String): String {
        val normalized = ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter.normalize(sender)
            .replace('ي','ی').replace('ك','ک').lowercase(java.util.Locale.ROOT)
            .replace(Regex("[\\s\\u200c\\u200e\\u200f\\u202a-\\u202e\\u2066-\\u2069\\ufeff]"), "")
        val numeric = normalized.replace(Regex("[+().-]"), "")
        return if(numeric.isNotEmpty() && numeric.all { it in '0'..'9' }) numeric.removePrefix("00") else normalized
    }
    fun isBlocked(sender: String, blocked: List<String>) = key(sender).let { value ->
        value.isNotEmpty() && blocked.any { key(it) == value }
    }
}

@Singleton class BankSmsInbox @Inject constructor(private val preferences: DataStore<Preferences>) {
    private val key = stringPreferencesKey("bank_sms_review_v1")
    val state = preferences.data.map {
        val saved = it[key]?.let { json -> Json.decodeFromString<BankSmsInboxState>(json) } ?: BankSmsInboxState()
        clean(saved)
    }
    private fun clean(saved: BankSmsInboxState) = saved.copy(pending = saved.pending.mapNotNull { row ->
        if(BankSmsSenders.isBlocked(row.sender,saved.blockedSenders)) return@mapNotNull null
        val parsed = BankSmsParser.parse(row.body) ?: return@mapNotNull null
        if(row.automatic && !BankSmsParser.isBankTransaction(row.body,parsed)) null else row.copy(parsed = parsed)
    })
    private suspend fun change(block: (BankSmsInboxState) -> BankSmsInboxState) {
        preferences.edit { values ->
            val old = values[key]?.let { Json.decodeFromString<BankSmsInboxState>(it) } ?: BankSmsInboxState()
            values[key] = Json.encodeToString(block(clean(old)))
        }
    }
    suspend fun setEnabled(value: Boolean) = change { it.copy(enabled = value) }
    suspend fun capture(body: String, sender: String, timestamp: Long, automatic: Boolean): Boolean {
        if(automatic && !state.first().enabled) return false
        if(body.length > 4000) return false
        val parsed = BankSmsParser.parse(body) ?: return false
        if(automatic && !BankSmsParser.isBankTransaction(body,parsed)) return false
        val candidate = BankSmsCandidate(BankSmsParser.fingerprint(body),body,sender,timestamp,parsed,automatic)
        var added = false
        change { old ->
            if((automatic && !old.enabled) || BankSmsSenders.isBlocked(sender,old.blockedSenders) || candidate.id in old.reviewed || old.pending.any { it.id == candidate.id } || old.pending.size >= 200) old
            else { added = true; old.copy(pending = old.pending + candidate) }
        }
        return added
    }
    suspend fun reviewed(id: String) = change { it.copy(pending = it.pending.filterNot { row -> row.id == id }, reviewed = (it.reviewed + id).distinct().takeLast(2000)) }
    suspend fun discard(id: String, blockSender: Boolean) = change { old ->
        val candidate = old.pending.firstOrNull { it.id == id } ?: return@change old
        val blocked = if(blockSender && candidate.automatic && BankSmsSenders.key(candidate.sender).isNotEmpty() &&
            !BankSmsSenders.isBlocked(candidate.sender,old.blockedSenders)) old.blockedSenders + candidate.sender else old.blockedSenders
        val removed = old.pending.filter { it.id == id || BankSmsSenders.isBlocked(it.sender,blocked) }
        old.copy(blockedSenders = blocked,pending = old.pending - removed.toSet(),
            reviewed = (old.reviewed + removed.map { it.id }).distinct().takeLast(2000))
    }
    suspend fun unblock(sender: String) = change { old ->
        old.copy(blockedSenders = old.blockedSenders.filterNot { BankSmsSenders.key(it) == BankSmsSenders.key(sender) })
    }
}
