package ir.siamak.fintrack.personalaccountant

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ir.siamak.fintrack.common.core.datepicker.components.PersianCalendarHeader
import ir.siamak.fintrack.common.core.datepicker.model.PersianMonth
import ir.siamak.fintrack.store.presentation.components.Entry
import ir.siamak.fintrack.personalaccountant.data.model.Transaction
import ir.siamak.fintrack.personalaccountant.data.model.TransactionType
import ir.siamak.fintrack.personalaccountant.presentation.transaction.section.TransactionItemCard
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class FinanceFormsUiTest {
    @get:Rule val compose = createComposeRule()
    @Test fun monthAndYearCanBeSelectedDirectlyWithoutGroupedYear() {
        var month by mutableStateOf(PersianMonth(1405,8))
        compose.setContent { MaterialTheme {
            PersianCalendarHeader(month,{}, {},onMonthSelected = { month = it })
        } }
        compose.onNodeWithText("آبان ۱۴۰۵").performClick()
        compose.onNodeWithText("سال شمسی").performTextReplacement("1400")
        compose.onNodeWithText("فروردین").performClick()
        compose.onNodeWithText("نمایش ماه").performClick()
        compose.onNodeWithText("فروردین ۱۴۰۰").assertIsDisplayed()
        compose.runOnIdle { assertEquals(PersianMonth(1400,1),month) }
    }
    @Test fun amountWordsUpdateOnEveryEditAndDisappearWhenCleared() {
        var amount by mutableStateOf("")
        compose.setContent { MaterialTheme { Entry("مبلغ",amount,{ amount = it },number = true) } }
        compose.onNodeWithText("مبلغ").performTextInput("12500")
        compose.onNodeWithText("دوازده هزار و پانصد").assertIsDisplayed()
        compose.onNodeWithText("مبلغ").performTextReplacement("20000")
        compose.onNodeWithText("بیست هزار").assertIsDisplayed()
        compose.onNodeWithText("دوازده هزار و پانصد").assertDoesNotExist()
        compose.onNodeWithText("مبلغ").performTextClearance()
        compose.onNodeWithText("بیست هزار").assertDoesNotExist()
    }
    @Test fun transactionCardShowsFourFieldsAndHidesSmsBodyAndAccountNames() {
        compose.setContent { MaterialTheme {
            TransactionItemCard(Transaction(amount = 12500.0,type = TransactionType.EXPENSE,categoryName = "قسط",
                walletId = 1,memberId = 1,date = 0,note = "[FinTrackSms:test]\nمتن خصوصی پیامک"),
                "شخص خصوصی","حساب خصوصی",{})
        } }
        compose.onNodeWithText("قسط").assertIsDisplayed()
        compose.onNodeWithText("بدون تگ").assertIsDisplayed()
        compose.onNodeWithText("شخص خصوصی").assertDoesNotExist()
        compose.onNodeWithText("حساب خصوصی").assertDoesNotExist()
        compose.onNodeWithText("متن خصوصی پیامک",substring = true).assertDoesNotExist()
    }
}
