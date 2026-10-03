package ir.siamak.fintrack.personalaccountant.data.mapper

import ir.siamak.fintrack.personalaccountant.data.local.entity.MemberEntity
import ir.siamak.fintrack.personalaccountant.data.local.entity.WalletEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class ContextMapperTest {
    @Test
    fun walletRoundTripPreservesContextId() {
        val entity = WalletEntity(id = 7, contextId = 42, name = "Wallet", balance = 10.0, color = "#000000")

        assertEquals(42L, entity.toModel().contextId)
        assertEquals(42L, entity.toModel().toEntity().contextId)
    }

    @Test
    fun memberRoundTripPreservesContextId() {
        val entity = MemberEntity(id = 3, contextId = 42, name = "Member", relation = "Friend", color = "#000000", icon = "person")

        assertEquals(42L, entity.toModel().contextId)
        assertEquals(42L, entity.toModel().toEntity().contextId)
    }
}
