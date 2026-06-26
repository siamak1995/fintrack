package ir.siamak.fintrack.data.repository

import ir.siamak.fintrack.data.local.dao.InstallmentDao
import ir.siamak.fintrack.data.mapper.toModel
import ir.siamak.fintrack.data.model.Installment
import ir.siamak.fintrack.domain.repository.InstallmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InstallmentRepositoryImpl @Inject constructor(
    private val dao: InstallmentDao
) : InstallmentRepository {
    override fun getAllInstallments(): Flow<List<Installment>> {
        return dao.getAllInstallments().map { entities ->
            entities.map { it.toModel() }
        }
    }
}
