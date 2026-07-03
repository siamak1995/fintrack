package ir.siamak.fintrack.presentation.reports

sealed interface ReportsEvent {

    data object LoadReports : ReportsEvent

    data object Refresh : ReportsEvent

}