package ir.siamak.fintrack.data.local.audit

enum class SyncState {
    LOCAL_ONLY,
    PENDING_UPLOAD,
    SYNCED,
    CONFLICT,
    ERROR
}