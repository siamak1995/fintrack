package ir.siamak.fintrack.presentation.report.pages.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Wallet Report screen.
 *
 * این صفحه برای نمایش گزارش‌های مربوط به کیف پول استفاده می‌شود.
 * در آینده می‌توان این صفحه را به داده‌های واقعی، نمودارها و فیلترها وصل کرد.
 *
 * @param onBackClick callback برای بازگشت به صفحه قبل
 */
@Composable
fun WalletReportScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Wallet Report")
    }
}
