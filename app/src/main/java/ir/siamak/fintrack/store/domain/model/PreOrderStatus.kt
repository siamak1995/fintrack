package ir.siamak.fintrack.store.domain.model
import kotlinx.serialization.Serializable
@Serializable
enum class PreOrderStatus { NEW, IN_PROGRESS, READY, DELIVERED, CANCELLED }
