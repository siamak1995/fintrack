# Context and Migration Baseline

## Active module decision

`ir.siamak.fintrack.storeaccountant` is the active store-accounting module because the application navigation imports its routes and bottom bar. The separate `ir.siamak.fintrack.store` package is not wired to Hilt or navigation and must not receive parallel feature development.

## Current database facts

| Database | Class | Declared version | Provider migrations | Schema export |
| --- | --- | ---: | --- | --- |
| Personal | `personalaccountant.data.local.database.AppDatabase` | 7 | `1->2` through `6->7` | enabled |
| Store | `storeaccountant.data.local.StoreDatabase` | 1 | none | disabled |

`AppDatabase` version 7 activates the existing `6->7` repair that adds legacy installment sync columns when missing. The generated schemas for versions 6 and 7 must remain committed. `StoreDatabase` has no published migration history, so its first schema change must start with a tested `1->2` migration.

## Context migration plan

1. Enable Room schema export and commit generated version-6 and version-1 schemas.
2. Add a persistent context table and create a default personal context plus a default store context.
3. Add non-null `contextId` columns with a legacy default context only after the context table migration can be executed atomically.
4. Add scoped indexes and revise DAO queries to require `contextId`.
5. Write MigrationTestHelper fixtures containing existing wallets, transactions, tags, members, installments, store records, sellers, products, materials, and sales.
6. Validate migration data and foreign keys before updating navigation or enabling Context switching in UI.

No destructive fallback is permitted for an upgrade path. Context selection is currently persisted in DataStore only; it does not yet alter Room queries.
