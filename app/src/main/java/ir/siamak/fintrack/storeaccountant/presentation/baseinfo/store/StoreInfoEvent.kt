package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.store

sealed class StoreInfoEvent {
    data class NameChanged(val name: String) : StoreInfoEvent()
    data class BrandChanged(val brand: String) : StoreInfoEvent()
    data class PhoneChanged(val phone: String) : StoreInfoEvent()
    data class AddressChanged(val address: String) : StoreInfoEvent()
    data class DescriptionChanged(val description: String) : StoreInfoEvent()
    object SaveStore : StoreInfoEvent()
}
