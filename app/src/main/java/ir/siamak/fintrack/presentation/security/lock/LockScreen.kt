package ir.siamak.fintrack.presentation.security.lock

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Application lock screen for PIN and biometric authentication.
 */
@Composable
fun LockScreen(
    state: LockState,
    onEvent: (LockEvent) -> Unit,
    onUnlocked: () -> Unit,
    onBiometricClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(enabled = true) {}

    LaunchedEffect(state.isUnlocked) {
        if (state.isUnlocked) {
            onUnlocked()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ورود به فین ترک",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        PinDots(
            enteredLength = state.enteredPin.length,
            totalLength = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        } else {
            Spacer(modifier = Modifier.height(20.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        PinPad(
            isEnabled = !state.isLoading,
            onDigitClick = { digit ->
                onEvent(LockEvent.DigitClicked(digit))
            },
            onBackspaceClick = {
                onEvent(LockEvent.BackspaceClicked)
            }
        )

        if (state.canUseBiometric) {
            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = {
                    onEvent(LockEvent.BiometricClicked)
                    onBiometricClick()
                },
                enabled = !state.isLoading
            ) {
                Icon(
                    imageVector = Icons.Rounded.Fingerprint,
                    contentDescription = "ورود با اثر انگشت"
                )
            }
        }
    }
}
