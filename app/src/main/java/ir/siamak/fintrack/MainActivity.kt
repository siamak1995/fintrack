package ir.siamak.fintrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ir.siamak.fintrack.presentation.navigation.NavGraph
import ir.siamak.fintrack.presentation.theme.FinTrackTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinTrackTheme {
                NavGraph()

            }
        }
    }
}