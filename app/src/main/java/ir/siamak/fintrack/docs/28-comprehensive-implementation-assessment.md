# گزارش تحلیل اجرایی FinTrack

## دامنه و روش بررسی

این گزارش پیش از شروع تغییرات اجرایی جدید تهیه شده است. مستندات `01` تا `13` و `MasterPrompt.txt` در مسیر `docs`، ساختار واقعی packageها، Navigation، Hilt، Room، DAOها، Repositoryها، ViewModelها و تست‌های موجود بررسی شده‌اند.

شواهد مستندات:

- معماری لایه‌ای Presentation → Domain → Data → Room در [02-architecture.md:21](02-architecture.md:21) تا خط 34 تعریف شده است.
- قرارداد Repository، UseCase و Hilt در [02-architecture.md:121](02-architecture.md:121) تا خط 191 آمده است.
- Entityها و روابط شخصی در [05-database.md:75](05-database.md:75) تا خط 299 تعریف شده‌اند.
- MVP، اقساط، بودجه، دسته‌بندی، اهداف، Backup و Cloud Sync به‌ترتیب در [06-features.md:15](06-features.md:15) تا خط 217 برنامه‌ریزی شده‌اند.
- هرم و سطوح تست در [13-testing.md:13](13-testing.md:13) تا خط 80 آمده است.

## وضعیت واقعی مبنا

| حوزه | وضعیت واقعی | شواهد |
| --- | --- | --- |
| ماژول فعال غرفه | `storeaccountant` | `AppNavGraph.kt:46-47,369`، `StoreNavGraph.kt:27` |
| package موازی | `store` مستقل و به Navigation/Hilt فعال متصل نیست | `store/presentation/navigation/StoreNavGraph.kt:16` در برابر importهای `storeaccountant` در `AppNavGraph.kt` |
| دیتابیس شخصی | `AppDatabase` نسخه ۸؛ Schemaهای ۶، ۷ و ۸ در Git | `AppDatabase.kt:20-42` و `app/schemas/.../AppDatabase` |
| دیتابیس غرفه | `StoreDatabase` نسخه ۱؛ Migration ندارد | `storeaccountant/data/local/StoreDatabase.kt:19-38` |
| Session/Context | مدل، DataStore Repository، UseCase و ViewModel پایه وجود دارد | `account/domain/model/AccountantContext.kt`، `DataStoreContextRepository.kt`، `AppSessionViewModel.kt` |
| Scope دادهٔ شخصی | ستون و Index `contextId` وجود دارد، ولی Queryها Scoped نیستند | `WalletDao.kt`، `TransactionDao.kt`، `MemberDao.kt`، `TagDao.kt`، `InstallmentDao.kt` |
| مدیریت خطا | `AppError` ایجاد شده، اما هنوز Error boundary و قرارداد واحد UiState سراسری ندارد | `common/core/error/AppError.kt` |

## قابلیت‌های موجود و حفظ‌شدنی

- Navigation شخصی برای Landing، Dashboard، Wallet، Transaction، Member، Tag، Installment، گزارش‌ها و Settings تعریف شده است: `AppNavGraph.kt:123-369`.
- ساختار شخصی برای Wallet، Transaction، Member، Tag و Installment وجود دارد و با Entity/DAO/Repository/ViewModel پیاده‌سازی شده است.
- Navigation فعال غرفه مسیرهای Dashboard، اطلاعات پایه، فروش، مواد اولیه، پیش‌سفارش و فاکتورها را دارد: `StoreNavGraph.kt:27-101`.
- موجودیت‌های فعال غرفه شامل Store، RawMaterial، Seller، Product، Customer، Sale و SaleItem هستند: `StoreDatabase.kt:20-28`.
- Formatterهای فارسی، DataStore انتخاب Context، Qualifierهای Hilt و Migrationهای شخصی `6→7` و `7→8` افزوده شده‌اند.

«موجود» به معنی عبور کامل از معیار تجاری نیست؛ برای هیچ قابلیت، بدون تست UI/Integration، Error handling و Navigation معتبر، وضعیت «تکمیل‌شده» اعلام نمی‌شود.

