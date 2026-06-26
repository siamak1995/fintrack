package ir.siamak.fintrack.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.siamak.fintrack.domain.repository.MemberRepository
import ir.siamak.fintrack.domain.usecase.wallet.DeleteWalletUseCase
import ir.siamak.fintrack.domain.usecase.wallet.GetAllWalletsUseCase
import ir.siamak.fintrack.domain.usecase.wallet.GetWalletByIdUseCase
import ir.siamak.fintrack.domain.usecase.wallet.InsertWalletUseCase
import ir.siamak.fintrack.domain.usecase.wallet.UpdateWalletUseCase
import ir.siamak.fintrack.domain.usecase.wallet.WalletUseCases
import ir.siamak.fintrack.domain.repository.TransactionRepository
import ir.siamak.fintrack.domain.usecase.installment.InstallmentUseCases
import ir.siamak.fintrack.domain.usecase.installments.GetAllInstallmentsUseCase
import ir.siamak.fintrack.domain.usecase.transaction.DeleteTransactionUseCase
import ir.siamak.fintrack.domain.usecase.transaction.GetAllTransactionsUseCase
import ir.siamak.fintrack.domain.usecase.transaction.GetTransactionsByWalletUseCase
import ir.siamak.fintrack.domain.usecase.transaction.InsertTransactionUseCase
import ir.siamak.fintrack.domain.usecase.transaction.TransactionUseCases
import ir.siamak.fintrack.domain.usecase.transaction.UpdateTransactionUseCase
import ir.siamak.fintrack.domain.usecase.member.DeleteMemberUseCase
import ir.siamak.fintrack.domain.usecase.member.GetAllMembersUseCase
import ir.siamak.fintrack.domain.usecase.member.GetMemberByIdUseCase
import ir.siamak.fintrack.domain.usecase.member.InsertMemberUseCase
import ir.siamak.fintrack.domain.usecase.member.MemberUseCases
import ir.siamak.fintrack.domain.usecase.member.UpdateMemberUseCase


/**
 * ماژول تزریق وابستگی مربوط به UseCaseهای برنامه.
 *
 * در این ماژول، UseCaseهای مرتبط با Wallet در قالب یک کلاس تجمیعی
 * به نام [WalletUseCases] فراهم می‌شوند تا در ViewModelها راحت‌تر
 * تزریق و استفاده شوند.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    /**
     * فراهم کردن مجموعه UseCaseهای مرتبط با Wallet.
     *
     * @return نمونه‌ای از [WalletUseCases]
     */
    @Provides
    fun provideWalletUseCases(
        getAllWalletsUseCase: GetAllWalletsUseCase,
        getWalletByIdUseCase: GetWalletByIdUseCase,
        insertWalletUseCase: InsertWalletUseCase,
        updateWalletUseCase: UpdateWalletUseCase,
        deleteWalletUseCase: DeleteWalletUseCase
    ): WalletUseCases {
        return WalletUseCases(
            getAllWallets = getAllWalletsUseCase,
            getWalletById = getWalletByIdUseCase,
            insertWallet = insertWalletUseCase,
            updateWallet = updateWalletUseCase,
            deleteWallet = deleteWalletUseCase
        )
    }

    /**
     * فراهم کردن مجموعه UseCaseهای مرتبط با Transaction.
     *
     * @return نمونه‌ای از [TransactionUseCases]
     */
    @Provides
    fun provideTransactionUseCases(
        repository: TransactionRepository
    ): TransactionUseCases {
        return TransactionUseCases(
            getAllTransactions = GetAllTransactionsUseCase(repository),
            getTransactionsByWallet = GetTransactionsByWalletUseCase(repository),
            insertTransaction = InsertTransactionUseCase(repository),
            updateTransaction = UpdateTransactionUseCase(repository),
            deleteTransaction = DeleteTransactionUseCase(repository)
        )
    }

    @Provides
    fun provideMemberUseCases(
        repository: MemberRepository
    ): MemberUseCases {
        return MemberUseCases(
            getAllMembers = GetAllMembersUseCase(repository),
            getMemberById = GetMemberByIdUseCase(repository),
            insertMember = InsertMemberUseCase(repository),
            updateMember = UpdateMemberUseCase(repository),
            deleteMember = DeleteMemberUseCase(repository)
        )
    }


    @Provides
    fun provideInstallmentUseCases(
        getAllInstallmentsUseCase: GetAllInstallmentsUseCase
    ): InstallmentUseCases {
        return InstallmentUseCases(
            getAllInstallments = getAllInstallmentsUseCase
        )
    }

}
