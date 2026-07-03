package ir.siamak.fintrack.domain.dashboard

import kotlinx.coroutines.flow.Flow

interface DashboardRepository {

    fun dashboardData(): Flow<DashboardData>

}