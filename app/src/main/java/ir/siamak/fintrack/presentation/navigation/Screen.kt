package ir.siamak.fintrack.presentation.navigation

sealed class Screen(val route: String) {

    object Dashboard : Screen("dashboard")

    object Expense : Screen("expense")

    object Installment : Screen("installment")

    object Members : Screen("members")

    object Report : Screen("report")

    object Settings : Screen("settings")
}