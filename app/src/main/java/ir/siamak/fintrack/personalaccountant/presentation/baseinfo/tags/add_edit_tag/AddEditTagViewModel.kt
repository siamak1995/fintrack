package ir.siamak.fintrack.personalaccountant.presentation.baseinfo.tags.add_edit_tag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.personalaccountant.data.model.Tag
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.personalaccountant.domain.repository.TagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// افزودن رنگ و نوع تراکنش مجاز به State صفحه
data class AddEditTagState(
    val name: String = "",
    val color: Long? = null,
    val allowedType: TransactionType = TransactionType.EXPENSE
)

// افزودن رویدادها برای انتخاب رنگ و نوع تراکنش
sealed class AddEditTagEvent {
    data class NameChanged(val name: String) : AddEditTagEvent()
    data class ColorChanged(val color: Long?) : AddEditTagEvent()
    data class AllowedTypeChanged(val type: TransactionType) : AddEditTagEvent()
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
            // مقداردهی اولیه فیلدها هنگام ویرایش تگ
            _state.value = _state.value.copy(
                name = tag.name,
                color = tag.color,
                allowedType = tag.allowedType
            )
        }
    }

    fun onEvent(event: AddEditTagEvent) {
        when (event) {
            is AddEditTagEvent.NameChanged -> {
                _state.value = _state.value.copy(name = event.name)
            }

            is AddEditTagEvent.ColorChanged -> {
                _state.value = _state.value.copy(color = event.color)
            }

            is AddEditTagEvent.AllowedTypeChanged -> {
                _state.value = _state.value.copy(allowedType = event.type)
            }

            AddEditTagEvent.Save -> {
                viewModelScope.launch {
                    val trimmedName = state.value.name.trim()
                    if (trimmedName.isBlank()) return@launch

                    // ارسال تمام پارامترهای جدید به کلاس سازنده Tag
                    val tag = Tag(
                        id = currentId ?: 0L,
                        name = trimmedName,
                        color = state.value.color,
                        allowedType = state.value.allowedType
                    )

                    tagRepository.insertTag(tag)
                }
            }
        }
    }
}

