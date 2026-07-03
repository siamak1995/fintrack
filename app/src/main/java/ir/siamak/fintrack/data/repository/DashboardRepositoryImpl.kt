package ir.siamak.fintrack.data.dashboard

import ir.siamak.fintrack.domain.analytics.DashboardData
import ir.siamak.fintrack.domain.dashboard.DashboardRepository
import ir.siamak.fintrack.domain.usecase.installments.InstallmentUseCases
import ir.siamak.fintrack.domain.usecase.member.MemberUseCases
import ir.siamak.fintrack.domain.usecase.transaction.TransactionUseCases
import ir.siamak.fintrack.domain.usecase.wallet.WalletUseCases
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(

    private val walletUseCases: WalletUseCases,

    private val transactionUseCases: TransactionUseCases,

    private val memberUseCases: MemberUseCases,

    private val installmentUseCases: InstallmentUseCases

) : DashboardRepository {

    override fun dashboardData(): Flow<DashboardData> {

        return combine(

            walletUseCases.getAllWallets(),

            transactionUseCases.getAllTransactions(),

            memberUseCases.getAllMembers(),

            installmentUseCases.getAllInstallments()

        ) {

                wallets,

                transactions,

                members,

                installments ->

            DashboardData(

                wallets,

                transactions,

                members,

                installments

            )

        }

    }

}