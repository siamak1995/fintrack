package ir.siamak.fintrack.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.domain.usecase.wallet.GetAllWalletsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ویومدل صفحه داشبورد.
 *
 * این کلاس مسئول مدیریت منطق صفحه Dashboard است و داده‌های موردنیاز UI را
 * از لایه domain دریافت کرده و در قالب [DashboardState] به صفحه ارسال می‌کند.
 *
 * وظایف اصلی این ViewModel:
 * - دریافت لیست حساب‌ها
 * - محاسبه مجموع موجودی
 * - مدیریت وضعیت loading
 * - مدیریت خطا
 * - جلوگیری از collect تکراری هنگام refresh
 *
 * @property getAllWalletsUseCase یوزکیس دریافت همه حساب‌ها
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getAllWalletsUseCase: GetAllWalletsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    /**
     * job مربوط به دریافت داده‌های داشبورد.
     *
     * برای این نگه داشته می‌شود که اگر کاربر چند بار refresh زد،
     * collect قبلی cancel شود و فقط آخرین درخواست فعال بماند.
     */
    private var loadJob: Job? = null

    init {
        onEvent(DashboardEvent.RefreshData)
    }

    /**
     * دریافت و مدیریت رویدادهای صفحه داشبورد.
     *
     * @param event رویدادی که از UI ارسال شده است
     */
    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.RefreshData -> loadData()
        }
    }

    /**
     * بارگذاری یا تازه‌سازی اطلاعات داشبورد.
     *
     * رفتار این تابع:
     * - اگر job قبلی فعال باشد، آن را متوقف می‌کند
     * - loading را فعال می‌کند
     * - داده‌ها را از UseCase دریافت می‌کند
     * - مجموع موجودی حساب‌ها را محاسبه می‌کند
     * - در صورت بروز خطا، پیام مناسب در state قرار می‌دهد
     */
    private fun loadData() {
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            getAllWalletsUseCase()
                .catch { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "یه مشکلی پیش اومد، دوباره امتحان کن."
                        )
                    }
                }
                .collect { wallets ->
                    _state.update {
                        it.copy(
                            wallets = wallets,
                            totalBalance = wallets.sumOf { wallet -> wallet.balance },
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }
}
