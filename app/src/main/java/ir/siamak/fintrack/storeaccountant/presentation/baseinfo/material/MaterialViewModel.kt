package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.material

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.storeaccountant.domain.model.RawMaterial
import ir.siamak.fintrack.storeaccountant.domain.usecase.material.AddMaterialUseCase
import ir.siamak.fintrack.storeaccountant.domain.usecase.material.DeleteMaterialUseCase
import ir.siamak.fintrack.storeaccountant.domain.usecase.material.GetMaterialsUseCase
import ir.siamak.fintrack.storeaccountant.domain.usecase.material.UpdateMaterialUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialViewModel @Inject constructor(
    private val getMaterialsUseCase: GetMaterialsUseCase,
    private val addMaterialUseCase: AddMaterialUseCase,
    private val updateMaterialUseCase: UpdateMaterialUseCase,
    private val deleteMaterialUseCase: DeleteMaterialUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MaterialUiState())
    val state = _state.asStateFlow()

    init {
        loadMaterials()
    }

    private fun loadMaterials() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getMaterialsUseCase().collect { materials ->
                _state.update { it.copy(materials = materials, isLoading = false) }
            }
        }
    }

    fun onEvent(event: MaterialEvent) {
        when (event) {
            is MaterialEvent.NameChanged -> _state.update { it.copy(name = event.name) }
            is MaterialEvent.UnitChanged -> _state.update { it.copy(unit = event.unit) }
            is MaterialEvent.PriceChanged -> _state.update { it.copy(pricePerUnit = event.price) }
            is MaterialEvent.QuantityChanged -> _state.update { it.copy(quantity = event.quantity) }
            is MaterialEvent.PurchaseDateChanged -> _state.update { it.copy(purchaseDate = event.date) }
            is MaterialEvent.ShippingCostChanged -> _state.update { it.copy(shippingCost = event.cost) }
            MaterialEvent.SaveMaterial -> saveMaterial()
            is MaterialEvent.DeleteMaterial -> deleteMaterial(event.material)
        }
    }

    private fun saveMaterial() {
        viewModelScope.launch {
            val material = RawMaterial(
                id = 0,
                name = _state.value.name,
                unit = _state.value.unit,
                pricePerUnit = _state.value.pricePerUnit.toLongOrNull() ?: 0L,
                quantity = _state.value.quantity.toDoubleOrNull() ?: 0.0,
                purchaseDate = _state.value.purchaseDate,
                shippingCost = _state.value.shippingCost.toLongOrNull()
            )
            addMaterialUseCase(material)
            _state.update { it.copy(isSaved = true) }
        }
    }

    private fun deleteMaterial(material: RawMaterial) {
        viewModelScope.launch {
            deleteMaterialUseCase(material)
        }
    }
}
