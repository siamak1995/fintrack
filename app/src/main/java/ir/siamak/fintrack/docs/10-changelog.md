# Changelog

تمام تغییرات مهم پروژه FinTrack در این فایل ثبت می‌شود.

این فایل از استاندارد **Keep a Changelog** پیروی می‌کند و نسخه‌بندی پروژه بر اساس **Semantic Versioning (SemVer)** انجام می‌شود.

---

# [Unreleased]

## Added

- قابلیت‌های در حال توسعه

## Changed

- تغییرات معماری

## Fixed

- رفع باگ‌ها

---

# [1.2.0] - 2026

## Added

### Dashboard

- طراحی کامل داشبورد
- کارت‌های خلاصه مالی
- میانبرهای سریع
- بخش فعالیت‌های اخیر
- نمایش موجودی حساب‌ها
- نمایش درآمد و هزینه امروز
- نمایش درآمد و هزینه ماه

### Wallet

- مدیریت حساب‌ها
- ایجاد حساب
- ویرایش حساب
- حذف حساب

### Transactions

- ثبت درآمد
- ثبت هزینه
- انتقال بین حساب‌ها
- ویرایش تراکنش
- حذف تراکنش

### Members

- مدیریت اعضا
- ایجاد عضو
- ویرایش
- حذف

### Tags

- مدیریت تگ‌ها
- رنگ تگ
- اتصال چند تگ به تراکنش
- محدودسازی نوع تگ

### Reports

- گزارش حساب
- گزارش عضو
- گزارش تاریخچه
- گزارش فیلتر شده
- گزارش تصویری

### Settings

- صفحه تنظیمات
- انتخاب تم
- انتخاب زبان
- انتخاب ارز
- Dynamic Color
- اعلان‌ها
- Biometric
- PIN
- Reminder

### Architecture

- Clean Architecture
- MVVM
- Repository Pattern
- UseCase Pattern
- Hilt
- Room
- DataStore

### Navigation

- Navigation Compose
- Type Safe Navigation

### Database

- Room Migration
- Cross Reference Tags
- Foreign Keys
- Indexes

### UI

- Material Design 3
- RTL
- Persian Calendar
- Dynamic Color
- Dark Mode

---

## Changed

- بهبود ساختار Packageها
- استانداردسازی Componentها
- بازطراحی Dashboard
- بازطراحی Reports
- بازطراحی Navigation
- بهبود UseCaseها
- کاهش وابستگی بین لایه‌ها

---

## Fixed

- رفع مشکلات Navigation
- رفع خطاهای Room
- رفع مشکلات SummaryCard
- رفع خطاهای QuickAction
- اصلاح Dashboard Layout
- اصلاح گزارش‌ها
- اصلاح Theme
- اصلاح Settings

---

# [1.1.0]

## Added

- سیستم Tags
- تقویم شمسی
- گزارش‌های اولیه
- صفحه Dashboard

---

# [1.0.0]

## Initial Release

### Added

- Wallet
- Transactions
- Members
- Dashboard اولیه
- Room Database
- Hilt
- Navigation Compose
- Material Design 3

---

# Upcoming Versions

## 1.2

- Installments
- Budget
- Categories
- Notifications

---

## 1.3

- Financial Goals
- Charts
- Cash Flow
- Statistics

---

## 1.4

- Backup
- Restore
- Excel Export
- PDF Export

---

## 1.5

- Cloud Sync
- Multi Device
- Authentication

---

## 2.0

- AI Assistant
- AI Reports
- Smart Dashboard
- Forecast
- Recommendation Engine
- Premium Features

---

# Versioning Policy

فرمت نسخه‌ها:

```
MAJOR.MINOR.PATCH
```

نمونه:

| Version | توضیح                               |
|---------|-------------------------------------|
| 1.0.0   | اولین انتشار رسمی                   |
| 1.1.0   | قابلیت‌های جدید بدون تغییر ناسازگار |
| 1.1.1   | رفع باگ                             |
| 1.2.0   | افزودن تنظیمات و اصلاحات ظاهری      |
| 2.0.0   | تغییرات بزرگ معماری یا امکانات      |

---

# Release Notes

برای هر نسخه موارد زیر ثبت می‌شود:

- قابلیت‌های جدید
- تغییرات
- رفع باگ‌ها
- بهبود عملکرد
- تغییرات دیتابیس
- تغییرات API
- Migrationهای لازم
- نکات مهم برای ارتقاء نسخه