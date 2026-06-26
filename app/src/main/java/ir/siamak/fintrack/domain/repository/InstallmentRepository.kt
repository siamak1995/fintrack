package ir.siamak.fintrack.domain.repository
import ir.siamak.fintrack.data.model.Installment
import kotlinx.coroutines.flow.Flow

interface InstallmentRepository {
    fun getAllInstallments(): Flow<List<Installment>>
}
