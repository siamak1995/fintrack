package ir.siamak.fintrack.store
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.graphics.asAndroidBitmap
import ir.siamak.fintrack.MainActivity
import org.junit.Rule
import org.junit.Test

/** Exercises the real Hilt root and the landing entry point, not a mock nav graph. */
class StoreNavigationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun landingOpensStoreAndSalesWizard() {
        enterStore()
        compose.onNodeWithText("داشبورد فروشگاه").assertIsDisplayed()
        compose.onNodeWithText("ثبت فروش / فاکتورهای فروش").performScrollTo().performClick()
        compose.onNodeWithText("ثبت فروش جدید").performScrollTo().performClick()
        compose.onNodeWithText("نام مشتری *").assertIsDisplayed()
    }
    @Test fun storeReportsAndModuleSelectionRemainReachable() {
        enterStore()
        compose.onNodeWithText("گزارش‌ها و تحلیل فروشگاه").performScrollTo().performClick()
        compose.onNodeWithText("گزارش‌های فروشگاه").assertIsDisplayed()
        compose.waitUntil(15000) { compose.onAllNodesWithText("شاخص‌های فاکتورهای این بازه").fetchSemanticsNodes().isNotEmpty() }
        screenshot("reports.png")
        compose.onNodeWithText("بازگشت").performScrollTo().performClick()
        compose.onNodeWithText("داشبورد فروشگاه").assertIsDisplayed()
        screenshot("dashboard.png")
        compose.onNodeWithText("انتخاب حسابدار").performClick()
        compose.onNodeWithText("ورود به حسابدار").performScrollTo().assertIsDisplayed()
        screenshot("landing.png")
    }
    @Test fun backupIsAvailableFromStoreSettingsWithoutMixingDashboards() {
        enterStore()
        compose.onNodeWithContentDescription("تنظیمات و پشتیبان‌گیری").performClick()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("پشتیبان‌گیری و بازیابی"))
        compose.onNodeWithText("پشتیبان‌گیری و بازیابی").assertIsDisplayed()
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText("ذخیرهٔ نسخهٔ پشتیبان"))
        compose.onNodeWithText("ذخیرهٔ نسخهٔ پشتیبان").performClick()
        compose.onNodeWithText("محافظت از اطلاعات مالی").assertIsDisplayed()
        screenshot("backup-confirmation.png")
        compose.onNodeWithText("انصراف").performClick()
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val version = context.packageManager.getPackageInfo(context.packageName,0).versionName.orEmpty()
        val label = ir.siamak.fintrack.common.core.formatter.PersianNumberFormatter.toPersianDigits("نسخه برنامه: $version")
        compose.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(label))
        compose.onNodeWithText(label).assertIsDisplayed()
    }
    private fun screenshot(name: String) {
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        compose.waitForIdle()
        instrumentation.waitForIdleSync()
        instrumentation.uiAutomation.waitForIdle(200,5000)
        val appWindow = androidx.test.platform.app.InstrumentationRegistry.getArguments().getString("captureAppWindow") == "true"
        val directory = java.io.File(instrumentation.targetContext.cacheDir,if(appWindow) "sol-light-ui-app-window-qa" else "sol-light-ui-qa").apply { mkdirs() }
        val bitmap = requireNotNull(if(appWindow && name == "backup-confirmation.png") compose.onNode(isDialog()).captureToImage().asAndroidBitmap()
            else if(appWindow && android.os.Build.VERSION.SDK_INT >= 34) instrumentation.uiAutomation.takeScreenshot(compose.activity.window)
            else instrumentation.uiAutomation.takeScreenshot()) { "Screenshot unavailable" }
        java.io.File(directory,name).outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,it) }
        bitmap.recycle()
    }
    private fun enterStore() {
        compose.waitUntil(15000) { compose.onAllNodesWithText("ورود به حسابدار فروشگاه").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("ورود به حسابدار فروشگاه").performScrollTo().performClick()
    }
}
