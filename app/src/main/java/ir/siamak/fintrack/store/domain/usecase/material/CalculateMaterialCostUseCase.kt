package ir.siamak.fintrack.store.domain.usecase.material
import ir.siamak.fintrack.store.domain.model.PreOrderItem
import java.math.BigDecimal
import java.math.RoundingMode
class CalculateMaterialCostUseCase {
    operator fun invoke(items: List<PreOrderItem>): Long = items.fold(BigDecimal.ZERO) { sum,item ->
        require(item.quantity.isFinite() && item.quantity >= 0 && item.unitPrice >= 0)
        sum.add(BigDecimal.valueOf(item.unitPrice).multiply(BigDecimal.valueOf(item.quantity)))
    }.setScale(0,RoundingMode.HALF_UP).longValueExact()
}
