package ir.siamak.fintrack.domain.settings

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * مشاهده زنده تنظیمات برنامه.
 */
class ObserveSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> {
        return repository.observeSettings()
    }
}
