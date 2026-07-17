package ir.siamak.fintrack.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.siamak.fintrack.presentation.dashboard.components.DashboardEmpty
import ir.siamak.fintrack.presentation.dashboard.components.DashboardError
import ir.siamak.fintrack.presentation.dashboard.components.DashboardLoading
import ir.siamak.fintrack.presentation.dashboard.components.sections.ChartSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.FinancialSummarySection
import ir.siamak.fintrack.presentation.dashboard.components.sections.GreetingSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.InsightSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.MemberSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.QuickActionsSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.RecentTransactionsSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.UpcomingInstallmentsSection
import ir.siamak.fintrack.presentation.dashboard.components.sections.WalletSection

@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction:(DashboardUiAction)->Unit={}
){

    Scaffold(

        floatingActionButton={

            ExtendedFloatingActionButton(

                onClick={
                    onAction(DashboardUiAction.AddTransaction)
                },

                icon={
                    Icon(
                        Icons.Default.Add,
                        null
                    )
                },

                text={
                    Text("ثبت تراکنش")
                }

            )

        }

    ){padding->

        when{

            state.isLoading->{
                DashboardLoading()
                return@Scaffold
            }

            state.error!=null->{
                DashboardError(state.error)
                return@Scaffold
            }

            state.wallets.isEmpty() &&
                    state.transactions.isEmpty() &&
                    state.members.isEmpty()->{

                DashboardEmpty(
                    message="هنوز اطلاعاتی ثبت نشده است."
                )

                return@Scaffold
            }

        }

        LazyColumn(

            modifier=Modifier
                .fillMaxSize()
                .padding(padding),

            contentPadding=PaddingValues(16.dp),

            verticalArrangement=Arrangement.spacedBy(18.dp)

        ){

            item{
                GreetingSection(
                    userName=state.userName,
                    insight=state.insight
                )
            }

            item{

                FinancialSummarySection(
                    totalBalance=state.totalBalance,
                    walletBalance=state.walletBalance,
                    income=state.monthlyIncome,
                    expense=state.monthlyExpense,
                    saving=state.saving,
                    todayIncome=state.todayIncome,
                    todayExpense=state.todayExpense
                )

            }

            item{

                ChartSection(
                    spending=state.spendingPercent,
                    saving=state.savingPercent,
                    health=state.financialHealth
                )

            }

//            item{
//
//                StatisticsSection(
//                    walletCount=state.walletCount,
//                    transactionCount=state.transactionCount,
//                    memberCount=state.memberCount,
//                    installmentCount=state.installmentCount
//                )
//
//            }

            item{

                QuickActionsSection(
                    onWalletClick={
                        onAction(DashboardUiAction.AddWallet)
                    },
                    onTransactionClick={
                        onAction(DashboardUiAction.AddTransaction)
                    },
                    onMemberClick={
                        onAction(DashboardUiAction.OpenMembers)
                    },
                    onInstallmentClick={
                        onAction(DashboardUiAction.OpenInstallments)
                    },
                    onReportClick={
                        onAction(DashboardUiAction.OpenReports)
                    }
                )

            }

            if(state.wallets.isNotEmpty()){

                item{

                    WalletSection(
                        wallets=state.wallets,
                        onWalletClick={
                            onAction(DashboardUiAction.EditWallet(it))
                        },
                        onAddWallet={
                            onAction(DashboardUiAction.AddWallet)
                        }
                    )

                }

            }

            if(state.members.isNotEmpty()){

                item{

                    MemberSection(

                        members=state.members,

                        onMembersClick={
                            onAction(
                                DashboardUiAction.OpenMembers
                            )
                        }

                    )

                }

            }

            item{

                InsightSection(

                    insight=state.insight

                )

            }

            item{

                RecentTransactionsSection(

                    transactions=state.recentTransactions

                )

            }

            if(state.upcomingInstallments.isNotEmpty()){

                item{

                    UpcomingInstallmentsSection(

                        installments=state.upcomingInstallments,

                        onShowAll={
                            onAction(
                                DashboardUiAction.OpenInstallments
                            )
                        }

                    )

                }

            }

        }

    }

}