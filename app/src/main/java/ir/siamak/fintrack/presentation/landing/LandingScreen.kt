package ir.siamak.fintrack.presentation.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
    viewModel: LandingViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
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

        Button(
            modifier = Modifier.padding(top = 32.dp),
            onClick = onEnterDashboard
        ) {
            Text("ورود به حسابدار")
        }

        //@TODO - phase -2
//        Button(
//            modifier = Modifier.padding(top = 32.dp),
//            onClick = onEnterDashboard
//        ) {
//            Text("ورود به حسابدار خانه")
//        }
//
//        Button(
//            modifier = Modifier.padding(top = 32.dp),
//            onClick = onEnterDashboard
//        ) {
//            Text("ورود به حسابدار فروشگاه")
//        }

    }

}