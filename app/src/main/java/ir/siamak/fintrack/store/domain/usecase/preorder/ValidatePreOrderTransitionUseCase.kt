package ir.siamak.fintrack.store.domain.usecase.preorder
import ir.siamak.fintrack.store.domain.model.*
class ValidatePreOrderTransitionUseCase {
    operator fun invoke(order: PreOrder, target: PreOrderStatus) {
        if(order.status == target) return
        val allowed = when(order.status) {
            PreOrderStatus.NEW -> setOf(PreOrderStatus.IN_PROGRESS,PreOrderStatus.CANCELLED)
            PreOrderStatus.IN_PROGRESS -> setOf(PreOrderStatus.READY,PreOrderStatus.CANCELLED)
            PreOrderStatus.READY -> setOf(PreOrderStatus.DELIVERED,PreOrderStatus.CANCELLED)
            else -> emptySet()
        }
        require(target in allowed) { "تغییر وضعیت مجاز نیست" }
    }
}
