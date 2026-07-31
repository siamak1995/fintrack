# Technical Decisions (ADR)

آخرین بروزرسانی: 2026

---

# مقدمه

این سند تصمیمات مهم فنی (Architecture Decision Records - ADR) پروژه FinTrack را ثبت می‌کند.

هدف از این سند جلوگیری از تصمیمات متناقض در آینده و مستندسازی دلایل انتخاب فناوری‌ها و الگوهای معماری است.

---

# ADR-001

## انتخاب زبان Kotlin

### وضعیت

Accepted

### تصمیم

کل پروژه با Kotlin توسعه داده می‌شود.

### دلیل

- زبان رسمی Android
- Null Safety
- Coroutines
- خوانایی بالا
- پشتیبانی عالی از Compose

---

# ADR-002

## Jetpack Compose

### وضعیت

Accepted

### تصمیم

تمام رابط کاربری با Compose نوشته می‌شود.

### دلیل

- Declarative UI
- سرعت توسعه بیشتر
- Preview
- State Management بهتر
- آینده Android

---

# ADR-003

## Material Design 3

### وضعیت

Accepted

### دلیل

- استاندارد رسمی گوگل
- Dynamic Color
- Dark Theme
- Accessibility
- طراحی مدرن

---

# ADR-004

## Clean Architecture

### وضعیت

Accepted

### لایه‌ها

- Presentation
- Domain
- Data

### دلیل

- تست‌پذیری
- توسعه‌پذیری
- کاهش وابستگی
- نگهداری آسان

---

# ADR-005

## MVVM

### وضعیت

Accepted

### دلیل

- هماهنگی کامل با Compose
- پشتیبانی ViewModel
- StateFlow
- Lifecycle Aware

---

# ADR-006

## Repository Pattern

### وضعیت

Accepted

### دلیل

- جداسازی Data Source
- قابلیت Mock
- تست آسان
- توسعه ساده

---

# ADR-007

## UseCase Pattern

### وضعیت

Accepted

### دلیل

تمام منطق Business باید داخل UseCaseها قرار گیرد.

Composable و ViewModel نباید Business Logic داشته باشند.

---

# ADR-008

## Hilt

### وضعیت

Accepted

### دلیل

- Dependency Injection رسمی
- سادگی
- تست آسان
- نگهداری بهتر

---

# ADR-009

## Room

### وضعیت

Accepted

### دلیل

- ORM رسمی اندروید
- Migration
- Flow Support
- Type Safety

---

# ADR-010

## DataStore

### وضعیت

Accepted

### دلیل

جایگزین SharedPreferences

---

# ADR-011

## StateFlow

### وضعیت

Accepted

### دلیل

- Reactive UI
- Lifecycle Safe
- Compose Friendly

---

# ADR-012

## Navigation Compose

### وضعیت

Accepted

### دلیل

- Type Safe Navigation
- Compose Integration
- Serialization

---

# ADR-013

## Persian Calendar

### وضعیت

Accepted

### دلیل

بازار هدف ایران است.

تمام تاریخ‌های برنامه باید قابلیت نمایش شمسی داشته باشند.

---

# ADR-014

## RTL First

### وضعیت

Accepted

### تصمیم

تمام طراحی ابتدا برای RTL انجام می‌شود.

---

# ADR-015

## Offline First

### وضعیت

Accepted

### تصمیم

برنامه بدون اینترنت نیز باید قابل استفاده باشد.

---

# ADR-016

## Future Cloud Sync

### وضعیت

Planned

### تصمیم

در آینده Repositoryها قابلیت اتصال به Cloud را خواهند داشت.

---

# ADR-017

## Premium Features

### وضعیت

Accepted

### تصمیم

برخی قابلیت‌ها فقط در نسخه Premium فعال خواهند شد.

نمونه‌ها:

- AI
- Export PDF
- Export Excel
- Forecast
- Advanced Reports

---

# ADR-018

## AI Ready Architecture

### وضعیت

Accepted

### تصمیم

تمام معماری باید قابلیت اضافه شدن ماژول‌های AI را داشته باشد.

---

# ADR-019

## Modularization

### وضعیت

Planned

### تصمیم

در صورت بزرگ شدن پروژه، به Multi Module مهاجرت خواهد شد.

نمونه:

```
core

domain

data

presentation

feature-wallet

feature-report

feature-settings

feature-dashboard

feature-ai
```

---

# ADR-020

## Coding Philosophy

اصول اصلی توسعه پروژه:

- SOLID
- DRY
- KISS
- YAGNI
- Clean Code
- Composition over Inheritance
- Immutable State
- Single Source of Truth

---

# نحوه ثبت تصمیم جدید

هر تصمیم جدید باید شامل موارد زیر باشد:

- شناسه (ADR-XXX)
- عنوان
- وضعیت
- مسئله
- تصمیم
- دلیل انتخاب
- مزایا
- معایب
- تأثیر بر پروژه
- تاریخ ثبت

---

# نتیجه

تمام توسعه‌دهندگان پروژه موظف هستند پیش از ایجاد تغییرات معماری، این سند را مطالعه کرده و تصمیمات ثبت‌شده را رعایت کنند.