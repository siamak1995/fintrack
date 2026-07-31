package ir.siamak.fintrack.presentation.baseinfo.settings.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.siamak.fintrack.presentation.baseinfo.settings.SettingsEvent
import ir.siamak.fintrack.presentation.baseinfo.settings.SettingsScreen
import ir.siamak.fintrack.presentation.baseinfo.settings.SettingsViewModel
import ir.siamak.fintrack.presentation.security.setup.PinSetupDialog
import ir.siamak.fintrack.presentation.security.setup.PinSetupViewModel
import ir.siamak.fintrack.security.BiometricAuthenticator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // بررسی پشتیبانی دستگاه از سنسور اثرانگشت
    LaunchedEffect(Unit) {
        val authenticator = BiometricAuthenticator()
        val isAvailable = authenticator.isBiometricAvailable(context)
        viewModel.onEvent(SettingsEvent.SetBiometricHardwareAvailable(isAvailable))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیمات برنامه") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SettingsScreen(
                state = state,
                onEvent = viewModel::onEvent
            )

            // نمایش دیالوگ راه‌اندازی پین در صورت نیاز
            if (state.showPinSetup) {
                val pinSetupViewModel: PinSetupViewModel = hiltViewModel()
                val pinSetupState by pinSetupViewModel.state.collectAsStateWithLifecycle()

                PinSetupDialog(
                    state = pinSetupState,
                    onEvent = pinSetupViewModel::onEvent,
                    onDismiss = { viewModel.onEvent(SettingsEvent.PinSetupDismissed) },
                    onSuccess = { viewModel.onEvent(SettingsEvent.PinSetupSuccess) }
                )
            }
        }
    }
}
