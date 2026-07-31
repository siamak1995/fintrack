# Module Guide

---

# مقدمه

FinTrack بر اساس معماری Clean Architecture توسعه یافته است.

هر ماژول مسئولیت مشخصی دارد و تنها با لایه‌های مجاز ارتباط برقرار می‌کند.

این ساختار باعث می‌شود پروژه توسعه‌پذیر، تست‌پذیر و قابل نگهداری باشد.

---

# ساختار کلی پروژه

```
app
│
├── core
├── data
├── domain
├── presentation
├── di
└── navigation
```

---

# Core Module

مسئول نگهداری کدهای عمومی پروژه.

نمونه‌ها

- Extensions
- Utilities
- Constants
- Date Helpers
- Currency Formatter
- Persian Calendar
- Validators

Core نباید وابسته به سایر Featureها باشد.

---

# Data Module

پیاده‌سازی واقعی دسترسی به داده‌ها.

شامل

- Room
- DAO
- Entity
- Repository Implementation
- DataStore
- Mapper
- Local Data Source

این لایه Interfaceهای Domain را پیاده‌سازی می‌کند.

---

# Domain Module

هسته اصلی منطق کسب‌وکار.

شامل

- Model
- Repository Interface
- UseCase
- Calculator
- Business Rules

هیچ وابستگی به Android ندارد.

---

# Presentation Module

تمام رابط کاربری پروژه.

شامل

- Screen
- ViewModel
- State
- Event
- Component
- Navigation Route

این لایه فقط با UseCaseها کار می‌کند.

---

# DI Module

تمام وابستگی‌های پروژه توسط Hilt در این بخش مدیریت می‌شوند.

نمونه‌ها

- Database Module
- Repository Module
- UseCase Module

---

# Navigation Module

تمام مسیرهای برنامه در یک محل نگهداری می‌شوند.

وظایف

- Screen Contract
- Navigation Graph
- Bottom Navigation
- Route Management

---

# Theme Module

تمام قوانین طراحی ظاهری پروژه.

شامل

- Color
- Typography
- Shape
- Spacing
- Dimension
- Dynamic Color

---

# Feature Modules

هر قابلیت پروژه به صورت مستقل توسعه داده می‌شود.

نمونه‌ها

- Dashboard
- Wallet
- Transaction
- Member
- Report
- Settings
- Tags
- Installment

---

# Dashboard Module

وظایف

- نمایش KPI
- Summary
- Quick Actions
- وضعیت مالی
- آمار روزانه

---

# Wallet Module

وظایف

- مدیریت حساب‌ها
- موجودی
- انتقال وجه
- گزارش حساب

---

# Transaction Module

وظایف

- ثبت درآمد
- ثبت هزینه
- انتقال
- ویرایش
- حذف

---

# Member Module

وظایف

- مدیریت اعضای خانواده
- گزارش اعضا
- تخصیص تراکنش

---

# Tag Module

وظایف

- مدیریت برچسب‌ها
- دسته‌بندی تراکنش‌ها
- تحلیل گزارش‌ها

---

# Installment Module

وظایف

- ثبت اقساط
- مدیریت بدهی
- مدیریت پرداخت
- یادآوری

---

# Reports Module

وظایف

- گزارش‌های مالی
- نمودارها
- تحلیل روند
- Export

---

# Settings Module

وظایف

- تنظیمات ظاهری
- زبان
- امنیت
- اعلان‌ها
- DataStore

---

# AI Module (Future)

در نسخه‌های آینده اضافه خواهد شد.

وظایف

- AI Assistant
- Chat
- Recommendation Engine
- Budget Advisor
- Prediction Engine
- Financial Insights

---

# Cloud Module (Future)

وظایف

- Backup
- Restore
- Sync
- Multi Device
- Authentication

---

# وابستگی بین ماژول‌ها

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
Room / DataStore
```

هیچ لایه‌ای اجازه وابستگی معکوس ندارد.

---

# قوانین توسعه

- هر Feature مستقل توسعه داده می‌شود.
- هر Feature دارای ViewModel اختصاصی است.
- تمام عملیات از طریق UseCase انجام می‌شود.
- هیچ ViewModel نباید مستقیماً Repository را فراخوانی کند.
- هیچ Screen نباید منطق تجاری داشته باشد.
- تمام قوانین کسب‌وکار در Domain نگهداری می‌شوند.

---

# هدف نهایی

این ساختار امکان توسعه تدریجی FinTrack از یک اپلیکیشن مدیریت هزینه به یک پلتفرم جامع مدیریت مالی شخصی و در آینده یک سرویس ابری مجهز به هوش مصنوعی را فراهم می‌کند.