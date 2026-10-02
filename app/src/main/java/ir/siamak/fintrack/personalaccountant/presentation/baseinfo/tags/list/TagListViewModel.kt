package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.tags.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.personalaccountant.domain.repository.TagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TagListViewModel @Inject constructor(
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TagListState())
    val state = _state.asStateFlow()

    init {
        loadTags()
    }

    private fun loadTags() {
        viewModelScope.launch {
            tagRepository.getAllTags().collect {
                _state.value = _state.value.copy(tags = it)
            }
        }
    }

    fun onEvent(event: TagListEvent) {
        when (event) {
            is TagListEvent.Delete -> {
                viewModelScope.launch {
                    tagRepository.deleteTagById(event.id)
                }
            }
        }
    }

}

