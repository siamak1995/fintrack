package ir.siamak.fintrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import ir.siamak.fintrack.presentation.wallet.WalletRoute
import ir.siamak.fintrack.presentation.theme.FinTrackTheme

/**
 * اکتیویتی اصلی برنامه.
 *
 * این اکتیویتی نقطه ورود رابط کاربری Compose است
 * و اولین صفحه برنامه را نمایش می‌دهد.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FinTrackTheme {
                WalletRoute()
            }
        }
    }
}
