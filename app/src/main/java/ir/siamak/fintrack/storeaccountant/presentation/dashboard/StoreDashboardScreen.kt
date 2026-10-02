package ir.siamak.fintrack.storeaccountant.presentation.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * صفحه اصلی داشبورد فروشگاه.
 */
@Composable
fun StoreDashboardScreen(
    viewModel: StoreDashboardViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "داشبورد حسابدار فروشگاه")
    }
}

