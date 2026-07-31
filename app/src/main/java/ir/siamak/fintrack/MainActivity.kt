package ir.siamak.fintrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.siamak.fintrack.presentation.baseinfo.settings.AppSettingsViewModel
import ir.siamak.fintrack.presentation.baseinfo.settings.LocalAppSettings
import ir.siamak.fintrack.presentation.navigation.AppNavGraph
import ir.siamak.fintrack.presentation.theme.FinTrackTheme

/**
 * اکتیویتی اصلی برنامه.
 *
 * این اکتیویتی نقطه ورود رابط کاربری Compose است و تنظیمات سراسری برنامه
 * را به کل درخت UI تزریق می‌کند.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appSettingsViewModel: AppSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val settings = appSettingsViewModel.settings.collectAsStateWithLifecycle().value

            CompositionLocalProvider(
                LocalAppSettings provides settings
            ) {
                FinTrackTheme(
                    themeMode = settings.theme,
                    dynamicColor = settings.dynamicColor
                ) {
                    val navController = rememberNavController()

                    Surface(color = MaterialTheme.colorScheme.background) {
                        AppNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
