package ir.siamak.fintrack.presentation.dashboard


//State:
//نگهدارنده “داده‌های خام” صفحه است. (مثلاً: آیا لودینگ است؟ موجودی حساب چقدر است؟)

data class DashboardState(

    val isLoading: Boolean = false

)