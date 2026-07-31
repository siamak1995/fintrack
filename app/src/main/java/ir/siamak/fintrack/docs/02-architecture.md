# FinTrack Architecture

Version: 1.1

---

# Overview

FinTrack is implemented using Clean Architecture together with MVVM.

The application is fully offline-first and stores all user data locally using Room.

Dependency Injection is handled by Hilt.

UI is implemented completely using Jetpack Compose.

Navigation is fully Type-safe using Kotlin Serialization.

---

# Architecture Layers

```
Presentation
      │
      ▼
Domain
      │
      ▼
Data
      │
      ▼
Room Database
```

Each layer has only one direction of dependency.

Presentation

↓

Domain

↓

Data

---

# Presentation Layer

Responsible for:

- UI
- ViewModel
- Screen State
- Events
- Navigation
- Components

Folders

presentation/

    dashboard/

    wallet/

    member/

    transaction/

    report/

    settings/

    components/

    navigation/

Pattern:

```
Screen

↓

Route

↓

ViewModel

↓

UseCase

↓

Repository
```

Every screen should contain:

```
Screen

Route

ViewModel

State

Event

Effect (optional)
```

---

# Domain Layer

Contains only business logic.

No Android imports are allowed.

Contains:

Repositories (Interfaces)

UseCases

Analytics

Calculators

Validation

Example

```
WalletRepository

↓

GetWalletUseCase

↓

WalletCalculator
```

---

# Data Layer

Contains implementation.

Includes:

Room

DAO

RepositoryImpl

Mapper

Database

No UI code exists here.

---

# Dependency Injection

Framework:

Hilt

Modules:

DatabaseModule

RepositoryModule

UseCaseModule

All repositories are Singleton.

UseCases are injected into ViewModels.

---

# Database

Database:

Room

Current Database:

fintrack_db

Main Entities

Wallet

Transaction

Member

Tag

Installment

Relationships

Wallet

↓

Transaction

↓

Tag

Member

↓

Transaction

Installment

↓

Wallet

Future:

Workspace

Cloud Sync

Backup

---

# Navigation

Navigation is fully Type Safe.

No String Routes.

Example

```
Screen.Dashboard

Screen.Transactions

Screen.AddEditWallet(id)

Screen.Settings
```

---

# UI

Framework:

Jetpack Compose

Material 3

RTL Support

Persian Calendar

Dynamic Theme

Reusable Components:

FTCard

MoneyText

SummaryCard

SectionHeader

EmptySection

TopBar

BottomBar

---

# State Management

Every screen owns:

State

Event

ViewModel

Example

```
SettingsState

SettingsEvent

SettingsViewModel
```

State is immutable.

Events update State.

UI only observes State.

---

# Repository Pattern

Presentation

↓

UseCase

↓

Repository Interface

↓

RepositoryImpl

↓

DAO

↓

Room

---

# Current Features

Dashboard

Wallets

Transactions

Members

Reports

Tags

Settings

Landing

Persian Calendar

RTL Layout

Material Design 3

---

# Planned Features

Installment Management

Recurring Transactions

Budgets

Goals

Cloud Backup

Import / Export

Widgets

Charts

Premium Reports

AI Financial Analysis

OCR Receipt Scanner

Voice Input

Bank SMS Parser

Multiple Workspaces

Family Sharing

Authentication

Biometric Login

PIN Lock

Cloud Synchronization

---

# Technology Stack

Language

Kotlin 2.0

Architecture

Clean Architecture

Pattern

MVVM

UI

Jetpack Compose

Database

Room

Dependency Injection

Hilt

Navigation

Navigation Compose

Serialization

Kotlin Serialization

Storage

DataStore

Async

Coroutines + Flow

Minimum SDK

26

Target SDK

36

Java

21

---

# Design Principles

Single Source of Truth

Immutable UI State

Offline First

Reusable Components

Type Safe Navigation

Repository Pattern

Dependency Injection

Clean Code

SOLID

Material Design 3

RTL First

Persian Friendly

---

# Future Architecture

Phase 2

Installments

Budgets

Goals

Recurring Payments

Phase 3

Premium Reports

Charts

Cloud Backup

Phase 4

AI Assistant

Financial Insights

Smart Recommendations

Receipt OCR

Voice Commands

Phase 5

Marketplace Release

Google Play

Cafe Bazaar

Myket

Direct APK Distribution

Subscription Model

Premium Features

Enterprise Version

---

# Coding Standards

No business logic inside UI.

Every screen must have ViewModel.

Repositories must be interfaces.

Domain must not import Android.

UI must observe immutable State.

Navigation must be type-safe.

Reusable components belong inside presentation/components.

Every public class must contain documentation.

---

# Vision

FinTrack is intended to become the most complete Persian personal finance management application with modern architecture, beautiful UX, AI-powered financial insights, and premium reporting capabilities.