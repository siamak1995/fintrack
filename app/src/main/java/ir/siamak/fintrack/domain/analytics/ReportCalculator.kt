package ir.siamak.fintrack.domain.analytics

import ir.siamak.fintrack.data.model.Transaction
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.data.model.Wallet
import ir.siamak.fintrack.domain.report.CategoryReportItem
import ir.siamak.fintrack.domain.report.MonthlyReportItem
import ir.siamak.fintrack.domain.report.WalletReportItem

class ReportCalculator {

    fun totalIncome(
        transactions: List<Transaction>
    ): Double {

        return transactions
            .filter {
                it.type == TransactionType.INCOME
            }
            .sumOf {
                it.amount
            }

    }

    fun totalExpense(
        transactions: List<Transaction>
    ): Double {

        return transactions
            .filter {
                it.type == TransactionType.EXPENSE
            }
            .sumOf {
                it.amount
            }

    }

    fun totalSaving(
        transactions: List<Transaction>
    ): Double {

        return totalIncome(transactions) -
                totalExpense(transactions)

    }

    fun walletReport(
        wallets: List<Wallet>
    ): List<WalletReportItem> {

        val total =
            wallets.sumOf { it.balance }

        return wallets.map {

            WalletReportItem(

                walletName = it.name,

                balance = it.balance,

                percent =
                    if (total == 0.0)
                        0f
                    else
                        ((it.balance / total) * 100)
                            .toFloat()

            )

        }

    }

    fun categoryReport(
        transactions: List<Transaction>
    ): List<CategoryReportItem> {

        val total =
            totalExpense(transactions)

        return transactions

            .filter {

                it.type == TransactionType.EXPENSE

            }

            .groupBy {

                it.categoryName

            }

            .map {

                CategoryReportItem(

                    category = it.key,

                    amount =

                        it.value.sumOf {

                                transaction -> transaction.amount

                        },

                    percent =

                        if (total == 0.0)

                            0f

                        else

                            ((it.value.sumOf {

                                    transaction ->
                                transaction.amount

                            } / total) * 100)
                                .toFloat()

                )

            }

            .sortedByDescending {

                it.amount

            }

    }

    fun monthlyReport(

        transactions: List<Transaction>

    ): List<MonthlyReportItem> {

        return transactions

            .groupBy {

                java.text.SimpleDateFormat(
                    "yyyy/MM"
                ).format(java.util.Date(it.date))

            }

            .map {

                val income =

                    it.value

                        .filter {

                                transaction ->

                            transaction.type ==
                                    TransactionType.INCOME

                        }

                        .sumOf {

                                transaction ->

                            transaction.amount

                        }

                val expense =

                    it.value

                        .filter {

                                transaction ->

                            transaction.type ==
                                    TransactionType.EXPENSE

                        }

                        .sumOf {

                                transaction ->

                            transaction.amount

                        }

                MonthlyReportItem(

                    month = it.key,

                    income = income,

                    expense = expense,

                    saving = income - expense

                )

            }

            .sortedByDescending {

                it.month

            }

    }

}