package ir.siamak.fintrack.presentation.member.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.domain.usecase.member.MemberUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val memberUseCases: MemberUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemberUiState(isLoading = true))
    val uiState: StateFlow<MemberUiState> = _uiState.asStateFlow()

    init {
        loadMembers()
    }

    private fun loadMembers() {
        viewModelScope.launch {
            memberUseCases.getAllMembers()
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "خطا در دریافت اعضا"
                        )
                    }
                }
                .collect { memberList ->
                    _uiState.update {
                        it.copy(
                            members = memberList,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun deleteMember(member: Member) {
        viewModelScope.launch {
            memberUseCases.deleteMember(member)
        }
    }
}
