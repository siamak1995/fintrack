package ir.siamak.fintrack.presentation.security.setup

data class PinSetupState(
    val step: PinSetupStep = PinSetupStep.ENTER_NEW,
    val firstPin: String = "",
    val secondPin: String = "",
    val errorMessage: String? = null,
    val isFinished: Boolean = false
)

enum class PinSetupStep {
    ENTER_NEW,
    CONFIRM_NEW
}
