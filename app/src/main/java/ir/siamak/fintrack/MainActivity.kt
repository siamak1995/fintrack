package ir.siamak.fintrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.siamak.fintrack.presentation.navigation.NavGraph
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
        setContent {
            FinTrackTheme {
                val navController = rememberNavController()
                Surface(color = MaterialTheme.colorScheme.background) {
                    NavGraph(navController = navController)
                }
            }
        }
    }
}

