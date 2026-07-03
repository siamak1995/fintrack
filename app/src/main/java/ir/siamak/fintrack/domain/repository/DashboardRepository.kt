package ir.siamak.fintrack.domain.dashboard

import ir.siamak.fintrack.domain.analytics.DashboardData
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {

    fun dashboardData(): Flow<DashboardData>

}