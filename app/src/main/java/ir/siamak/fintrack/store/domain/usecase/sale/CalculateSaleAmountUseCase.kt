package ir.siamak.fintrack.store.domain.usecase.sale
import ir.siamak.fintrack.store.domain.model.SaleItem
class CalculateSaleAmountUseCase {
    operator fun invoke(items: List<SaleItem>): Long = items.fold(0L) { sum,item ->
        require(item.quantity >= 0 && item.unitPrice >= 0)
        Math.addExact(sum,Math.multiplyExact(item.quantity.toLong(),item.unitPrice))
    }
}
