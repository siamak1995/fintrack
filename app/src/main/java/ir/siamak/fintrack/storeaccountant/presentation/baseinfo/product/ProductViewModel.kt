package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.siamak.fintrack.storeaccountant.domain.model.Product
import ir.siamak.fintrack.storeaccountant.domain.usecase.product.AddProductUseCase
import ir.siamak.fintrack.storeaccountant.domain.usecase.product.DeleteProductUseCase
import ir.siamak.fintrack.storeaccountant.domain.usecase.product.GetProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProductUiState())
    val state = _state.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getProductsUseCase().collect { products ->
                _state.update { it.copy(products = products, isLoading = false) }
            }
        }
    }

    fun onEvent(event: ProductEvent) {
        when (event) {
            is ProductEvent.NameChanged -> _state.update { it.copy(name = event.name) }
            is ProductEvent.MaterialChanged -> _state.update { it.copy(material = event.material) }
            is ProductEvent.SizeChanged -> _state.update { it.copy(size = event.size) }
            is ProductEvent.WeightChanged -> _state.update { it.copy(weight = event.weight) }
            is ProductEvent.StockChanged -> _state.update { it.copy(stock = event.stock) }
            is ProductEvent.PriceChanged -> _state.update { it.copy(price = event.price) }
            ProductEvent.SaveProduct -> saveProduct()
            is ProductEvent.DeleteProduct -> deleteProduct(event.product)
        }
    }

    private fun saveProduct() {
        viewModelScope.launch {
            val product = Product(
                id = 0,
                name = _state.value.name,
                material = _state.value.material.ifBlank { null },
                size = _state.value.size.ifBlank { null },
                weight = _state.value.weight.ifBlank { null },
                stock = _state.value.stock.toIntOrNull() ?: 0,
                price = _state.value.price.toLongOrNull() ?: 0L,
                image = null
            )
            addProductUseCase(product)
            _state.update { it.copy(isSaved = true) }
        }
    }

    private fun deleteProduct(product: Product) {
        viewModelScope.launch {
            deleteProductUseCase(product)
        }
    }
}