## قابلیت‌های ناقص یا برنامه‌ریزی‌شده

طبق roadmap و features، اقساط پیشرفته، بدهی/وام، تراکنش دوره‌ای، یادآور، بودجه، دسته‌بندی، اهداف، Backup/Restore، Cloud Sync، گزارش پیشرفته و قابلیت Premium هنوز نیازمند بررسی و توسعه‌اند. مستندات آن‌ها را هدف‌گذاری کرده‌اند، نه آن‌که پیاده‌سازی واقعی‌شان را اثبات کنند: `01-project-overview.md:137-185` و `06-features.md:99-217`.

کمبودهای فنی فوری:

1. DAOهای شخصی مانند `getAllWallets()`، `getAllTransactions()`، `getAllMembers()`، `getAllTags()` و `getAllInstallments()` هنوز پارامتر `contextId` ندارند؛ بنابراین v8 به‌تنهایی جلوی نشت داده را نمی‌گیرد.
2. مدل‌های Domain و Mapperهای شخصی هنوز قرارداد Context را به‌صورت کامل حمل نمی‌کنند.
3. `AppSessionViewModel` هنوز به Application Shell و Navigation Guard وصل نشده است؛ در نتیجه تغییر Context محتوا یا back stack را ایزوله نمی‌کند.
4. تنظیمات فعلی (`SettingsRepositoryImpl`) global هستند و سیاست App/User/Context برای آن‌ها وجود ندارد.
5. قرارداد یکنواخت `Loading/Content/Empty/Saving/Saved/Error` و Error boundary سراسری وجود ندارد.
6. تست Context isolation، Navigation switching، UI و release اجرا نشده‌اند.

## تصمیم معماری دربارهٔ `storeaccountant` و `store`

`storeaccountant` مرجع اجرایی و تنها ماژول قابل توسعهٔ غرفه باقی می‌ماند، زیرا Navigation برنامه صراحتاً آن را import و ثبت می‌کند. `store` یک پیاده‌سازی موازی است؛ فعلاً نباید توسعه، حذف، انتقال یا اتصال جدیدی روی آن انجام شود.

تصمیم پیشنهادی ثبت‌شده:

1. تا پایان Context isolation شخصی، `store` بدون تغییر باقی بماند.
2. سپس با inventory دقیق Entity/DAO/Route، هم‌پوشانی و دادهٔ احتمالی آن ارزیابی شود.
3. فقط با Migration قابل بازگشت، یکی از مسیرهای Adapter، ادغام مرحله‌ای یا خروج رسمی از چرخه انتخاب شود.

## قرارداد نهایی Context در وضعیت فعلی

| مورد | قرارداد |
| --- | --- |
| رجیستری پایدار | جدول `accountant_contexts` در `AppDatabase` |
| انتخاب فعال | DataStore؛ فقط شناسهٔ Context فعال را نگه می‌دارد |
| شناسه‌های پایه | Personal=`1`، Store=`2` |
| Scope فعلی Personal | Wallet، Transaction، Member، Tag، Installment؛ `TransactionTagCrossRef` از والدها Scope می‌گیرد |
| Nullability | `contextId: Long` غیرnullable با default Personal برای دادهٔ Legacy |
| Store Database | مستقل و خارج از Migration `7→8`؛ سیاست multi-context آن هنوز نهایی نشده است |

Migration `7→8` جدول Context، ستون `contextId` و Indexها را بدون حذف داده افزوده و دادهٔ Legacy را به Personal تخصیص داده است. Foreign key به جدول Context عمداً انجام نشده، زیرا Room/SQLite برای افزودن آن نیازمند بازسازی جدول‌هاست و رابطهٔ سازگاری Context میان Transaction، Wallet و Member نیز باید همراه با DAO و Repository طراحی شود.

## Migrationهای ثبت‌شده و برنامهٔ بعدی

| دیتابیس | نسخه/مسیر | وضعیت |
| --- | --- | --- |
| Personal | `1→2` تا `6→7` | موجود |
| Personal | `7→8` | Context registry، backfill، ستون و Index؛ تست‌شده |
| Store active | `1→2` | هنوز طراحی نشده؛ هر تغییر Schema غرفه باید از این مسیر آغاز شود |

