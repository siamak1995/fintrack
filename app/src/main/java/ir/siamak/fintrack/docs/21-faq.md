# Frequently Asked Questions (FAQ)

# FinTrack FAQ

This document answers the most common questions about the project, its architecture, development process, and future plans.

---

# General

## What is FinTrack?

FinTrack is an Offline-First Personal Financial Management Platform designed for Persian-speaking users.

It helps users manage

- Income
- Expenses
- Wallets
- Installments
- Budgets
- Financial Reports

while preparing the foundation for future AI-powered financial assistance.

---

## Is FinTrack open source?

Yes.

The project is designed to be fully maintainable, extensible and community friendly.

---

## Why was FinTrack created?

Most existing personal finance applications are either

- Too simple
- Too complicated
- Cloud dependent
- Not optimized for Persian users

FinTrack aims to provide a modern alternative with high performance, privacy and scalability.

---

# Technical Questions

## Which programming language is used?

Kotlin

---

## Which UI framework is used?

Jetpack Compose

Material Design 3

---

## Which architecture is used?

Clean Architecture

MVVM

Repository Pattern

Use Cases

Dependency Injection

---

## Which database is used?

Room (SQLite)

---

## Which Dependency Injection framework is used?

Hilt

---

## Is the project Offline First?

Yes.

The application is fully usable without an internet connection.

---

## Does it support Persian Calendar?

Yes.

Persian Calendar is the primary calendar used throughout the application.

---

## Does it support RTL?

Yes.

RTL is supported across the entire user interface.

---

# Data

## Is user data stored in the cloud?

Current version

No.

Everything is stored locally.

Future versions may introduce optional cloud synchronization.

---

## Can data be backed up?

Planned.

Future versions will support

- Local Backup
- Cloud Backup
- Restore

---

## Is user data encrypted?

Preference encryption and database protection are planned for future releases.

---

# Features

## Can multiple wallets be created?

Yes.

---

## Can money be transferred between wallets?

Yes.

Transfers are treated as independent transaction types.

---

## Are reports available?

Yes.

Current reports include

- Wallet
- Member
- History
- Filtered Reports

More advanced reports are planned.

---

## Does FinTrack support budgets?

Not yet.

Budget management is planned.

---

## Are installments supported?

Yes.

The feature will continue to evolve.

---

## Are financial goals supported?

Planned.

---

## Can recurring transactions be created?

Planned.

---

## Can subscriptions be managed?

Planned.

---

# Premium

## Will FinTrack include Premium features?

Yes.

The application follows a Freemium model.

---

## Which features may become Premium?

Examples

- Advanced Reports
- Cloud Backup
- AI Analysis
- Smart Dashboard
- Unlimited Categories
- Export Center

---

# Artificial Intelligence

## Will AI be integrated?

Yes.

Future versions will include

- AI Assistant
- Financial Recommendations
- Budget Prediction
- Expense Prediction
- Spending Analysis
- Financial Health Score

---

## Will AI require internet?

Depends on implementation.

Possible options

- Local AI
- Cloud AI
- Hybrid

---

# Development

## Can I contribute?

Yes.

Please read

```
docs/13-contributing.md
```

---

## How should I report bugs?

Open an Issue including

- Android Version
- Device
- Steps to reproduce
- Expected behavior
- Actual behavior
- Screenshots if available

---

## Which branch should I use?

```
develop
```

for active development.

---

# Future

## Will FinTrack support iOS?

Not currently.

Android is the primary platform.

---

## Will a web version exist?

Possibly.

Architecture decisions keep this possibility open.

---

## Will synchronization be added?

Yes.

Cloud synchronization is planned after the core product becomes stable.

---

## Will SaaS be supported?

Potentially.

The architecture is intentionally designed to allow future SaaS expansion.

---

# Business

## Who is the primary audience?

Persian-speaking individuals and families.

---

## Is FinTrack intended for companies?

Not initially.

However, the architecture allows future expansion toward small business finance.

---

## Will there be advertisements?

The preferred long-term business model is Freemium with Premium subscriptions rather than advertising.

---

# Vision

## What is the ultimate goal of FinTrack?

The long-term vision is to evolve from a simple expense tracker into a complete intelligent financial platform capable of helping users understand, predict and improve their financial lives through modern software architecture and artificial intelligence.