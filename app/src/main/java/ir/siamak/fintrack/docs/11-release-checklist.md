# Release Checklist

آخرین بروزرسانی: 2026

---

# هدف

این سند مراحل استاندارد انتشار نسخه‌های جدید FinTrack را مشخص می‌کند تا هر نسخه با کیفیت، بدون خطا و قابل انتشار باشد.

---

# قبل از شروع انتشار

- [ ] تمام Featureهای برنامه تکمیل شده‌اند.
- [ ] تمام Pull Requestها Merge شده‌اند.
- [ ] نسخه Develop پایدار است.
- [ ] هیچ TODO بحرانی باقی نمانده است.
- [ ] هیچ Crash شناخته‌شده‌ای وجود ندارد.

---

# Code Quality

- [ ] Build بدون Error
- [ ] Build بدون Warning
- [ ] Build Release موفق
- [ ] بدون Duplicate Code
- [ ] بدون Dead Code
- [ ] بدون Hardcoded String
- [ ] بدون Hardcoded Color
- [ ] بدون Hardcoded Dimension

---

# Architecture

- [ ] Clean Architecture رعایت شده است.
- [ ] MVVM رعایت شده است.
- [ ] Repository Pattern رعایت شده است.
- [ ] UseCase Pattern رعایت شده است.
- [ ] Hilt صحیح کار می‌کند.
- [ ] StateFlow صحیح استفاده شده است.

---

# Database

- [ ] Migration تست شده است.
- [ ] Entityها بررسی شده‌اند.
- [ ] Indexها بررسی شده‌اند.
- [ ] Foreign Keyها صحیح هستند.
- [ ] داده‌های قبلی از بین نمی‌روند.

---

# Dashboard

- [ ] خلاصه مالی صحیح است.
- [ ] درآمد ماه صحیح است.
- [ ] هزینه ماه صحیح است.
- [ ] موجودی حساب‌ها صحیح است.
- [ ] میانبرها صحیح هستند.

---

# Transactions

- [ ] ثبت تراکنش
- [ ] ویرایش
- [ ] حذف
- [ ] انتقال بین حساب‌ها
- [ ] محاسبه موجودی

---

# Wallet

- [ ] ایجاد حساب
- [ ] ویرایش
- [ ] حذف
- [ ] محاسبه موجودی

---

# Members

- [ ] ایجاد
- [ ] ویرایش
- [ ] حذف

---

# Tags

- [ ] ایجاد
- [ ] ویرایش
- [ ] حذف
- [ ] اتصال به تراکنش

---

# Reports

- [ ] گزارش حساب
- [ ] گزارش عضو
- [ ] گزارش تاریخچه
- [ ] گزارش تصویری
- [ ] فیلترها

---

# Settings

- [ ] Theme
- [ ] Dynamic Color
- [ ] Language
- [ ] Currency
- [ ] Notification
- [ ] Reminder
- [ ] PIN
- [ ] Biometric

---

# UI Review

- [ ] RTL
- [ ] Dark Theme
- [ ] Light Theme
- [ ] Dynamic Color
- [ ] Material 3
- [ ] Responsive Layout
- [ ] Tablet Compatibility

---

# Performance

- [ ] Startup Time مناسب
- [ ] بدون Lag
- [ ] بدون Memory Leak
- [ ] بدون ANR
- [ ] مصرف باتری مناسب

---

# Security

- [ ] DataStore بررسی شده است.
- [ ] اطلاعات حساس ذخیره نشده‌اند.
- [ ] Backup بررسی شده است.
- [ ] PIN بررسی شده است.
- [ ] Biometric بررسی شده است.

---

# Documentation

- [ ] README به‌روز شده است.
- [ ] CHANGELOG به‌روز شده است.
- [ ] ROADMAP به‌روز شده است.
- [ ] مستندات قابلیت جدید تکمیل شده‌اند.

---

# Version

- [ ] Version Code افزایش یافته است.
- [ ] Version Name صحیح است.
- [ ] Release Notes آماده است.

---

# Store Assets

- [ ] App Icon
- [ ] Feature Graphic
- [ ] Screenshots
- [ ] Privacy Policy
- [ ] Description
- [ ] Keywords

---

# Final Test

- [ ] نصب روی دستگاه واقعی
- [ ] نصب روی Emulator
- [ ] تست نسخه Release
- [ ] تست ارتقاء از نسخه قبلی
- [ ] تست دیتابیس قدیمی

---

# Release Approval

| بخش | وضعیت |
|------|--------|
| Development | ☐ |
| Testing | ☐ |
| Documentation | ☐ |
| Database | ☐ |
| UI Review | ☐ |
| Release | ☐ |

---

# انتشار

پس از تکمیل تمام موارد فوق، نسخه می‌تواند در یکی از کانال‌های زیر منتشر شود:

- Google Play
- بازار
- مایکت
- نسخه سازمانی
- نسخه آزمایشی (Internal Testing)