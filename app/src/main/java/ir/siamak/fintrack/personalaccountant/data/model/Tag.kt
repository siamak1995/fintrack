package ir.siamak.fintrack.personalaccountant.data.model

data class Tag(
    val id: Long = 0L,
    val name: String,
    val color: Long? = null,
    val workspaceId: Long? = null,
    val allowedType: TransactionType = TransactionType.EXPENSE
)

