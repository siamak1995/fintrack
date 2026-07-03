package ir.siamak.fintrack.presentation.landing.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StartButton(
    onClick: () -> Unit
) {

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {

        Text(
            text = "ورود به برنامه",
            style = MaterialTheme.typography.titleMedium
        )

    }

}