package ir.siamak.fintrack.personalaccountant.presentation.security.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ir.siamak.fintrack.personalaccountant.presentation.security.lock.PinDots
import ir.siamak.fintrack.personalaccountant.presentation.security.lock.PinPad

@Composable
fun PinSetupDialog(
    state: PinSetupState,
    onEvent: (PinSetupEvent) -> Unit,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onSuccess()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val title = if (state.step == PinSetupStep.ENTER_NEW)
                    "رمز عبور جدید را وارد کنید" else "تأیید رمز عبور"

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                val currentPinLength = if (state.step == PinSetupStep.ENTER_NEW)
                    state.firstPin.length else state.secondPin.length

                PinDots(enteredLength = currentPinLength, totalLength = 4)

                Spacer(modifier = Modifier.height(16.dp))

                state.errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } ?: Spacer(modifier = Modifier.height(20.dp))

                Spacer(modifier = Modifier.height(24.dp))

                PinPad(
                    isEnabled = true,
                    onDigitClick = { onEvent(PinSetupEvent.DigitClicked(it)) },
                    onBackspaceClick = { onEvent(PinSetupEvent.BackspaceClicked) }
                )
            }
        }
    }
}

