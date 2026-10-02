package ir.siamak.fintrack.personalaccountant.domain.analytics

import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.domain.report.CategoryReportItem
import ir.siamak.fintrack.personalaccountant.domain.report.MonthlyReportItem
import ir.siamak.fintrack.personalaccountant.domain.report.WalletReportItem
import ir.siamak.fintrack.personalaccountant.domain.report.model.AdvancedReport
import ir.siamak.fintrack.personalaccountant.domain.report.model.MemberFinancialReport
import ir.siamak.fintrack.personalaccountant.domain.report.model.ReportFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * موتور اصلی گزارشات FinTrack
 *
 * تمام محاسبات گزارشات فقط در این کلاس انجام می‌شود.
 *
 * هیچ ViewModel یا Screen اجازه انجام محاسبات ندارد.
 */
class ReportCalculator @Inject constructor() {

    //=========================================================
    // FILTER ENGINE
    //=========================================================

    fun filterTransactions(
        transactions: List<Transaction>,
        filter: ReportFilter
    ): List<Transaction> {

        return transactions.filter { transaction ->

            val dateOk =
                (filter.startDate == null || transaction.date >= filter.startDate) &&
                        (filter.endDate == null || transaction.date <= filter.endDate)

            val memberOk =
                filter.memberId == null ||
                        transaction.memberId == filter.memberId

            val walletOk =
                filter.walletId == null ||
                        transaction.walletId == filter.walletId

            val typeOk =

                (filter.includeIncome &&
                        transaction.type == TransactionType.INCOME)

                        ||

                        (filter.includeExpense &&
                                transaction.type == TransactionType.EXPENSE)

            dateOk &&
                    memberOk &&
                    walletOk &&
                    typeOk

        }

    }

    //=========================================================
    // BASE CALCULATIONS
    //=========================================================

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

    //=========================================================
    // WALLET REPORT
    //=========================================================

    fun walletReport(
        wallets: List<Wallet>
    ): List<WalletReportItem> {

        val totalBalance =
            wallets.sumOf { it.balance }

        return wallets.map { wallet ->

            WalletReportItem(

                walletName = wallet.name,

                balance = wallet.balance,

                percent =

                    if (totalBalance == 0.0)

                        0f

                    else

                        (
                                wallet.balance /
                                        totalBalance *
                                        100
                                ).toFloat()

            )

        }

            .sortedByDescending {

                it.balance

            }

    }

    //=========================================================
    // CATEGORY REPORT
    //=========================================================

    fun categoryReport(
        transactions: List<Transaction>
    ): List<CategoryReportItem> {

        val expenses =

            transactions.filter {

                it.type == TransactionType.EXPENSE

            }

        val totalExpense =

            totalExpense(expenses)

        return expenses

            .groupBy {

                it.categoryName

            }

            .map {

                val amount =

                    it.value.sumOf {

                            tx -> tx.amount

                    }

                CategoryReportItem(

                    category = it.key,

                    amount = amount,

                    percent =

                        if (totalExpense == 0.0)

                            0f

                        else

                            (

                                    amount /

                                            totalExpense *

                                            100

                                    ).toFloat()

                )

            }

            .sortedByDescending {

                it.amount

            }

    }

    //=========================================================
    // MONTHLY REPORT
    //=========================================================

    fun monthlyReport(
        transactions: List<Transaction>
    ): List<MonthlyReportItem> {

        return transactions

            .groupBy {

                SimpleDateFormat(

                    "yyyy/MM",

                    Locale.getDefault()

                ).format(

                    Date(it.date)

                )

            }

            .map { entry ->

                val income =

                    entry.value

                        .filter {

                            it.type ==
                                    TransactionType.INCOME

                        }

                        .sumOf {

                            it.amount

                        }

                val expense =

                    entry.value

                        .filter {

                            it.type ==
                                    TransactionType.EXPENSE

                        }

                        .sumOf {

                            it.amount

                        }

                MonthlyReportItem(

                    month = entry.key,

                    income = income,

                    expense = expense,

                    saving = income - expense

                )

            }

            .sortedByDescending {

                it.month

            }

    }
    //=========================================================
    // MEMBER REPORT
    //=========================================================

