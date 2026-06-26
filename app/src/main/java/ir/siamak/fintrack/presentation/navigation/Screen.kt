package ir.siamak.fintrack.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * تعریف مقاصد (Destinations) اپلیکیشن با استفاده از Serialization برای امنیت تایپ.
 */
sealed class Screen {

    @Serializable
    object Dashboard : Screen()

    @Serializable
    data class AddEditWallet(val walletId: Long? = null) : Screen()

    @Serializable
    object WalletList : Screen()

    // بعداً صفحات تراکنش و اعضا را اینجا اضافه می‌کنیم
}
