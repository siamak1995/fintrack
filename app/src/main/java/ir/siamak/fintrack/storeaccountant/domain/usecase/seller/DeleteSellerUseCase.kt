package ir.siamak.fintrack.storeaccountant.domain.usecase.seller

import ir.siamak.fintrack.storeaccountant.domain.model.Seller
import ir.siamak.fintrack.storeaccountant.domain.repository.SellerRepository
import javax.inject.Inject

class DeleteSellerUseCase @Inject constructor(
    private val repository: SellerRepository
) {
    suspend operator fun invoke(seller: Seller) = repository.deleteSeller(seller)
}
