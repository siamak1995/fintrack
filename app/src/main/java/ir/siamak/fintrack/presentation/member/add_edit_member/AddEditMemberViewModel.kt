package ir.siamak.fintrack.presentation.member.add_edit_member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.data.model.Member
import ir.siamak.fintrack.domain.usecase.member.MemberUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditMemberViewModel @Inject constructor(
    private val memberUseCases: MemberUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditMemberState())
    val state = _state.asStateFlow()

    private var currentMemberId: Long? = null

    fun onEvent(event: AddEditMemberEvent) {
        when (event) {
            is AddEditMemberEvent.EnteredName -> {
                _state.update { it.copy(name = event.value) }
            }

            is AddEditMemberEvent.EnteredRelation -> {
                _state.update { it.copy(relation = event.value) }
            }

            is AddEditMemberEvent.EnteredColor -> {
                _state.update { it.copy(color = event.value) }
            }

            is AddEditMemberEvent.LoadMember -> {
                loadMember(event.memberId)
            }

            is AddEditMemberEvent.EnteredIcon -> {
                _state.update { it.copy(icon = event.value) }
            }

            AddEditMemberEvent.SaveMember -> {
                saveMember()
            }
        }
    }

    private fun saveMember() {
        viewModelScope.launch {
            val name = _state.value.name.trim()
            val relation = _state.value.relation.trim()
            val color = _state.value.color.trim()
            val icon = _state.value.icon

            if (name.isBlank() || relation.isBlank()) return@launch

            _state.update { it.copy(isLoading = true) }

            val member = Member(
                id = currentMemberId ?: 0L,
                name = name,
                relation = relation,
                color = color,
                icon = icon
            )

            if (currentMemberId == null) {
                memberUseCases.insertMember(member)
            } else {
                memberUseCases.updateMember(member)
            }

            _state.update { it.copy(isLoading = false, isSaved = true) }
        }
    }

    private fun loadMember(memberId: Long) {
        viewModelScope.launch {
            currentMemberId = memberId

            val member = memberUseCases.getMemberById(memberId)

            member?.let {
                _state.update { state ->
                    state.copy(
                        name = it.name,
                        relation = it.relation,
                        color = it.color,
                        icon = it.icon
                    )
                }
            }
        }
    }
}
