package ir.siamak.fintrack.di

import javax.inject.Qualifier

/** Marks dependencies that belong to the personal-accounting database graph. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PersonalAccountingDatabase

/** Marks dependencies that belong to the store-accounting database graph. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StoreAccountingDatabase
