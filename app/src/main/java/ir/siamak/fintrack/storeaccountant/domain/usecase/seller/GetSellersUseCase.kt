package ir.siamak.fintrack.storeaccountant.domain.usecase.seller

import ir.siamak.fintrack.storeaccountant.domain.model.Seller
import ir.siamak.fintrack.storeaccountant.domain.repository.SellerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSellersUseCase @Inject constructor(
    private val repository: SellerRepository
) {
    operator fun invoke(): Flow<List<Seller>> = repository.getAllSellers()
}
