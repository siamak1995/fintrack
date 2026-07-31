# Deployment Guide

آخرین بروزرسانی: 2026

---

# هدف

این سند فرآیند Build، امضای برنامه و انتشار نسخه‌های مختلف FinTrack را توضیح می‌دهد.

---

# Supported Platforms

- Android
- Google Play
- Bazaar
- Myket
- Enterprise Distribution

---

# Build Variants

## Debug

ویژگی‌ها:

- مناسب توسعه
- Log فعال
- Debuggable
- تست روزانه

---

## Release

ویژگی‌ها:

- امضاء شده
- بهینه‌سازی شده
- مناسب انتشار
- بدون Logهای توسعه

---

# Versioning

از Semantic Versioning استفاده می‌شود.

```
MAJOR.MINOR.PATCH
```

نمونه

```
1.0.0
1.1.0
1.1.1
2.0.0
```

---

# Version Code

هر نسخه باید Version Code بزرگ‌تر از نسخه قبلی داشته باشد.

نمونه

| Version | VersionCode |
|----------|------------:|
| 1.0.0 | 1 |
| 1.1.0 | 2 |
| 1.1.1 | 3 |
| 1.2.0 | 4 |

---

# Build Release

ساخت Release

```
Build → Generate Signed Bundle / APK
```

---

# Output

APK

```
app-release.apk
```

AAB

```
app-release.aab
```

---

# Signing

برای انتشار رسمی باید از Keystore اختصاصی پروژه استفاده شود.

اطلاعات Keystore باید خارج از Repository نگهداری شود.

---

# Secrets

موارد زیر نباید داخل Git قرار بگیرند.

- Keystore
- Password
- API Keys
- Signing Config
- Secret Tokens

---

# Git Ignore

نمونه فایل‌هایی که نباید Commit شوند.

```
*.jks

keystore.properties

local.properties

.idea

build

captures
```

---

# Release Process

1. تکمیل توسعه
2. اجرای تست‌ها
3. بروزرسانی CHANGELOG
4. افزایش Version
5. Build Release
6. تست نسخه Release
7. انتشار

---

# Google Play

چک‌لیست

- App Bundle
- Feature Graphic
- Screenshots
- App Icon
- Privacy Policy
- Data Safety
- Release Notes

---

# Bazaar

چک‌لیست

- APK یا AAB
- آیکون
- اسکرین‌شات
- توضیحات فارسی
- دسته‌بندی
- نسخه

---

# Myket

چک‌لیست

- APK یا AAB
- تصاویر
- توضیحات
- Release Notes

---

# Internal Testing

قبل از انتشار عمومی:

- نصب روی دستگاه واقعی
- تست روی Android 8
- تست روی Android 10
- تست روی Android 12
- تست روی Android 14+
- بررسی Migration
- بررسی عملکرد

---

# Release Notes

برای هر نسخه باید شامل موارد زیر باشد.

- قابلیت‌های جدید
- بهبودها
- رفع باگ‌ها
- تغییرات مهم

---

# Rollback Plan

در صورت مشاهده خطای بحرانی:

- توقف انتشار
- بازگشت به نسخه پایدار قبلی
- بررسی Logها
- رفع مشکل
- انتشار Patch جدید

---

# Backup Before Release

قبل از هر انتشار:

- Backup Database
- Backup Source Code
- Git Tag
- نسخه Release Archive

---

# Git Tag

نمونه

```
v1.0.0

v1.1.0

v1.1.1
```

---

# Deployment Checklist

- Build موفق
- Release موفق
- Version صحیح
- Migration صحیح
- Documentation به‌روز
- Changelog به‌روز
- تست نهایی انجام شده
- نسخه آرشیو شده
- Git Tag ثبت شده

---

# Long-Term Deployment Strategy

مرحله اول

- انتشار در بازار

مرحله دوم

- انتشار در مایکت

مرحله سوم

- انتشار در Google Play

مرحله چهارم

- نسخه Premium

مرحله پنجم

- نسخه Cloud

مرحله ششم

- نسخه SaaS

---

# Deployment Goal

هدف نهایی انتشار، ارائه یک محصول پایدار، قابل اعتماد و حرفه‌ای برای میلیون‌ها کاربر فارسی‌زبان است.