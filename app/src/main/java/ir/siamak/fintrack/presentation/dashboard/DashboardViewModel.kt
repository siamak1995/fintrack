package ir.siamak.fintrack.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.data.model.TransactionType
import ir.siamak.fintrack.domain.usecase.member.GetAllMembersUseCase
import ir.siamak.fintrack.domain.usecase.transaction.GetAllTransactionsUseCase
import ir.siamak.fintrack.domain.usecase.wallet.GetAllWalletsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
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
 * @property getAllTransactionsUseCase یوزکیس دریافت همه تراکنش ها
 * @property getAllMembersUseCase یوزکیس دریافت همه اعضا
 * @property getAllInstallmentsUseCase یوزکیس دریافت همه اقساط
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getAllWalletsUseCase: GetAllWalletsUseCase,
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val getAllMembersUseCase: GetAllMembersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.RefreshData -> loadDashboardData()
        }
    }

    private fun loadDashboardData() {
        _state.update { it.copy(isLoading = true) }

        // ترکیب ۳ منبع داده به صورت همزمان
        combine(
            getAllWalletsUseCase(),
            getAllTransactionsUseCase(),
            getAllMembersUseCase()
        ) { wallets, transactions, members ->

            val totalBalance = wallets.sumOf { it.balance }

            // محاسبه درآمد و هزینه ماه جاری (مثلاً بر اساس تراکنش‌های کل)
            val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

            _state.update {
                it.copy(
                    wallets = wallets,
                    recentTransactions = transactions.take(5), // فقط ۵ تای آخر
                    members = members,
                    totalBalance = totalBalance,
                    monthlyIncome = income,
                    monthlyExpense = expense,
                    transactionCount = transactions.size,
                    isLoading = false
                )
            }
        }.catch { throwable ->
            _state.update { it.copy(isLoading = false, error = throwable.message) }
        }.launchIn(viewModelScope)
    }
}