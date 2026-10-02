package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.storeaccountant.domain.model.Store
import ir.siamak.fintrack.storeaccountant.domain.usecase.store.GetStoreUseCase
import ir.siamak.fintrack.storeaccountant.domain.usecase.store.SaveStoreUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreInfoViewModel @Inject constructor(
    private val getStoreUseCase: GetStoreUseCase,
    private val saveStoreUseCase: SaveStoreUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(StoreInfoUiState())
    val state = _state.asStateFlow()

    init {
        loadStore()
    }

    private fun loadStore() {
        viewModelScope.launch {
            getStoreUseCase().collect { store ->
                store?.let { s ->
                    _state.update {
                        it.copy(
                            id = s.id,
                            name = s.name,
                            brand = s.brand,
                            phone = s.phone ?: "",
                            address = s.address ?: "",
                            description = s.description ?: "",
                            logoPath = s.logoPath
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: StoreInfoEvent) {
        when (event) {
            is StoreInfoEvent.NameChanged -> _state.update { it.copy(name = event.name) }
            is StoreInfoEvent.BrandChanged -> _state.update { it.copy(brand = event.brand) }
            is StoreInfoEvent.PhoneChanged -> _state.update { it.copy(phone = event.phone) }
            is StoreInfoEvent.AddressChanged -> _state.update { it.copy(address = event.address) }
            is StoreInfoEvent.DescriptionChanged -> _state.update { it.copy(description = event.description) }
            StoreInfoEvent.SaveStore -> saveStore()
        }
    }

    private fun saveStore() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val store = Store(
                id = _state.value.id,
                name = _state.value.name,
                brand = _state.value.brand,
                logoPath = _state.value.logoPath,
                phone = _state.value.phone,
                address = _state.value.address,
                description = _state.value.description
            )
            saveStoreUseCase(store)
            _state.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}
