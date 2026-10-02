package ir.siamak.fintrack.store.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Persisted installment schedule entry. */
@Entity(tableName = "installments") data class InstallmentEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val paymentId: Long, val monthNumber: Int, val percentage: Int, val amount: Long, val dueDate: String)
