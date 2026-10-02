package ir.siamak.fintrack.account.data.repository

import ir.siamak.fintrack.account.domain.model.AccountantContext
import ir.siamak.fintrack.account.domain.model.AccountantType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Tests context selection rules before persistence is connected. */
class InMemoryContextRepositoryTest {
    @Test fun switchesOnlyToAnActiveContext() = runBlocking {
        val repository = InMemoryContextRepository(listOf(context(1, true), context(2, true), context(3, false)))
        assertTrue(repository.switchActiveContext(2).isSuccess)
        assertEquals(2L, repository.observeActiveContext().first()?.id)
        assertTrue(repository.switchActiveContext(3).isFailure)
        assertEquals(2L, repository.observeActiveContext().first()?.id)
    }

    private fun context(id: Long, active: Boolean) = AccountantContext(id, 1, AccountantType.PERSONAL, "Context $id", isActive = active, createdAt = 0, updatedAt = 0)
}
