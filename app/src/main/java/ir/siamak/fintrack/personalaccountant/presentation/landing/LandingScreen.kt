package ir.siamak.fintrack.personalaccountant.presentation.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import ir.siamak.fintrack.common.core.formatter.PersianText as Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LandingScreen(
    onEnterDashboard: () -> Unit,
    onEnterStoreDashboard: () -> Unit,
    viewModel: LandingViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = state.userName,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = state.welcome,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = state.todaySummary,
            modifier = Modifier.padding(top = 12.dp)
        )

        Card(modifier = Modifier.padding(top = 32.dp).widthIn(max = 640.dp).fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("حسابدار شخصی",style = MaterialTheme.typography.titleLarge)
                Text("حساب‌ها، درآمد و هزینه، اعضا و گزارش‌های مالی شما")
                Button(onClick = onEnterDashboard, modifier = Modifier.fillMaxWidth()) { Text("ورود به حسابدار") }
            }
        }

        OutlinedCard(modifier = Modifier.padding(top = 16.dp).widthIn(max = 640.dp).fillMaxWidth()) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("حسابدار فروشگاه",style = MaterialTheme.typography.titleLarge)
                Text("فروش، موجودی، سفارش‌ها و فاکتورها؛ مستقل از حساب‌های شخصی")
                Button(onClick = onEnterStoreDashboard, modifier = Modifier.fillMaxWidth()) { Text("ورود به حسابدار فروشگاه") }
            }
        }

    }

}
