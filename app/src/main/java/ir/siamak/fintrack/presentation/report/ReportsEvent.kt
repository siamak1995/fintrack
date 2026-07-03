package ir.siamak.fintrack.presentation.report

sealed interface ReportsEvent {

    data object LoadReports : ReportsEvent

    data object Refresh : ReportsEvent

}