package ir.siamak.fintrack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.AppSettingsViewModel
import ir.siamak.fintrack.personalaccountant.presentation.baseinfo.settings.LocalAppSettings
import ir.siamak.fintrack.personalaccountant.presentation.navigation.AppNavGraph
import ir.siamak.fintrack.personalaccountant.presentation.security.lock.LockEvent
import ir.siamak.fintrack.personalaccountant.presentation.security.lock.LockScreen
import ir.siamak.fintrack.personalaccountant.presentation.security.lock.LockViewModel
import ir.siamak.fintrack.personalaccountant.presentation.theme.FinTrackTheme
import ir.siamak.fintrack.common.security.BiometricAuthenticator
import javax.inject.Inject

/**
 * اکتیویتی اصلی برنامه.
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private var financeDestination by androidx.compose.runtime.mutableStateOf<String?>(null)
    override fun onNewIntent(intent: android.content.Intent) { super.onNewIntent(intent); setIntent(intent); financeDestination = intent.getStringExtra("finance_destination") }


    private val appSettingsViewModel: AppSettingsViewModel by viewModels()
    private val lockViewModel: LockViewModel by viewModels()

    @Inject
    lateinit var biometricAuthenticator: BiometricAuthenticator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        financeDestination = intent.getStringExtra("finance_destination")

        setContent {
            val restore by FinTrackApplication.restoreState.collectAsStateWithLifecycle()
            if(restore.checking || restore.error != null) {
                FinTrackTheme { Surface {
                    androidx.compose.foundation.layout.Column {
                        if(restore.checking) androidx.compose.material3.LinearProgressIndicator()
                        ir.siamak.fintrack.common.core.formatter.PersianText(restore.error ?: "بررسی ایمنی داده‌ها و بازیابی…")
                    }
                } }
                return@setContent
            }
            val settings by appSettingsViewModel.settings.collectAsStateWithLifecycle()
            val lockState by lockViewModel.state.collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalAppSettings provides settings
            ) {
                FinTrackTheme(
                    themeMode = settings.theme,
                    dynamicColor = settings.dynamicColor
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        if (lockState.shouldShowLockScreen && !lockState.isUnlocked) {
                            LockScreen(
                                state = lockState,
                                onEvent = lockViewModel::onEvent,
                                onUnlocked = { },
                                onBiometricClick = {
                                    biometricAuthenticator.authenticate(
                                        activity = this@MainActivity,
                                        title = "ورود به حسابدار",
                                        subtitle = "برای ورود، اثر انگشت خود را تأیید کنید",
                                        onSuccess = {
                                            lockViewModel.onEvent(LockEvent.BiometricSucceeded)
                                        },
                                        onError = { _, errString ->
                                            lockViewModel.onEvent(
                                                LockEvent.BiometricFailed(errString.toString())
                                            )
                                        },
                                        onFailed = {
                                            lockViewModel.onEvent(
                                                LockEvent.BiometricFailed("احراز هویت ناموفق بود")
                                            )
                                        }
                                    )
                                }
                            )
                        } else {
                            val navController = rememberNavController()
                            AppNavGraph(navController = navController)
                            androidx.compose.runtime.LaunchedEffect(financeDestination) {
                                when(financeDestination) {
                                    "loans" -> navController.navigate(ir.siamak.fintrack.personalaccountant.presentation.navigation.Screen.LoanReport)
                                    "sms" -> navController.navigate(ir.siamak.fintrack.personalaccountant.presentation.navigation.Screen.BankSms)
                                }
                                financeDestination = null
                            }
                        }
                    }
                }
            }
        }
    }
}