    fun reportByMember(
        transactions: List<Transaction>,
        members: List<Member>,
        filter: ReportFilter
    ): List<MemberFinancialReport> {

        val filtered = filterTransactions(
            transactions,
            filter
        )

        return members.map { member ->

            val memberTransactions =

                filtered.filter {

                    it.memberId == member.id

                }

            val income = totalIncome(memberTransactions)

            val expense = totalExpense(memberTransactions)

            MemberFinancialReport(

                memberId = member.id,

                memberName = member.name,

                income = income,

                expense = expense,

                balance = income - expense

            )

        }

            .filter {

                it.income != 0.0 ||
                        it.expense != 0.0

            }

            .sortedByDescending {

                it.balance

            }

    }

    //=========================================================
    // REPORT BY WALLET
    //=========================================================

    fun reportByWallet(

        transactions: List<Transaction>,

        wallets: List<Wallet>,

        filter: ReportFilter

    ): List<WalletReportItem> {

        val filtered =

            filterTransactions(

                transactions,

                filter

            )

        val total =

            filtered.sumOf {

                it.amount

            }

        return wallets.map { wallet ->

            val amount =

                filtered

                    .filter {

                        it.walletId == wallet.id

                    }

                    .sumOf {

                        it.amount

                    }

            WalletReportItem(

                walletName = wallet.name,

                balance = amount,

                percent =

                    if (total == 0.0)

                        0f

                    else

                        (

                                amount /

                                        total *

                                        100

                                ).toFloat()

            )

        }

            .filter {

                it.balance != 0.0

            }

            .sortedByDescending {

                it.balance

            }

    }

    //=========================================================
    // ADVANCED REPORT
    //=========================================================

    fun buildAdvancedReport(

        transactions: List<Transaction>,

        members: List<Member>,

        filter: ReportFilter

    ): AdvancedReport {

        val filtered =

            filterTransactions(

                transactions,

                filter

            )

        val income =

            totalIncome(filtered)

        val expense =

            totalExpense(filtered)

        return AdvancedReport(

            income = income,

            expense = expense,

            balance = income - expense,

            byMember =

                reportByMember(

                    transactions,

                    members,

                    filter

                )

        )

    }

    //=========================================================
    // QUICK STATISTICS
    //=========================================================

    fun transactionCount(

        transactions: List<Transaction>

    ): Int {

        return transactions.size

    }

    fun incomeCount(

        transactions: List<Transaction>

    ): Int {

        return transactions.count {

            it.type == TransactionType.INCOME

        }

    }

    fun expenseCount(

        transactions: List<Transaction>

    ): Int {

        return transactions.count {

            it.type == TransactionType.EXPENSE

        }

    }

    fun averageIncome(

        transactions: List<Transaction>

    ): Double {

        val incomes =

            transactions.filter {

                it.type == TransactionType.INCOME

            }

        if (incomes.isEmpty())

            return 0.0

        return incomes.sumOf {

            it.amount

        } / incomes.size

    }

    fun averageExpense(

        transactions: List<Transaction>

    ): Double {

        val expenses =

            transactions.filter {

                it.type == TransactionType.EXPENSE

            }

        if (expenses.isEmpty())

            return 0.0

        return expenses.sumOf {

            it.amount

        } / expenses.size

    }

    //=========================================================
    // MAX VALUES
    //=========================================================

    fun biggestIncome(

        transactions: List<Transaction>

    ): Transaction? {

        return transactions

            .filter {

                it.type == TransactionType.INCOME

            }

            .maxByOrNull {

                it.amount

            }

    }

    fun biggestExpense(

        transactions: List<Transaction>

    ): Transaction? {

        return transactions

            .filter {

                it.type == TransactionType.EXPENSE

            }

            .maxByOrNull {

                it.amount

            }

    }

    //=========================================================
    // READY FOR CHARTS
    //=========================================================

    fun incomeSeries(

        transactions: List<Transaction>

    ): List<Double> {

        return monthlyReport(transactions)

            .map {

                it.income

            }

    }

    fun expenseSeries(

        transactions: List<Transaction>

    ): List<Double> {

        return monthlyReport(transactions)

            .map {

                it.expense

            }

    }

    fun savingSeries(

        transactions: List<Transaction>

    ): List<Double> {

        return monthlyReport(transactions)

            .map {

                it.saving

            }

    }

}
