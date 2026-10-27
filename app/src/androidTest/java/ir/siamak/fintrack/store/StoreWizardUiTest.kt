package ir.siamak.fintrack.store
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ir.siamak.fintrack.store.domain.model.*
import ir.siamak.fintrack.store.presentation.sales.create.*
import ir.siamak.fintrack.store.presentation.preorder.create.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class StoreWizardUiTest {
    @get:Rule val compose = createComposeRule()
    @Test fun productCounterCannotExceedAvailableStock() {
        var state by mutableStateOf(CreateSaleUiState(
            draft = SaleDraft(step = 1), loading = false,
            workspace = StoreWorkspace(products = listOf(Product(1,"ماگ",stock = 1,price = 450_000)))))
        compose.setContent { MaterialTheme { CreateSaleContent(state) { event ->
            if(event is CreateSaleEvent.Edit) state = state.copy(draft = event.change(state.draft))
        } } }
        compose.onNodeWithText("انتخاب محصول").performScrollTo().performClick()
        compose.onNodeWithText("ماگ").performClick()
        compose.onNodeWithText("+").assertIsNotEnabled()
        compose.runOnIdle { assertEquals(1,state.draft.quantities[1L]) }
        compose.onNodeWithText("−").performClick()
        compose.runOnIdle { assertFalse(state.draft.quantities.containsKey(1L)) }
    }
    @Test fun customerFormEmitsImmutableDraftUpdates() {
        var state by mutableStateOf(CreateSaleUiState(loading = false))
        var next = false
        compose.setContent { MaterialTheme { CreateSaleContent(state) { event ->
            when(event) {
                is CreateSaleEvent.Edit -> state = state.copy(draft = event.change(state.draft))
                CreateSaleEvent.Next -> next = true
                else -> Unit
            }
        } } }
        compose.onNodeWithText("مرحله‌ای").performClick()
        compose.onNodeWithText("نام مشتری *").performTextInput("مشتری تست")
        compose.onNodeWithText("تلفن *").performTextInput("09123456789")
        compose.onNodeWithText("ادامه").performClick()
        compose.runOnIdle {
            assertEquals("مشتری تست",state.draft.name)
            assertEquals("09123456789",state.draft.phone)
            assertTrue(next)
        }
    }
    @Test fun invalidPreorderCannotBeSubmitted() {
        val state = CreatePreOrderUiState(draft = PreOrderDraft(step = 5),loading = false)
        compose.setContent { MaterialTheme { CreatePreOrderContent(state,{}) } }
        compose.onNodeWithText("ثبت سفارش و فاکتور").assertIsNotEnabled()
    }
}
