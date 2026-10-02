# Context and Migration Baseline

## Active module decision

`ir.siamak.fintrack.storeaccountant` is the active store-accounting module because the application navigation imports its routes and bottom bar. The separate `ir.siamak.fintrack.store` package is not wired to Hilt or navigation and must not receive parallel feature development.

## Current database facts

| Database | Class | Declared version | Provider migrations | Schema export |
| --- | --- | ---: | --- | --- |
| Personal | `personalaccountant.data.local.database.AppDatabase` | 8 | `1->2` through `7->8` | enabled |
| Store | `storeaccountant.data.local.StoreDatabase` | 1 | none | enabled |

`AppDatabase` version 7 activates the existing `6->7` repair that adds legacy installment sync columns when missing. Version 8 introduces the context registry and non-null scoped columns. The generated schemas for versions 6, 7, and 8 must remain committed. `StoreDatabase` has no published migration history, so its first schema change must start with a tested `1->2` migration.

## Context migration plan

1. `accountant_contexts` is the durable registry; DataStore persists only the selected context id.
2. Stable ids are Personal=`1` and Store=`2`; all v7 legacy personal records migrate to Personal.
3. `Wallet`, `Transaction`, `Member`, `Tag`, and `Installment` have non-null `Long contextId` with default `1`; `TransactionTagCrossRef` is scoped through its parent records.
4. Version `7->8` adds the registry, columns, and indexes atomically. Foreign keys are deferred to a later rebuild migration so this migration remains non-destructive.
5. `StoreDatabase` is explicitly out of scope for `7->8` and remains version 1.
6. DAO/repository query scoping follows this schema migration and is not changed by it.

No destructive fallback is permitted for an upgrade path. Context selection is currently persisted in DataStore only; it does not yet alter Room queries.
