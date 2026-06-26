package ir.siamak.fintrack.presentation.dashboard

sealed class DashboardUiAction {

    data object OpenAddWallet : DashboardUiAction()

    data class OpenWallet(val id:Long):DashboardUiAction()

    data object OpenTransactions : DashboardUiAction()

    data object OpenMembers : DashboardUiAction()

}