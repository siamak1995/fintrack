package ir.siamak.fintrack.personalaccountant.presentation.dashboard

sealed interface DashboardUiAction {

    data object AddWallet : DashboardUiAction

    data object AddTransaction : DashboardUiAction

    data object OpenMembers : DashboardUiAction

    data object OpenReports : DashboardUiAction

    data object OpenInstallments : DashboardUiAction

    data class EditWallet(
        val id: Long
    ) : DashboardUiAction

}
