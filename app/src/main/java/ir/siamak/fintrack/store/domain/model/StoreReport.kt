package ir.siamak.fintrack.store.domain.model

import java.time.LocalDate

enum class ReportPeriod { DAY, WEEK, MONTH, YEAR }
enum class ReportOrder { NEWEST, OLDEST, AMOUNT_DESC, BALANCE_DESC }
enum class ReportSettlement { ALL, SETTLED, OUTSTANDING, INSTALLMENT }
enum class ReportDocument { SALE, PREORDER }

data class StoreReportFilter(
    val from: LocalDate = LocalDate.now().minusDays(29),
    val to: LocalDate = LocalDate.now(),
    val customerId: Long? = null,
    val productId: Long? = null,
    val settlement: ReportSettlement = ReportSettlement.ALL,
    val document: ReportDocument = ReportDocument.SALE,
    val search: String = "",
    val order: ReportOrder = ReportOrder.NEWEST,
    val period: ReportPeriod = ReportPeriod.DAY,
    val comparisonFrom: LocalDate? = null,
    val comparisonTo: LocalDate? = null,
    val page: Int = 0,
    val pageSize: Int = 20,
    val storeId: Long? = null,
    val comparisonStoreId: Long? = null
)

data class StoreReportTotals(
    val count: Int = 0, val grossItems: Long = 0, val discount: Long = 0,
    val tax: Long = 0, val financeCharge: Long = 0, val payable: Long = 0,
    val received: Long = 0, val balance: Long = 0, val dueScheduleInvoices: Int = 0,
    val unreconciledInvoices: Int = 0
)
data class StoreReportProduct(val id: Long, val name: String, val quantity: Long, val gross: Long)
data class StoreReportCustomer(val id: Long, val name: String, val phone: String, val count: Int, val payable: Long, val balance: Long)
data class StoreReportBucket(val label: String, val amount: Long, val count: Int)
data class StoreReportBranch(val id: Long, val name: String, val totals: StoreReportTotals)
data class StoreReport(
    val filter: StoreReportFilter,
    val totals: StoreReportTotals,
    val comparison: StoreReportTotals,
    val comparisonFrom: LocalDate,
    val comparisonTo: LocalDate,
    val rows: List<Invoice>,
    val allRows: List<Invoice>,
    val products: List<StoreReportProduct>,
    val customers: List<StoreReportCustomer>,
    val trend: List<StoreReportBucket>,
    val pages: Int,
    val excludedCancelled: Int,
    val invalidDates: Int = 0,
    val branches: List<StoreReportBranch> = emptyList(),
    val stores: List<Store> = emptyList(),
    val comparisonTrend: List<StoreReportBucket> = emptyList()
)
