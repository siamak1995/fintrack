package ir.siamak.fintrack.store.domain.repository

import ir.siamak.fintrack.store.domain.model.RawMaterial
import kotlinx.coroutines.flow.Flow

/** Raw-material inventory data boundary. */
interface MaterialRepository { fun observeAll(): Flow<List<RawMaterial>>; suspend fun save(material: RawMaterial); suspend fun delete(id: Long) }
