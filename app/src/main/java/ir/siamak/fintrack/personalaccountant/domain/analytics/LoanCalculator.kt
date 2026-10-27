package ir.siamak.fintrack.personalaccountant.domain.analytics

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.time.LocalDate
import ir.siamak.fintrack.common.core.datepicker.calendar.JalaliDateConverter
import ir.siamak.fintrack.common.core.datepicker.calendar.JalaliCalendarEngine
import ir.siamak.fintrack.common.core.datepicker.model.PersianDate

data class LoanCalculation(val payment: BigDecimal, val total: BigDecimal, val interest: BigDecimal)

enum class LoanInterestMethod(val label: String) {
    AMORTIZED("اقساط مساوی با نرخ سالانه"), SIMPLE("سود ساده روی اصل وام"), CONTRACT("مبالغ طبق قرارداد بانک")
}

object LoanCalculator {
    private val precision = MathContext.DECIMAL128
    val limit = BigDecimal("9007199254740991")
    /** Fixed monthly payment, nominal annual percent / 12. Contract fees are entered manually. */
    fun calculate(principal: BigDecimal, annualPercent: BigDecimal, months: Int, method: LoanInterestMethod = LoanInterestMethod.AMORTIZED): LoanCalculation {
        require(method != LoanInterestMethod.CONTRACT) { "مبالغ قرارداد باید مستقیماً وارد شوند" }
        require(principal.signum() > 0 && principal <= limit) { "اصل وام نامعتبر است" }
        require(annualPercent >= BigDecimal.ZERO && annualPercent <= BigDecimal("100")) { "درصد سالانه باید بین صفر و صد باشد" }
        require(months in 1..600) { "تعداد اقساط باید بین یک و ۶۰۰ باشد" }
        val rate = annualPercent.divide(BigDecimal("1200"),precision)
        val payment = if(method == LoanInterestMethod.SIMPLE) {
            principal.multiply(BigDecimal.ONE.add(rate.multiply(BigDecimal(months))),precision).divide(BigDecimal(months),precision)
        } else if(rate.signum() == 0) principal.divide(BigDecimal(months),precision) else {
            val growth = BigDecimal.ONE.add(rate).pow(months,precision)
            principal.multiply(rate,precision).multiply(growth,precision).divide(growth.subtract(BigDecimal.ONE),precision)
        }
        // Keep total principal exact at zero interest; final instalment absorbs rounding.
        val total = if(rate.signum() == 0) principal else payment.multiply(BigDecimal(months),precision).setScale(2,RoundingMode.HALF_UP)
        require(total <= limit) { "کل بازپرداخت خارج از محدوده مجاز است" }
        return LoanCalculation(payment.setScale(2,RoundingMode.HALF_UP),total,total.subtract(principal))
    }
}

/** Keep a Persian monthly due date on the same Persian day, clamping only short months. */
fun loanDueDate(first: LocalDate, months: Int): LocalDate {
    val original = JalaliDateConverter.fromGregorian(first)
    val index = original.year * 12 + original.month - 1 + months
    val year = Math.floorDiv(index,12)
    val month = Math.floorMod(index,12)+1
    return JalaliDateConverter.toGregorian(PersianDate(year,month,
        original.day.coerceAtMost(JalaliCalendarEngine.getDaysInMonth(year,month))))
}

fun isLoanTag(name: String): Boolean = Regex("وام|قسط|اقساط").containsMatchIn(name)
