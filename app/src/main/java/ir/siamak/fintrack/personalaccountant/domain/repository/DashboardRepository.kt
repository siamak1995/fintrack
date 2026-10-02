package ir.siamak.fintrack.personalaccountant.domain.dashboard

import kotlinx.coroutines.flow.Flow

interface DashboardRepository {

    fun dashboardData(): Flow<DashboardData>

}
