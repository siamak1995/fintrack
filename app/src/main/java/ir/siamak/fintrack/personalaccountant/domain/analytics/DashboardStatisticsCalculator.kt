package ir.siamak.fintrack.personalaccountant.domain.analytics

import ir.siamak.fintrack.personalaccountant.data.model.Installment
import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.domain.dashboard.DashboardStatistics
import javax.inject.Inject

/**
 * مسئول محاسبه تمام آمارهای داشبورد
 */
class DashboardStatisticsCalculator @Inject constructor() {

    fun calculate(

        wallets: List<Wallet>,

        members: List<Member>,

        installments: List<Installment>,

        transactions: List<Transaction>

    ): DashboardStatistics {

        return DashboardStatistics(

            walletCount = wallets.size,

            memberCount = members.size,

            transactionCount = transactions.size,

            installmentCount = installments.size

        )

    }

    /**
     * مجموع دارایی کیف پول‌ها
     */
    fun totalWalletBalance(

        wallets: List<Wallet>

    ): Double {

        return wallets.sumOf {

            it.balance

        }

    }

    /**
     * تعداد تراکنش‌های درآمد
     */
    fun incomeCount(

        transactions: List<Transaction>

    ): Int {

        return transactions.count {

            it.type == TransactionType.INCOME

        }

    }

    /**
     * تعداد تراکنش‌های هزینه
     */
    fun expenseCount(

        transactions: List<Transaction>

    ): Int {

        return transactions.count {

            it.type == TransactionType.EXPENSE

        }

    }

    /**
     * میانگین مبلغ تراکنش
     */
    fun averageTransaction(

        transactions: List<Transaction>

    ): Double {

        if (transactions.isEmpty()) return 0.0

        return transactions.sumOf {

            it.amount

        } / transactions.size

    }

    /**
     * بیشترین مبلغ تراکنش
     */
    fun maxTransaction(

        transactions: List<Transaction>

    ): Double {

        return transactions.maxOfOrNull {

            it.amount

        } ?: 0.0

    }

    /**
     * کمترین مبلغ تراکنش
     */
    fun minTransaction(

        transactions: List<Transaction>

    ): Double {

        return transactions.minOfOrNull {

            it.amount

        } ?: 0.0

    }

}
