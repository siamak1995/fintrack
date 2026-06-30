package ir.siamak.fintrack.domain.analytics

import ir.siamak.fintrack.data.model.Installment

/**
 * محاسبات مربوط به اقساط.
 *
 * این کلاس برای محاسبه موارد زیر استفاده می‌شود:
 *
 * - مبلغ باقی مانده
 * - درصد پرداخت شده
 * - وضعیت قسط
 */
class InstallmentCalculator {

    /**
     * محاسبه مبلغ باقی مانده قسط.
     */
    fun remainingAmount(
        installment: Installment
    ): Double {
        return installment.totalAmount - installment.paidAmount
    }

    /**
     * محاسبه درصد پرداخت قسط.
     */
    fun paymentProgress(
        installment: Installment
    ): Float {

        if (installment.totalAmount == 0.0) {
            return 0f
        }

        return (
                installment.paidAmount /
                        installment.totalAmount
                ).toFloat()
    }

    /**
     * آیا قسط کامل پرداخت شده است؟
     */
    fun isPaid(
        installment: Installment
    ): Boolean {

        return installment.paidAmount >= installment.totalAmount
    }
}
