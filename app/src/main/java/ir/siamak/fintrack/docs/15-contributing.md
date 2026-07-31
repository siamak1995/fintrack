# Contributing Guide

<div align="center">

# Contributing to FinTrack

Thank you for your interest in contributing to FinTrack.

</div>

---

# Philosophy

FinTrack is designed as a long-term open-source financial platform.

Every contribution should improve at least one of these:

- Code Quality
- User Experience
- Performance
- Documentation
- Architecture
- Security

---

# Development Workflow

```
Fork

↓

Create Branch

↓

Develop

↓

Test

↓

Commit

↓

Push

↓

Pull Request

↓

Review

↓

Merge
```

---

# Branch Naming

Feature

```
feature/wallet-transfer
```

Bug

```
bugfix/dashboard-crash
```

Hotfix

```
hotfix/transaction-save
```

Refactor

```
refactor/report-engine
```

Documentation

```
docs/readme-update
```

---

# Commit Convention

```
feat:
```

Example

```
feat(wallet): add transfer support
```

---

```
fix:
```

Example

```
fix(report): monthly chart bug
```

---

```
refactor:
```

Example

```
refactor(repository): simplify transaction repository
```

---

```
docs:
```

Example

```
docs(database): update ERD
```

---

```
style:
```

Formatting only.

---

```
test:
```

Testing.

---

```
chore:
```

Maintenance.

---

# Pull Request Checklist

Before opening a PR:

- Project builds successfully
- No warnings
- No lint errors
- UI tested
- Documentation updated
- Database migration added if required
- Screenshots attached for UI changes

---

# Code Review Rules

Every Pull Request should:

- be focused
- solve one problem
- contain clear description
- include screenshots (if UI)
- include migration notes (if database)

---

# Kotlin Rules

Always prefer

- Immutable state
- data class
- Flow
- StateFlow
- Coroutines

Avoid

- Global mutable state
- Static utilities
- Business logic inside UI

---

# Compose Rules

UI must remain Stateless.

ViewModel owns state.

Composable functions should be reusable.

No database access inside UI.

---

# Architecture Rules

Presentation

↓

Domain

↓

Data

Never reverse dependencies.

---

# Documentation Rules

Every public class

must contain KDoc.

Every complex algorithm

must include explanation.

---

# Issue Labels

Bug

Enhancement

Documentation

Question

Performance

Security

AI

Premium

Roadmap

Good First Issue

Help Wanted

---

# Versioning

Semantic Versioning

```
MAJOR.MINOR.PATCH
```

Example

```
2.3.1
```

---

# License

By contributing,

you agree that your code becomes part of the FinTrack project
under the project's license.

---

# Thank You

Every contribution helps FinTrack become a better financial management platform.