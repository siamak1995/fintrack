package ir.siamak.fintrack.data.local.converter

import androidx.room.TypeConverter
import ir.siamak.fintrack.data.model.TransactionType

class Converters {

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return TransactionType.valueOf(value)
    }
}
