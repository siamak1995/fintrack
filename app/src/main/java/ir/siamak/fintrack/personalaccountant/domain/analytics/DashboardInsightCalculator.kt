package ir.siamak.fintrack.personalaccountant.domain.analytics

import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.abs

class DashboardInsightCalculator @Inject constructor() {

    fun generate(
        transactions: List<Transaction>
    ): String {

        if (transactions.isEmpty()) {
            return "اولین تراکنش خود را ثبت کنید تا تحلیل مالی نمایش داده شود."
        }

        val monthly = transactions.filter { it.isCurrentMonth() }

        if (monthly.isEmpty()) {
            return "در این ماه هنوز تراکنشی ثبت نشده است."
        }

        val income = monthly
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val expense = monthly
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val balance = income - expense

        val expensePercent =
            if (income == 0.0)
                100
            else
                ((expense / income) * 100).toInt()

        return when {

            balance > 0 && expensePercent < 40 ->
                "عملکرد مالی شما عالی است. کمتر از ۴۰٪ درآمد این ماه خرج شده است."

            balance > 0 && expensePercent < 70 ->
                "وضعیت مالی مناسب است. روند فعلی را ادامه دهید."

            balance > 0 ->
                "هزینه‌ها رو به افزایش هستند. بهتر است مخارج غیرضروری را کنترل کنید."

            balance == 0.0 ->
                "درآمد و هزینه این ماه برابر است."

            abs(balance) > income * 0.30 ->
                "کسری بودجه قابل توجهی مشاهده می‌شود. مدیریت هزینه‌ها پیشنهاد می‌شود."

            else ->
                "هزینه‌های این ماه از درآمد بیشتر شده است."
        }

    }

    private fun Transaction.isCurrentMonth(): Boolean {

        val date = Instant
            .ofEpochMilli(this.date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return YearMonth.from(date) == YearMonth.now()

    }

}
