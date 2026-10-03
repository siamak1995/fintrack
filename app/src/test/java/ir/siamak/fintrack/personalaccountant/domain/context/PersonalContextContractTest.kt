package ir.siamak.fintrack.personalaccountant.domain.context

import org.junit.Assert.assertThrows
import org.junit.Test

class PersonalContextContractTest {
    @Test
    fun rejectsNonPositiveContextId() {
        assertThrows(IllegalArgumentException::class.java) {
            requireValidPersonalContextId(0L)
        }
    }

    @Test
    fun rejectsOwnershipMismatch() {
        assertThrows(IllegalArgumentException::class.java) {
            requireContextOwnership(requestContextId = 1L, entityContextId = 2L)
        }
    }
}
