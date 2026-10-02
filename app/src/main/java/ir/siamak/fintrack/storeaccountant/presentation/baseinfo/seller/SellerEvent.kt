package ir.siamak.fintrack.storeaccountant.presentation.baseinfo.seller

import ir.siamak.fintrack.storeaccountant.domain.model.Seller

sealed class SellerEvent {
    data class FirstNameChanged(val name: String) : SellerEvent()
    data class LastNameChanged(val name: String) : SellerEvent()
    data class PhoneChanged(val phone: String) : SellerEvent()
    data class AddressChanged(val address: String) : SellerEvent()
    data class DescriptionChanged(val description: String) : SellerEvent()
    object SaveSeller : SellerEvent()
    data class DeleteSeller(val seller: Seller) : SellerEvent()
}
