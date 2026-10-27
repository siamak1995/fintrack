package ir.siamak.fintrack.personalaccountant.banksms

import ir.siamak.fintrack.personalaccountant.data.local.entity.TransactionEntity
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import java.math.BigDecimal

object BankSmsBalances {
    fun resulting(walletId: Long, current: BigDecimal, change: BigDecimal, timestamp: Long,
        reported: BigDecimal?, history: List<TransactionEntity>): BigDecimal {
        val marker = "[FinTrackSmsBalance:$walletId]"
        val rows = history.filter { it.walletId == walletId || it.toWalletId == walletId }
        // The second note line is metadata; never interpret the original SMS text as metadata.
        if(rows.any { it.date > timestamp && it.note.lineSequence().drop(1).firstOrNull() == marker }) return current
        if(reported == null) return current.add(change)
        require(reported.signum() >= 0) { "مانده نامعتبر است" }
        return rows.filter { !it.isDeleted && it.date > timestamp }.fold(reported) { total,row ->
            val value = BigDecimal.valueOf(row.amount)
            total.add(if(row.toWalletId == walletId && row.type == TransactionType.TRANSFER) value
                else if(row.type == TransactionType.INCOME) value else value.negate())
        }
    }
}
