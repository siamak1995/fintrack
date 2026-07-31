# FinTrack Coding Guidelines

Version: 1.0

---

# Goal

This document defines the coding standards for the FinTrack project.

Every new feature must follow these rules.

The project follows:

- Clean Architecture
- MVVM
- SOLID
- Clean Code
- Material Design 3
- Kotlin Best Practices

---

# Package Structure

```
presentation/

domain/

data/

di/

core/
```

Never place business logic inside Presentation.

---

# Presentation Layer

Contains only:

Screen

Route

ViewModel

State

Event

Components

Navigation

Theme

Example

```
presentation/
    dashboard/
        DashboardScreen.kt
        DashboardRoute.kt
        DashboardViewModel.kt
        DashboardState.kt
        DashboardEvent.kt
```

---

# Screen Rules

Every screen must have:

Screen

Route

ViewModel

State

Event

Example

```
WalletScreen

WalletRoute

WalletViewModel

WalletState

WalletEvent
```

---

# UI Rules

UI must never:

Access Room

Access Repository

Contain business logic

Contain calculations

Wrong

```
val total = transactions.sumOf { it.amount }
```

Correct

```
val total = state.totalBalance
```

---

# ViewModel Rules

ViewModel responsibilities:

Receive Events

Call UseCases

Update State

Emit Effects

Never:

Access DAO

Access Database

Create SQL

---

# State Rules

State must be immutable.

Correct

```
data class WalletState(...)
```

Wrong

```
class WalletState{
    var loading=false
}
```

---

# Event Rules

Events describe user actions.

Examples

```
Save

Delete

Refresh

Search

ChangeCurrency

ToggleTheme
```

Events must never contain UI code.

---

# Domain Layer

Contains only business logic.

Allowed:

Repository Interfaces

UseCases

Validators

Calculators

Analytics

Forbidden:

Compose

Room

Android Context

Resources

Navigation

---

# UseCase Rules

One responsibility only.

Correct

```
InsertWalletUseCase
```

Wrong

```
WalletManagerUseCase
```

---

# Repository Rules

Always create Interface.

Correct

```
WalletRepository
```

Implementation

```
WalletRepositoryImpl
```

Presentation never imports RepositoryImpl.

---

# Data Layer

Contains

DAO

RepositoryImpl

Mapper

Database

Entities

Nothing else.

---

# Room Rules

One DAO per Entity.

DAO names

WalletDao

MemberDao

TransactionDao

TagDao

InstallmentDao

---

# Compose Rules

Small Composables.

Avoid files larger than 300 lines.

Split UI.

Correct

```
WalletScreen

WalletList

WalletCard

WalletItem
```

Wrong

```
WalletScreen
(900 lines)
```

---

# Components

Reusable components go inside

presentation/components

Examples

MoneyText

FTCard

SectionHeader

SummaryCard

PrimaryButton

TopBar

BottomBar

---

# Naming

Classes

PascalCase

WalletViewModel

Functions

camelCase

insertWallet()

Variables

camelCase

walletName

Constants

UPPER_CASE

MAX_LENGTH

Packages

lowercase

presentation.dashboard

---

# Documentation

Every public class must contain KDoc.

Example

```
/**
 * Wallet repository.
 */
```

Every complex function must explain:

Purpose

Parameters

Return value

---

# Comments

Good

Explain WHY.

Bad

Explain WHAT.

Wrong

```
// add 1
count++
```

Correct

```
// Required because Room does not support...
```

---

# Dependency Injection

Only Hilt.

Never create Repository manually.

Wrong

```
WalletRepositoryImpl()
```

Correct

```
@Inject
```

---

# Coroutines

Repository

↓

Flow

↓

UseCase

↓

ViewModel

↓

StateFlow

↓

Compose

Never use GlobalScope.

Always use viewModelScope.

---

# Navigation

Always use Type Safe Navigation.

Wrong

```
navigate("wallet")
```

Correct

```
navigate(Screen.WalletList)
```

---

# Theme

Always use

MaterialTheme.colorScheme

Never use random colors.

Wrong

```
Color.Red
```

Correct

```
MaterialTheme.colorScheme.error
```

---

# Strings

No hardcoded text.

Wrong

```
Text("Save")
```

Correct

```
stringResource(...)
```

(Currently acceptable during development, but all strings must move to resources before release.)

---

# Performance

Use remember when needed.

Use derivedStateOf when appropriate.

LazyColumn for long lists.

Stable State.

Avoid unnecessary recomposition.

---

# Error Handling

Never ignore exceptions.

Repository returns Result.

ViewModel updates Error State.

UI displays Snackbar/Dialog.

---

# Testing

Future structure

test/

androidTest/

Goals

Repository Tests

UseCase Tests

ViewModel Tests

UI Tests

---

# Git

Commit examples

feat(wallet): add wallet search

fix(report): monthly filter bug

refactor(settings): split appearance section

docs(architecture): update clean architecture

---

# Branch Strategy

main

develop

feature/*

hotfix/*

release/*

---

# Project Philosophy

Readable code is more valuable than clever code.

Prefer simplicity over complexity.

Every feature should be reusable.

Every screen should be maintainable.

Every class should have a single responsibility.

If a file feels too large, split it.

If a function becomes difficult to explain, redesign it.

The goal is to build a production-grade personal finance application that remains maintainable for years.