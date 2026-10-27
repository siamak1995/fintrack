package ir.siamak.fintrack.personalaccountant.presentation.components

import androidx.compose.runtime.Composable
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import ir.siamak.fintrack.personalaccountant.data.model.*
import ir.siamak.fintrack.personalaccountant.domain.analytics.*
import ir.siamak.fintrack.store.presentation.components.*

@Composable fun LoanTransactionFields(loans: List<Installment>,type: TransactionType,selectedLoanId: Long?,
    tagNames: List<String>,onSelected: (Long?) -> Unit) {
    if(type == TransactionType.TRANSFER) {
        Text("انتقال بین حساب‌های خودتان درآمد یا هزینه نیست و مانده وام را کم نمی‌کند. پرداخت به بانک را به‌صورت هزینه ثبت کنید.")
        return
    }
    val required = tagNames.any(::isLoanTag)
    val available = loans.filter { type == TransactionType.INCOME || it.paidAmount < it.totalAmount || it.id == selectedLoanId }
    ChoiceEntry(if(required) "وام مربوط *" else "اتصال به وام (اختیاری)",
        loans.firstOrNull { it.id == selectedLoanId }?.title ?: "بدون اتصال به وام",
        listOf(null to "بدون اتصال به وام") + available.map { it.id to it.title },onSelected)
    if(required && available.isEmpty()) Text("ابتدا وام مربوط را در بخش وام و اقساط ثبت کنید.")
    loans.firstOrNull { it.id == selectedLoanId }?.let { loan ->
        DataRow("مانده فعلی وام",quantity(loanSchedule(loan).remainingAmount) + " تومان")
        Text(if(type == TransactionType.EXPENSE) "این مبلغ هم به‌عنوان هزینه ثبت می‌شود و هم از مانده همین وام کم می‌شود؛ فقط یک بار از حساب برداشت می‌شود."
            else "این واریز به‌عنوان دریافت وام ثبت می‌شود و مبلغ پرداخت‌شده اقساط را افزایش نمی‌دهد.")
    }
}
