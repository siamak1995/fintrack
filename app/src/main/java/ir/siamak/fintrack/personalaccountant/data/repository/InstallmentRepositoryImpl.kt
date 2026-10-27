package ir.siamak.fintrack.personalaccountant.data.repository

import ir.siamak.fintrack.personalaccountant.data.local.dao.InstallmentDao
import ir.siamak.fintrack.personalaccountant.data.mapper.toEntity
import ir.siamak.fintrack.personalaccountant.data.mapper.toModel
import ir.siamak.fintrack.personalaccountant.data.model.Installment
import ir.siamak.fintrack.personalaccountant.domain.repository.InstallmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InstallmentRepositoryImpl @Inject constructor(
    private val dao: InstallmentDao,
    private val reminders: ir.siamak.fintrack.personalaccountant.notifications.InstallmentReminders
) : InstallmentRepository {
    private fun validate(row: Installment) {
        require(row.title.isNotBlank() && row.totalAmount.isFinite() && row.totalAmount > 0 && row.paidAmount.isFinite() && row.paidAmount in 0.0..row.totalAmount)
        require(row.installmentCount in 1..600 && row.startDate <= row.dueDate) { "برنامه اقساط معتبر نیست" }
    }


    override fun getAllInstallments(): Flow<List<Installment>> {
        return dao.getAllInstallments().map { entities ->
            entities.map { it.toModel() } // اصلاح شد از toInstallment به toModel
        }
    }

    override suspend fun insertInstallment(installment: Installment) {
        validate(installment)
        dao.insertInstallment(installment.toEntity())
        reminders.clear(installment.id)
        reminders.schedule()
    }

    override suspend fun deleteInstallment(installment: Installment) {
        dao.deleteInstallment(installment.toEntity())
        reminders.clear(installment.id)
        reminders.schedule()
    }

    override suspend fun getInstallmentById(
        id: Long
    ): Installment? {

        return dao
            .getInstallmentById(id)
            ?.toModel()

    }

    override suspend fun updateInstallment(
        installment: Installment
    ) {

        validate(installment)
        val old = requireNotNull(dao.getInstallmentById(installment.id))
        dao.updateInstallment(old.copy(title = installment.title, totalAmount = installment.totalAmount, paidAmount = installment.paidAmount,
            dueDate = installment.dueDate, note = installment.note, walletId = installment.walletId, isPaid = installment.isPaid,
            installmentCount = installment.installmentCount, startDate = installment.startDate, automaticPayment = installment.automaticPayment,
            updatedAt = System.currentTimeMillis()))
        reminders.clear(installment.id)
        reminders.schedule()

    }
}

