package ir.siamak.fintrack.presentation.dashboard


//Event:
//لیست کارهایی است که کاربر می‌تواند انجام دهد. (مثلاً: دکمه افزودن هزینه کلیک شد، یا صفحه رفرش شد.)
sealed interface DashboardEvent {

    data object LoadData : DashboardEvent

}