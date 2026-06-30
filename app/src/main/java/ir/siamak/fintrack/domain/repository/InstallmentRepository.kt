package ir.siamak.fintrack.domain.repository

import ir.siamak.fintrack.data.model.Installment
import kotlinx.coroutines.flow.Flow

interface InstallmentRepository {

    fun getAllInstallments(): Flow<List<Installment>>

    suspend fun getInstallmentById(id: Long): Installment?

    suspend fun insertInstallment(installment: Installment)

    suspend fun updateInstallment(installment: Installment)

    suspend fun deleteInstallment(installment: Installment)

}