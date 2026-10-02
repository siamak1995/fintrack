package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.storeaccountant.domain.model.Seller
import ir.siamak.fintrack.storeaccountant.domain.usecase.seller.AddSellerUseCase
import ir.siamak.fintrack.storeaccountant.domain.usecase.seller.DeleteSellerUseCase
import ir.siamak.fintrack.storeaccountant.domain.usecase.seller.GetSellersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerViewModel @Inject constructor(
    private val getSellersUseCase: GetSellersUseCase,
    private val addSellerUseCase: AddSellerUseCase,
    private val deleteSellerUseCase: DeleteSellerUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SellerUiState())
    val state = _state.asStateFlow()

    init {
        loadSellers()
    }

    private fun loadSellers() {
        viewModelScope.launch {
            getSellersUseCase().collect { sellers ->
                _state.update { it.copy(sellers = sellers) }
            }
        }
    }

    fun onEvent(event: SellerEvent) {
        when (event) {
            is SellerEvent.FirstNameChanged -> _state.update { it.copy(firstName = event.name) }
            is SellerEvent.LastNameChanged -> _state.update { it.copy(lastName = event.name) }
            is SellerEvent.PhoneChanged -> _state.update { it.copy(phone = event.phone) }
            is SellerEvent.AddressChanged -> _state.update { it.copy(address = event.address) }
            is SellerEvent.DescriptionChanged -> _state.update { it.copy(description = event.description) }
            SellerEvent.SaveSeller -> saveSeller()
            is SellerEvent.DeleteSeller -> deleteSeller(event.seller)
        }
    }

    private fun saveSeller() {
        viewModelScope.launch {
            val seller = Seller(
                id = 0,
                firstName = _state.value.firstName,
                lastName = _state.value.lastName,
                phone = _state.value.phone,
                address = _state.value.address,
                description = _state.value.description
            )
            addSellerUseCase(seller)
            _state.update { it.copy(isSaved = true) }
        }
    }

    private fun deleteSeller(seller: Seller) {
        viewModelScope.launch {
            deleteSellerUseCase(seller)
        }
    }
}
