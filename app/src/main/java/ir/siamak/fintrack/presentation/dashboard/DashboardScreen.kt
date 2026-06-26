package ir.siamak.fintrack.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

//Screen:
//لایه نمایش است. فقط وظیفه دارد State را نشان دهد و وقتی کاربر تعاملی کرد، Event ارسال کند.


//@Composable:
// به کامپایلر می‌گوید این تابع برای ساختن رابط کاربری (UI) است.
// توابع کامپوزبل مثل آجرهای لگو هستند که برای چیدن ظاهر اپلیکیشن کنار هم قرار می‌گیرند.

@Composable
fun DashboardScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "FinTrack",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.titleMedium
        )

    }
}