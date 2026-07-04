package ir.siamak.fintrack.presentation.report.pages.member

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
 * Member Report screen.
 *
 * این صفحه برای نمایش گزارش‌های مربوط به اعضای خانواده یا کاربران استفاده می‌شود.
 *
 * @param onBackClick callback برای بازگشت به صفحه قبل
 */
@Composable
fun MemberReportScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Member Report")
    }
}
