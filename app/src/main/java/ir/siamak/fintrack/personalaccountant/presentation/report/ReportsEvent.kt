package ir.siamak.fintrack.personalaccountant.presentation.report

sealed interface ReportsEvent {

    data object LoadReports : ReportsEvent

    data object Refresh : ReportsEvent

}