ریسک مهم: `DatabaseModule.kt:333` هنوز `fallbackToDestructiveMigrationOnDowngrade()` دارد. گرچه fallback مخرب برای upgrade وجود ندارد، این سیاست با منع صریح fallback مخرب در مستندات جدید سازگار نیست و باید پیش از release با سیاست downgrade غیرمخرب/عدم پشتیبانی از downgrade جایگزین شود.

## فایل‌های پیشنهادی برای تغییر مرحلهٔ بعد

| گروه | فایل‌های اصلی | دلیل |
| --- | --- | --- |
| DAO شخصی | `WalletDao.kt`, `TransactionDao.kt`, `MemberDao.kt`, `TagDao.kt`, `InstallmentDao.kt` | همهٔ خواندن/ویرایش/حذف‌های وابسته به داده باید `contextId` را الزام کنند |
| Domain/Data شخصی | Repository interface/impl، Model و Mapperهای پنج دامنه | انتقال صریح Context تا Room و جلوگیری از insert/update در Context اشتباه |
| Session | `AppSessionViewModel.kt`, `AppSessionState.kt`, Context use caseها | Validation دسترسی، fallback امن و پیام Error |
| Shell | `AppNavGraph.kt` و لایهٔ root جدید | جلوگیری از نمایش state قبلی، پاک‌سازی back stack و Route guard |
| Test | Room integration tests، Context isolation tests و Compose navigation tests | اثبات عدم نشت و Regression |
| Store | فقط مستند تصمیم و inventory؛ بدون تغییر کد در این مرحله | جلوگیری از توسعهٔ موازی |

## ترتیب پیشنهادی اجرا

1. تکمیل قرارداد API شخصی: تمام DAO و Repositoryهای وابسته به Context با `contextId` صریح.
2. افزودن Context به Domain model/Mapper و اعتبارسنجی Context هنگام insert/update/delete.
3. نوشتن تست‌های Room برای ایزوله‌بودن Personal contextهای ۱ و یک Context شخصی دوم.
4. اتصال `AppSessionState` به root و Route guard، سپس تست switch، restart، inactive و invalid Context.
5. تعیین سیاست Settings: App-level، User-level یا Context-level؛ مهاجرت فقط پس از طبقه‌بندی.
6. طراحی جداگانهٔ StoreDatabase `1→2` پس از تعیین اینکه یک دیتابیس میزبان چند Store Context است یا نه.
7. پس از تثبیت داده و Session، توسعهٔ قابلیت‌های تجاری بر پایهٔ اولویت roadmap؛ هر قابلیت با Entity/DAO/Repository/UseCase/UI/Test کامل.
8. Release hardening: حذف fallback مخرب، Backup/Restore، تست release، RTL، accessibility و checklist انتشار.

## ریسک‌ها و معیار عبور

- بزرگ‌ترین ریسک فعلی، فعال‌شدن Context Switching در UI پیش از Scoped شدن Queryهاست؛ این کار نشت داده ایجاد می‌کند.
- تغییر هم‌زمان Store، Navigation و Schema شخصی ریسک Regression بالا دارد و ممنوع است.
- اسناد و کد در برخی ادعاهای «Current» همگام نیستند؛ تنها تست‌های قابل اجرا باید معیار اعلام تکمیل باشند.
- معیار عبور مرحلهٔ بعد: تمام Queryهای شخصی Scope داشته باشند، Context isolation برای دادهٔ موجود و دادهٔ جدید پاس شود، switch Context محتوای قبلی را نمایش ندهد، و Build/Unit/Integration/UI مرتبط موفق باشد.

## نتیجه

پروژه برای شروع مرحلهٔ Context-scoped repository آماده است، اما برای اعلام آمادگی تجاری، Context switching کاربرمحور یا توسعهٔ StoreDatabase هنوز آماده نیست. این گزارش هیچ تغییر اجرایی در DAO، Repository، Navigation یا StoreDatabase ایجاد نکرده است.
