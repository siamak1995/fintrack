package ir.siamak.fintrack.presentation.baseinfo.tags.add_edit_tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.data.model.Tag
import ir.siamak.fintrack.domain.repository.TagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditTagState(
    val name: String = ""
)

sealed class AddEditTagEvent {
    data class NameChanged(val name: String) : AddEditTagEvent()
    data object Save : AddEditTagEvent()
}

@HiltViewModel
class AddEditTagViewModel @Inject constructor(
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditTagState())
    val state = _state.asStateFlow()

    private var currentId: Long? = null

    fun loadTag(id: Long?) {
        if (id == null) return
        currentId = id

        viewModelScope.launch {
            val tag = tagRepository.getTagById(id) ?: return@launch
            _state.value = _state.value.copy(name = tag.name)
        }
    }

    fun onEvent(event: AddEditTagEvent) {
        when (event) {
            is AddEditTagEvent.NameChanged -> {
                _state.value = _state.value.copy(name = event.name)
            }

            AddEditTagEvent.Save -> {
                viewModelScope.launch {
                    val trimmedName = state.value.name.trim()
                    if (trimmedName.isBlank()) return@launch

                    val tag = Tag(
                        id = currentId ?: 0L,
                        name = trimmedName
                    )

                    tagRepository.insertTag(tag)
                }
            }
        }
    }
}
