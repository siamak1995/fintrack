package ir.siamak.fintrack.personalaccountant.domain.report.usecases

import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.personalaccountant.domain.model.MemberReport
import ir.siamak.fintrack.personalaccountant.domain.repository.MemberRepository
import ir.siamak.fintrack.personalaccountant.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetMemberReportUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(startLong: Long?, endLong: Long?): List<MemberReport> {
        // ۱. دریافت اعضا
        val members = memberRepository.getAllMembers().first()

        // ۲. دریافت تراکنش‌ها (اگر تاریخ نبود، همه را می‌گیرد)
        val transactions = if (startLong != null && endLong != null) {
            transactionRepository.getTransactionsByDateRange(startLong, endLong)
        } else {
            transactionRepository.getAllTransactionsSync()
        }

        // ۳. گروه‌بندی تراکنش‌ها بر اساس memberId
        val groupedTransactions = transactions.groupBy { it.memberId }

        // ۴. تبدیل اعضا به گزارش مالی
        return members.map { member ->
            val memberTx = groupedTransactions[member.id] ?: emptyList()

            val income = memberTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val expense = memberTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

            MemberReport(
                memberId = member.id,
                memberName = member.name,
                totalIncome = income.toLong(),
                totalExpense = expense.toLong(),
                transactionCount = memberTx.size
            )
        }.filter { it.transactionCount > 0 } // فقط کسانی که فعالیت داشتند
    }
}

