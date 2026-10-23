package ir.siamak.fintrack.store.domain.usecase.store
import ir.siamak.fintrack.store.domain.model.*
import java.time.LocalDate
import java.math.BigDecimal
import java.math.RoundingMode

class CalculateStoreOverviewUseCase {
    operator fun invoke(workspace: StoreWorkspace, today: String = LocalDate.now().toString()): StoreOverview {
        val sales = workspace.sales.filter { it.createdAt.take(10) == today }
        return StoreOverview(sales.fold(0L) { sum,sale -> Math.addExact(sum,sale.finalAmount) },sales.size,
            workspace.orders.count { it.status != PreOrderStatus.DELIVERED && it.status != PreOrderStatus.CANCELLED })
    }
}
