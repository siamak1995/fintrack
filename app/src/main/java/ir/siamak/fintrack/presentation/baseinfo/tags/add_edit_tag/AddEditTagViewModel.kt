package ir.siamak.fintrack.presentation.baseinfo.tags.add_edit_tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.domain.model.Tag
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
    object Save : AddEditTagEvent()
}

@HiltViewModel
class AddEditTagViewModel @Inject constructor(
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditTagState())
    val state = _state.asStateFlow()

    private var currentId: Int? = null

    fun loadTag(id: Int?) {
        if (id == null) return
        currentId = id

        viewModelScope.launch {
            val tag = tagRepository.getTagById(id)
            _state.value = _state.value.copy(name = tag.name)
        }
    }

    fun onEvent(event: AddEditTagEvent) {
        when(event) {
            is AddEditTagEvent.NameChanged -> {
                _state.value = _state.value.copy(name = event.name)
            }
            AddEditTagEvent.Save -> {
                viewModelScope.launch {
                    val tag = Tag(
                        id = currentId ?: 0,
                        name = state.value.name
                    )

                    if (currentId == null)
                        tagRepository.insertTag(tag)
                    else
                        tagRepository.updateTag(tag)
                }
            }
        }
    }
}
