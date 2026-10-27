package ir.siamak.fintrack.personalaccountant.data.repository

import ir.siamak.fintrack.personalaccountant.data.local.dao.InstallmentDao
import ir.siamak.fintrack.personalaccountant.data.mapper.toEntity
import ir.siamak.fintrack.personalaccountant.data.mapper.toModel
import ir.siamak.fintrack.personalaccountant.data.model.Installment
import ir.siamak.fintrack.personalaccountant.domain.repository.InstallmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import androidx.room.withTransaction
import ir.siamak.fintrack.di.PersonalAccountingDatabase
import ir.siamak.fintrack.personalaccountant.data.local.database.AppDatabase

class InstallmentRepositoryImpl @Inject constructor(
    private val dao: InstallmentDao,
    @PersonalAccountingDatabase private val db: AppDatabase,
    private val reminders: ir.siamak.fintrack.personalaccountant.notifications.InstallmentReminders
) : InstallmentRepository {
    private fun validate(row: Installment) {
        require(row.title.isNotBlank() && row.totalAmount.isFinite() && row.totalAmount > 0 && row.paidAmount.isFinite() && row.paidAmount in 0.0..row.totalAmount)
        require(row.openingPaidAmount.isFinite() && row.openingPaidAmount >= 0 && row.openingPaidAmount <= row.paidAmount)
        require(row.principalAmount == null || (row.principalAmount.isFinite() && row.principalAmount > 0 && row.principalAmount <= row.totalAmount))
        require(row.annualInterestRate == null || (row.annualInterestRate.isFinite() && row.annualInterestRate in 0.0..100.0))
        require(row.installmentAmount == null || (row.installmentAmount.isFinite() && row.installmentAmount > 0))
        require(row.installmentCount in 1..600 && row.startDate <= row.dueDate) { "برنامه اقساط معتبر نیست" }
    }


    override fun getAllInstallments(): Flow<List<Installment>> {
        return dao.getAllInstallments().map { entities ->
            entities.map { it.toModel() } // اصلاح شد از toInstallment به toModel
        }
    }

    override suspend fun insertInstallment(installment: Installment) {
        validate(installment)
        dao.insertInstallment(installment.copy(paidAmount = installment.openingPaidAmount).toEntity())
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

        db.withTransaction {
            val old = requireNotNull(dao.getInstallmentById(installment.id))
            require(old.contextId == installment.contextId && !old.isDeleted)
            val paid = dao.linkedPaymentAmounts(installment.id).fold(java.math.BigDecimal.valueOf(installment.openingPaidAmount)) { total,amount ->
                total.add(java.math.BigDecimal.valueOf(amount))
            }.toDouble()
            val row = installment.copy(paidAmount = paid,isPaid = paid >= installment.totalAmount)
            validate(row)
            dao.updateInstallment(old.copy(title = row.title,totalAmount = row.totalAmount,paidAmount = row.paidAmount,
                dueDate = row.dueDate,note = row.note,walletId = row.walletId,isPaid = row.isPaid,
                installmentCount = row.installmentCount,startDate = row.startDate,automaticPayment = row.automaticPayment,
                principalAmount = row.principalAmount,annualInterestRate = row.annualInterestRate,installmentAmount = row.installmentAmount,
                openingPaidAmount = row.openingPaidAmount,endDate = row.endDate,interestMethod = row.interestMethod,updatedAt = System.currentTimeMillis()))
        }
        reminders.clear(installment.id)
        reminders.schedule()

    }
}

