package ir.siamak.fintrack.personalaccountant.presentation.report.components.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.personalaccountant.data.model.Member
import ir.siamak.fintrack.personalaccountant.data.model.Wallet
import ir.siamak.fintrack.personalaccountant.domain.report.model.ReportFilter

@Composable
fun ReportFilterSection(
    filter: ReportFilter,
    members: List<Member>,
    wallets: List<Wallet>,
    onFilterChanged: (ReportFilter) -> Unit
) {

    Column() {

        Text("فیلتر گزارش")

        Spacer(Modifier.height(8.dp))

        // فعلاً ساده نگه داشتیم
        Text("اینجا فیلتر عضو / حساب / تاریخ میاد")
    }
}
