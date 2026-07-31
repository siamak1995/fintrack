# Testing Guide

آخرین بروزرسانی: 2026

---

# هدف

این سند استاندارد تست نرم‌افزار FinTrack را تعریف می‌کند تا کیفیت، پایداری و صحت عملکرد برنامه در تمام نسخه‌ها حفظ شود.

---

# Testing Pyramid

```
                UI Tests
            ----------------
             Integration Tests
        --------------------------
              Unit Tests
```

هدف پروژه:

- Unit Test حدود 70%
- Integration Test حدود 20%
- UI Test حدود 10%

---

# Test Levels

## Unit Test

هدف:

تست منطق برنامه بدون وابستگی به Android Framework.

موارد تست:

- UseCase
- Calculator
- Validator
- Mapper
- Formatter
- Utility

---

## Integration Test

هدف:

تست ارتباط بین لایه‌ها.

موارد تست:

- Repository
- Room
- DataStore
- Navigation
- Dependency Injection

---

## UI Test

هدف:

تست رفتار رابط کاربری.

موارد تست:

- Compose UI
- Navigation
- فرم‌ها
- Dialogها
- Snackbar
- BottomSheet

---

# Dashboard Tests

باید بررسی شود:

- نمایش موجودی
- درآمد امروز
- هزینه امروز
- درآمد ماه
- هزینه ماه
- مجموع دارایی
- Quick Actions
- Recent Transactions

---

# Wallet Tests

- ایجاد حساب
- ویرایش
- حذف
- بروزرسانی موجودی
- جلوگیری از داده تکراری

---

# Transaction Tests

- ثبت درآمد
- ثبت هزینه
- انتقال بین حساب‌ها
- حذف
- ویرایش
- محاسبه موجودی

---

# Member Tests

- ایجاد عضو
- ویرایش
- حذف
- اتصال تراکنش

---

# Tag Tests

- ایجاد
- ویرایش
- حذف
- اتصال چند تگ
- حذف ارتباط

---

# Installment Tests

- ثبت قسط
- پرداخت
- محاسبه مانده
- یادآوری
- پایان قسط

---

# Reports Tests

- گزارش حساب
- گزارش عضو
- گزارش تاریخچه
- گزارش تصویری
- گزارش فیلتر شده

---

# Settings Tests

- Theme
- Dynamic Color
- Currency
- Language
- Notification
- Reminder
- PIN
- Biometric

---

# Database Tests

موارد زیر باید تست شوند:

- Insert
- Update
- Delete
- Query
- Migration
- Foreign Keys
- Cascade Delete
- Index

---

# Navigation Tests

- Dashboard
- Wallet
- Transaction
- Reports
- Settings
- Base Info

---

# Performance Tests

بررسی:

- Startup Time
- Scrolling
- Compose Recomposition
- Database Query Time
- Memory Usage

---

# Regression Tests

قبل از هر انتشار:

- Dashboard
- Wallet
- Transaction
- Reports
- Settings
- Navigation
- Database

---

# Device Testing

حداقل روی نسخه‌های زیر تست شود:

| Android | وضعیت |
|----------|--------|
| Android 8 | Required |
| Android 10 | Required |
| Android 12 | Required |
| Android 14 | Required |
| Android 16 | Required |

---

# Screen Size Testing

- Small Phone
- Normal Phone
- Large Phone
- Foldable
- Tablet

---

# RTL Testing

بررسی شود:

- Alignment
- Typography
- Navigation
- Icons
- Layout
- Dialog

---

# Dark Theme Testing

- Colors
- Contrast
- Icons
- Cards
- Charts

---

# Accessibility Testing

- Font Scale
- Screen Reader
- Contrast
- Touch Target
- Keyboard Navigation

---

# Crash Testing

موارد زیر بررسی شوند:

- Rotation
- Background
- Low Memory
- Empty Database
- Large Database
- Invalid Input

---

# Test Coverage Goal

| Layer | Coverage |
|--------|---------:|
| Domain | 95% |
| Data | 85% |
| Presentation | 70% |
| Overall | 85% |

---

# Automation

در آینده موارد زیر به CI اضافه خواهند شد:

- Unit Test
- UI Test
- Lint
- Ktlint
- Detekt
- Build Release

---

# Quality Gates

هیچ نسخه‌ای نباید منتشر شود مگر اینکه:

- تمام Unit Testها موفق باشند.
- Build بدون Error باشد.
- Crash بحرانی وجود نداشته باشد.
- Migrationها تست شده باشند.
- Regression Testها موفق باشند.

---

# Definition of Done (DoD)

هر قابلیت زمانی تکمیل‌شده محسوب می‌شود که:

- پیاده‌سازی کامل شده باشد.
- مستندات به‌روز شده باشند.
- تست‌های مربوطه موفق باشند.
- Code Review انجام شده باشد.
- استانداردهای معماری رعایت شده باشند.
- آماده انتشار باشد.