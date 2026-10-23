package ir.siamak.fintrack.store
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import ir.siamak.fintrack.MainActivity
import org.junit.Rule
import org.junit.Test

/** Exercises the real Hilt root and the landing entry point, not a mock nav graph. */
class StoreNavigationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun landingOpensStoreAndSalesWizard() {
        compose.onNodeWithText("ورود به حسابدار فروشگاه").performClick()
        compose.onNodeWithText("داشبورد فروشگاه").assertIsDisplayed()
        compose.onNodeWithText("ثبت فروش / فاکتورهای فروش").performScrollTo().performClick()
        compose.onNodeWithText("ثبت فروش جدید").performScrollTo().performClick()
        compose.onNodeWithText("نام مشتری *").assertIsDisplayed()
    }
}
