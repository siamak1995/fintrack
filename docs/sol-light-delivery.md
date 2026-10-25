# اصلاحات Sol Light — حسابدار شخصی و فروشگاه

تاریخ شروع: ۲۰۲۶/۱۰/۲۴، شاخهٔ `secondMadule`، مبنای `6bcd534`.

تاریخ آخرین اجرای اعتبارسنجی: ۲۰۲۶/۱۰/۲۵، به وقت تهران.

## یافته‌های واقعی پیش از تغییر

- برنامه Android/Kotlin با Compose/Material3، Hilt، Room، Flow و Navigation Compose است. بستهٔ فعال فروشگاه اکنون `store` است؛ تصمیم قدیمی اسناد ۲۷ و ۲۸ دربارهٔ فعال‌بودن `storeaccountant` با سورس فعلی منطبق نیست. کد قدیمی حذف یا جابه‌جا نشده است.
- دیتابیس شخصی نسخهٔ ۸ با فایل `fintrack_db` و فروشگاه نسخهٔ ۲ با فایل `store_fintrack_db` هستند. هیچ Schema یا Migration جدیدی در این اصلاح ایجاد نشده است.
- تنظیم مخرب `fallbackToDestructiveMigrationOnDowngrade()` در Provider شخصی وجود داشت و حذف شد. کاهش نسخه اکنون باید خطا بدهد، نه اینکه جدول‌ها را بازسازی کند.
- شاهد مستقیم حذف برنامه در لاگ UTP قبلی وجود داشت: `uninstall_after_test: true` و `Uninstalling ir.siamak.fintrack`. این، علت قطعی حذف در همان اجرای آزمایشی است؛ بدون لاگ دستگاه کاربر نمی‌توان همهٔ حذف‌های گزارش‌شده را به آن نسبت داد.
- لاگ اصلی پیش از بازنویسی خروجی تست در `app/build/reports/sol-light/previous-utp-uninstall.log` نگهداری شد.
- پشتیبان اولیهٔ نصب موجود در `app/build/safety-backups/2026-10-24/installed-app-before.tar` ذخیره شد. در لحظهٔ بررسی، آن نصب فقط پوشهٔ files داشت و دیتابیس حسابداری در آن وجود نداشت؛ دادهٔ حذف‌شدهٔ قبلی بازیابی یا بازسازی ادعایی نشده است.
- گزارش تصویری شخصی از سه آرایهٔ اعداد ثابت استفاده می‌کرد. آن آرایه‌ها جای خود را به تراکنش‌های واقعی دادند.
- گزارش فروشگاه، خروجی گزارش، پشتیبان‌گیری/بازیابی کاربرمحور و مسیر بازگشت روشن به انتخاب حسابدار وجود نداشت. برخی رویدادهای داشبورد شخصی به ناوبری وصل نبودند.
- تقویم و چندین نمایش/ورودی عددی وابسته به ارقام لاتین یا Locale دستگاه بودند.

## تغییرات اجرایی

### حفظ داده و نصب

- نسخهٔ برنامه به `1.3.0` و `versionCode=2` افزایش یافت؛ applicationId، مسیر و نسخهٔ دیتابیس‌ها ثابت ماندند.
- کلید پایدار AGP یعنی `android.injected.androidTest.leaveApksInstalledAfterRun=true` ثبت شد. `android.experimental.testOptions.uninstallIncompatibleApks=false` نیز صریح است؛ اختلاف امضا نباید به حذف خودکار نصب موجود منجر شود. نام کلیدها از سورس AGP نصب‌شدهٔ ۸٫۷٫۳ بررسی شد.
- Backup محلی هر دو Room DB و دو فایل DataStore موجود را با Snapshot تراکنشی شامل جدول‌های Legacy، BLOB و شمارنده‌های AUTOINCREMENT ذخیره می‌کند. فایل خروجی از طریق SAF در محل انتخابی خارج از فضای برنامه ذخیره می‌شود.
- فایل پشتیبان دارای Manifest نسخه‌دار و SHA-256 هر ورودی است. مسیر ZIP، تکرار ورودی، حجم، نسخه و hash ساختار Room، integrity_check و صحت Preferences پیش از آماده‌سازی بازیابی بررسی می‌شوند.
- بازیابی، ادغام داده نیست و پس از تأیید کاربر انجام می‌شود. ابتدا از وضع جاری پشتیبان داخلی گرفته می‌شود؛ سپس بازیابی آماده می‌شود و UI تا بستن برنامه مسدود می‌ماند. در اجرای سرد بعدی روی IO اجرا می‌شود؛ Shell تا پایان آن، پیش از ساخت ViewModelهای وابسته به Room/DataStore، منتظر می‌ماند تا بازیابی بزرگ باعث مسدودی Main Thread نشود.
- پیش از جایگزینی، فایل‌های اصلی و WAL/SHM احتمالی در محل بازیابی جداگانه نگهداری می‌شوند. Journal بازیابی نیمه‌تمام در شروع بعدی موجب برگشت فایل‌های اصلی می‌شود. خطای بازیابی مسیر عادی ورود را مسدود می‌کند؛ موفقیت کاذب اعلام نمی‌شود.
- قواعد Auto Backup، انتقال دستگاه و درخواست حفظ داده هنگام حذف تنظیم شدند. این‌ها جایگزین پشتیبان قابل بازیابی خارج از فضای برنامه نیستند.
- هیچ Uninstall، Clear Data یا حذف دیتابیس واقعی اجرا نشد. حذف‌های داخل تست صرفاً روی نام‌ها و پوشه‌های آزمایشی مستقل انجام می‌شوند.

### گزارش واقعی فروشگاه

- UseCase مستقل بر snapshot فاکتور، بدون JOIN چندبرابرکننده، محاسبات checked Long و تفکیک فروش/پیش‌سفارش.
- فیلتر تاریخ، روز مشخص، مشتری، کالا، تسویه، جست‌وجوی شماره/مشتری/کالا، مرتب‌سازی، صفحه‌بندی و مقایسهٔ دوره با حفظ همان فیلترها.
- نمایش تعداد، ناخالص کالا، تخفیف فاکتور، مالیات، افزودهٔ اقساط، قابل پرداخت، دریافتی فعلی، مانده، کالا/مشتری، روند شمسی روزانه/هفتگی/ماهانه/سالانه و ورود به جزئیات فاکتور.
- شروع هفته شنبه است. ماه/سال بر اساس تاریخ شمسی گروه‌بندی می‌شود. تاریخ ذخیره‌شدهٔ نامعتبر گزارش می‌شود و بی‌صدا در جمع دوره ناپدید نمی‌شود.
- خروجی PDF راست‌چین تمام نتایج فیلترشده را صادر می‌کند، نه فقط صفحهٔ فعلی. جدول جزئیات، جمع‌ها، اطلاعات دوره و مبنای مالی خروجی مشخص هستند.
- فیلتر کالا «فاکتورهای حاوی کالا» را انتخاب می‌کند؛ جمع فاکتور با مبلغ ناخالص همان کالا مخلوط نشده و سهم ساختگی تخفیف/مالیات به کالا تخصیص نیافته است.
- فاکتور لغوشده از جمع فروش کنار گذاشته می‌شود؛ این به معنی استرداد وجه نیست.
- اختلاف مبلغ سند نقدی یا فاکتور Legacy فاقد اقلام، سود اقساط فرض نمی‌شود. مبلغ قابل پرداخت همان snapshot ذخیره‌شده می‌ماند و تعداد اسناد دارای جزئیات ناقص/اختلاف محاسباتی جداگانه در UI و PDF هشدار داده می‌شود؛ ناخالص اقلام تنها بر ردیف‌های موجود متکی است.

### رابط، ناوبری و فارسی‌سازی

- رنگ‌های روشن مشترک اصلاح شدند؛ Landing دارای دو ورودی متمایز، داشبورد فروشگاه دارای شاخص‌های قابل کلیک و صفحهٔ فروشگاه دارای عرض خوانای محدود در نمایشگر عریض است.
- انتخاب حسابدار و تنظیمات/پشتیبان‌گیری از ریشهٔ هر دو بخش در دسترس است. بازگشت به Landing و رویدادهای گزارش/تراکنش/اقساط داشبورد شخصی متصل شدند.
- دادهٔ ساختگی گزارش تصویری شخصی حذف و با UseCase مستقل بر تراکنش‌های واقعی و دورهٔ قبلی جایگزین شد. انتقال بین حساب‌ها درآمد/هزینه شمرده نمی‌شود. جدول دقیق مقادیر در کنار نمودار و حالت خالی/بارگذاری/خطا نمایش داده می‌شود.
- شمارهٔ نسخهٔ ثابت تنظیمات حذف شد؛ ViewModel آن را از PackageManager نصب واقعی می‌گیرد و تست UI همان نسخهٔ فارسی‌شده را بررسی می‌کند.
- نمایش متن مشترک، ارقام لاتین و عربی را به فارسی تبدیل می‌کند؛ تغییر ۱۲۰ فایل عمدتاً جایگزینی import نمایش متن است، نه بازنویسی صفحات یا منطق مالی.
- تبدیل نمایش ورودی‌ها طول رشته و OffsetMapping را حفظ می‌کند؛ مبلغ و شناسهٔ ذخیره‌شده عوض نمی‌شوند. عنوان، روز و سال تقویم مستقل از Locale فارسی‌اند. شمارهٔ صفحه و متن PDF نیز فارسی‌سازی شدند.

## محدودیت‌های واقعی داده و موارد وابسته به ورودی

- تاریخ هر دریافت جداگانه و پرداخت هر قسط در مدل جاری وجود ندارد. دریافتی گزارش، وضعیت فعلی فاکتورهای صادرشده در بازه است، نه گردش وجه همان روز. شمار فاکتور با برنامهٔ سررسید گذشته و مانده، مبلغ دقیق اقساط معوق نیست.
- گروه کالا، تخفیف ردیف، کسر رند، کارت/شماره حساب تسویه و پرداخت خروجی ساختاریافته در این مدل وجود ندارند. برای نمایش آن‌ها مبلغ/اطلاعات جعلی یا Migration بی‌پشتوانه ایجاد نشده است.
- پشتیبان خروجی رمزگذاری نشده و پیش از ذخیره هشدار صریح دارد؛ فقط در محل امن نگهداری شود. مجوز URI تصاویر خارجی با حذف برنامه لزوماً باقی نمی‌ماند و فایل تصویر خارجی مستقل آرشیو نشده است.
- بازیابی این قالب به همین Schemaهای ۸ و ۲ محدود است؛ نسخهٔ ناشناخته یا ناسازگار رد می‌شود. مسیر تبدیل پشتیبان آینده باید جداگانه طراحی و تست شود.
- نصب عادی ارتقایی با همان امضا از حذف برنامه متفاوت است. حذف کامل فضای خصوصی بدون پشتیبان تضمین‌پذیر نیست. آزمون ارتقا روی شبیه‌ساز و Fixture مستقل انجام شده، نه با جعل یا پاک‌کردن دادهٔ واقعی کاربر.
- PDF «فاکتور شماره ۱.pdf» ارائه نشده است؛ تطبیق ظاهری با مرجع تأیید نمی‌شود. تا دریافت آن، قالب فاکتور موجود بازنویسی نشده؛ فعلاً ارقام آن اصلاح و خروجی آن رندر شده است.
- متن درخواست در بخش ۶.۱، عبارت «نمودارها و برچ…» قطع شده است. الزامات بعد از آن هنوز معلوم نیستند؛ تکمیل کل سند ادعا نمی‌شود.
- خروجی Release بدون امضای انتشار است. آزمون دستگاه فیزیکی و ماتریس کامل ابعاد/دسترس‌پذیری هنوز جای تأیید مستقل دارند؛ پشتیبانی دسکتاپ به معنی ایجاد برنامهٔ دسکتاپ جدید نیست.

## اعتبارسنجی

- ۴۲ تست Unit در اجرای نهایی موفق شدند؛ شکست، خطا و تست نادیده‌گرفته‌شده صفر است. دو تست رگرسیون نهایی، فاکتور نقدی فاقد اقلام و اختلاف مبلغ نقدی را پوشش می‌دهند. XMLها در `app/build/test-results/testDebugUnitTest/` موجودند.
- اجرای نهایی `assembleDebug`، `assembleDebugAndroidTest`، `assembleRelease` و `lintDebug` موفق شد. گزارش Lint نهایی صفر خطا، ۳۵ هشدار و یک مورد اطلاعاتی دارد؛ هشدارها پنهان یا با baseline حذف نشده‌اند. هشدار سازگاری AGP ۸٫۷٫۳ با compileSdk ۳۶ و APIهای deprecated موجود، جزو بدهی فنی باقی‌مانده‌اند.
- ارتقای واقعی نصب موجود از code ۱ / نسخهٔ ۱٫۲٫۰ به code ۲ / نسخهٔ ۱٫۳٫۰ با `adb install -r` و بدون Uninstall انجام شد. Fixture مستقل دادهٔ شخصی، فروشگاه و تنظیمات، پیش و پس از نصب با تست Framework-only بررسی و سالم تأیید شد.
- اجرای نهایی دستگاه پس از تمام اصلاحات تولیدی، ۳۳ تست موفق، صفر شکست/خطا/skip داشت. لاگ جدید UTP فاقد `uninstall_after_test: true` و هرگونه `Uninstalling` است؛ `pm path` باقی‌ماندن برنامه را تأیید کرد. نسخهٔ نمایشی تنظیمات نیز با نسخهٔ PackageManager در تست UI تطبیق داده شد. آزمون UpgradeRetention پس از نصب و پس از راه‌اندازی سرد شبیه‌ساز نیز مستقل موفق شد.
- تست‌ها شامل محاسبات و ترکیب فیلترها، عدم دوباره‌شماری، تفاوت فروش و دریافت، دورهٔ مقایسه، گزارش بدون داده، ارقام و تقویم، Backup/Restore دو دیتابیس و تنظیمات در sandbox، BLOB/Legacy/sequence، checksum، مسیر ZIP ناامن، rollback بازیابی نیمه‌تمام، کاهش نسخه، Migrationهای موجود، PDF و UI/Navigation هستند.
- PDF آزمایشی گزارش و فاکتور روی Android تولید و با runtime مهارت PDF رندر شدند. هر سه صفحهٔ گزارش جدول‌دار نهایی و صفحهٔ فاکتور مشاهده شدند؛ راست‌چین بودن، اتصال فارسی، ارقام، تکرار سرستون و نبود برش در این نمونه‌ها بررسی شد. هیچ تطبیق با مرجع مفقود ادعا نمی‌شود.
- بازبینی تصویری، پنجرهٔ خطای System UI شبیه‌ساز را روی تصاویر نخست آشکار کرد؛ موفقیت تست‌های معنایی Compose به‌تنهایی تأیید بصری تلقی نشد. شبیه‌ساز بدون پاک‌سازی داده راه‌اندازی مجدد شد و برای جلوگیری از هم‌زمانی با ساخت سنگین، اجرای UI به پس از ساخت منتقل شد.
- اجرای نخست پس از Boot سرد، پیش از شروع تست‌ها در ART/DexFileVerifier دچار timeout شد. Trace در `ActivityThread.initInstrumentation` و با صفر کلاس initializeشده بود؛ آن اجرا موفق محسوب نشد. هر دو APK با `adb shell cmd package compile -m speed -f` در همان شبیه‌ساز پیش‌کامپایل شدند؛ آزمون مستقل UpgradeRetention سپس موفق شد. هیچ timeout یا بررسی ایمنی برنامه برای عبور از این مشکل غیرفعال نشد؛ این بررسی، بنچمارک عملکرد Cold Start روی دستگاه واقعی نیست.
- با وجود موفقیت تست‌ها، تصاویر تمام‌صفحهٔ اجرای بعدی نیز پنجره‌های خطای **Pixel Launcher** و **Google Play services** را نشان دادند. بنابراین QA بصری سرتاسری دستگاه هنوز تأیید نشده است. این تصاویر در مسیر `ui/` باقی مانده‌اند؛ ثبت جداگانهٔ پنجرهٔ خود برنامه، برای بررسی کامپوننت‌هاست و جای این تأیید محیطی را نمی‌گیرد. سرویس‌ها، مجوزها یا تنظیمات امنیتی سیستم دست‌کاری نشدند.
- اجرای مجدد تمام ۳۳ تست با AndroidJUnitRunner و گزینهٔ آزمایشی `captureAppWindow=true` نیز موفق شد (۱۳۴٫۸۵۳ ثانیه). چهار تصویر پنجرهٔ خود برنامه در `app/build/reports/sol-light/ui-app-window/` ذخیره و جداگانه مشاهده شدند: گزارش‌ها، داشبورد فروشگاه، لندینگ و هشدار حفاظت از فایل پشتیبان. در همین نماهای بررسی‌شده، متن فارسی، ارقام، فاصله‌گذاری و دکمه‌ها خوانا و بدون هم‌پوشانی بودند؛ صفحهٔ گزارش قابل اسکرول است و تصویر نخست فقط بخش بالای آن را نشان می‌دهد. این روش صرفاً ابزار تست است و تغییری در کد تولید یا تنظیمات سیستم ایجاد نمی‌کند؛ تأیید همهٔ صفحات، ابعاد و محیط سرتاسری از آن نتیجه نمی‌شود.
- بررسی مستقل نهایی `UpgradeRetentionTest` نیز پس از تست‌ها موفق شد و سپس MainActivity برای اجرای عادی باز شد. هیچ فایل Schema تغییر نکرد، فایلی حذف نشد و hash هر دو APK نهایی دوباره با مقادیر زیر تطبیق داده شد.
- Schemaهای Room بدون Diff مانده‌اند. Commit و Push انجام نشده است.

## مسیرهای اصلی تغییر

`common/core/backup/`، `common/core/formatter/PersianText.kt`، `PersianVisualTransformation.kt`، Formatter/کامپوننت تقویم، `di/DatabaseModule.kt`، `FinTrackApplication.kt`، `MainActivity.kt`، `store/domain/model/StoreReport.kt`، `CalculateStoreReportUseCase.kt`، `store/presentation/reports/`، `StoreReportPdfGenerator.kt`، `InvoicePdfGenerator.kt`، Landing، Dashboard/Navigation، Theme/Color، Settings، Manifest/backup rules، `app/build.gradle.kts`، `gradle.properties` و تست‌های متناظر.

## فهرست فایل‌های این اصلاح

تغییر قبلی `docs/store-module-delivery.md` و فایل‌های خطای Kotlin از قبل موجود، در این فهرست به‌عنوان کار این مرحله حساب نشده‌اند.

<details>
<summary>مسیرهای تغییرکرده یا افزوده‌شده (157 فایل)</summary>

```text
app/build.gradle.kts
app/src/androidTest/java/ir/siamak/fintrack/common/BackupArchiveTest.kt
app/src/androidTest/java/ir/siamak/fintrack/common/UpgradeRetentionTest.kt
app/src/androidTest/java/ir/siamak/fintrack/store/StoreNavigationTest.kt
app/src/androidTest/java/ir/siamak/fintrack/store/StoreReportPdfTest.kt
app/src/main/AndroidManifest.xml
app/src/main/java/ir/siamak/fintrack/FinTrackApplication.kt
app/src/main/java/ir/siamak/fintrack/MainActivity.kt
app/src/main/java/ir/siamak/fintrack/common/core/backup/BackupArchive.kt
app/src/main/java/ir/siamak/fintrack/common/core/backup/BackupSection.kt
app/src/main/java/ir/siamak/fintrack/common/core/backup/BackupViewModel.kt
app/src/main/java/ir/siamak/fintrack/common/core/datepicker/calendar/PersianCalendarFormatter.kt
app/src/main/java/ir/siamak/fintrack/common/core/datepicker/components/PersianCalendarHeader.kt
app/src/main/java/ir/siamak/fintrack/common/core/datepicker/components/PersianDatePickerDialog.kt
app/src/main/java/ir/siamak/fintrack/common/core/datepicker/components/PersianDateRangePickerDialog.kt
app/src/main/java/ir/siamak/fintrack/common/core/datepicker/components/PersianDayCell.kt
app/src/main/java/ir/siamak/fintrack/common/core/datepicker/components/PersianWeekHeader.kt
app/src/main/java/ir/siamak/fintrack/common/core/datepicker/components/PersianYearPicker.kt
app/src/main/java/ir/siamak/fintrack/common/core/datepicker/preview/PersianDatePickerPreview.kt
app/src/main/java/ir/siamak/fintrack/common/core/formatter/PersianNumberFormatter.kt
app/src/main/java/ir/siamak/fintrack/common/core/formatter/PersianText.kt
app/src/main/java/ir/siamak/fintrack/common/core/formatter/PersianVisualTransformation.kt
app/src/main/java/ir/siamak/fintrack/di/DatabaseModule.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/domain/dashboard/items/FinanceInfoItem.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/domain/dashboard/items/InstallmentItem.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/domain/dashboard/items/MemberItem.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/domain/dashboard/items/QuickActionCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/domain/dashboard/items/TransactionItem.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/domain/dashboard/items/WalletItem.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/domain/report/usecases/CalculateVisualReportUseCase.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/BaseInfoScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/installment/add_edit_installment/AddEditInstallmentsScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/installment/list/InstallmentScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/member/add_edit_member/AddEditMemberScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/member/card/MemberSwipeCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/member/list/MemberScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/SettingsScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/SettingsState.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/SettingsViewModel.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/components/ColorPickerDialog.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/components/SettingArrowItem.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/components/SettingsGroup.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/components/SettingsItem.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/components/SwitchSettingItem.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/dialogs/CurrencyDialog.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/dialogs/LanguageDialog.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/dialogs/ThemeDialog.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/navigation/SettingsRoute.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/sections/AboutSection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/sections/AppearanceSection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/sections/GeneralSection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/sections/NotificationSection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/sections/SectionComponents.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/settings/sections/SecuritySection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/tags/add_edit_tag/AddEditTagScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/tags/card/TagSwipeCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/wallet/add_edit_wallet/AddEditWalletScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/wallet/card/WalletCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/wallet/list/WalletScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/components/EmptyState.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/components/FTBottomBar.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/components/FTButton.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/components/FTTextField.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/components/FTTopBar.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/components/MoneyText.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/components/StatCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/components/SummaryCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/DashboardComponentUtils.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/DashboardScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/DashboardUiAction.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/DashboardEmpty.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/DashboardError.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/DashboardLoading.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/MemberAvatar.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/SectionHeader.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/cards/DashboardStatCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/charts/ChartLegend.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/charts/DashboardDonutChart.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/fab/DashboardSpeedDial.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/sections/ChartSection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/sections/DashboardHeaderCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/sections/FinancialSummarySection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/sections/GreetingSection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/sections/InsightSection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/sections/MemberSection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/sections/RecentTransactionsSection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/components/sections/UpcomingInstallmentsSection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/dashboard/navigation/DashboardRoute.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/landing/LandingScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/landing/components/BalanceCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/landing/components/GreetingCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/landing/components/QuickFeatureCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/landing/components/StartButton.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/landing/components/StatisticsCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/navigation/AppNavGraph.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/report/components/CategoryReportCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/report/components/MonthlyReportCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/report/components/QuickActionsSection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/report/components/ReportsSummarySection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/report/components/TransactionReportCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/report/components/WalletReportCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/report/components/sections/ReportFilterSection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/report/pages/filtered/FilteredReportScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/report/pages/member/MemberReportScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/report/pages/transactionsHistory/HistoryReportScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/report/pages/visual/VisualReportScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/report/pages/visual/VisualReportViewModel.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/report/pages/wallet/WalletReportScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/security/lock/LockScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/security/lock/PinPad.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/security/setup/PinSetupDialog.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/theme/Color.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/transaction/TransactionListScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/transaction/add_edit_transaction/AddEditTransactionScreen.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/transaction/navigation/TransactionListRoute.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/transaction/section/FilterSection.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/transaction/section/TransactionDateGlassHeader.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/transaction/section/TransactionItemCard.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/transaction/section/TransactionListEmptyState.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/transaction/section/TransactionMonthGlassHeader.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/transaction/section/TransactionSwipeBackground.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/transaction/section/TransactionTypeFilterRow.kt
app/src/main/java/ir/siamak/fintrack/store/core/pdf/InvoicePdfGenerator.kt
app/src/main/java/ir/siamak/fintrack/store/core/pdf/StoreReportPdfGenerator.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/StoreReport.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/store/CalculateStoreReportUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/baseinfo/BaseInfoHubScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/components/StoreContent.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/components/StoreUi.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/dashboard/StoreDashboardScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/invoices/InvoiceScreens.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/invoices/SaleInvoiceDetailScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/navigation/StoreBottomBar.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/navigation/StoreNavGraph.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/navigation/StoreScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/PreOrderListScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/create/CreatePreOrderScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/invoice/PreOrderInvoiceScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/materials/PreOrderMaterialScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/payment/PreOrderPaymentScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/pricing/PreOrderPricingScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/reports/StoreReportsScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/reports/StoreReportsViewModel.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/create/CreateSaleScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/customer/SaleCustomerScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/discount/SaleDiscountScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/invoice/SaleInvoiceScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/list/SalesScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/payment/SalePaymentScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/products/SaleProductsScreen.kt
app/src/main/res/xml/backup_rules.xml
app/src/main/res/xml/data_extraction_rules.xml
app/src/test/java/ir/siamak/fintrack/common/core/formatter/PersianDisplayTest.kt
app/src/test/java/ir/siamak/fintrack/personalaccountant/VisualReportTest.kt
app/src/test/java/ir/siamak/fintrack/store/domain/StoreReportsTest.kt
docs/sol-light-delivery.md
gradle.properties
```

</details>

## اجرای مجدد اعتبارسنجی

خروجی‌های نسخهٔ `1.3.0` / code ۲:

- Debug قابل نصب با کلید توسعه: `app/build/outputs/apk/debug/app-debug.apk`
- Release بدون امضای انتشار: `app/build/outputs/apk/release/app-release-unsigned.apk`
- SHA-256 Debug: `C64BA0D8371849C317EE40A137F16DE2F795480EF259B16E0AF9E66B660FFDD9`
- SHA-256 Release: `924DDA781E011EEB116CA0798AA0184C2A790F5D56BC8BDB6F463190488E4FDC`
- گزارش Lint: `app/build/reports/lint-results-debug.html`
- تصاویر QA: `app/build/reports/sol-light/ui/`
- تصاویر مستقل پنجرهٔ برنامه: `app/build/reports/sol-light/ui-app-window/`
- PDFهای آزمایشی و رندرهای مشاهده‌شده: `app/build/reports/sol-light/pdf/`

از ریشهٔ پروژه، با PowerShell:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest :app:assembleRelease :app:lintDebug --offline --no-configuration-cache --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process'
.\gradlew.bat :app:connectedDebugAndroidTest --offline --no-configuration-cache --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process'
```

تست دستگاه فقط روی شبیه‌ساز/دستگاه آزمایشی اجرا شود. پیش از اجرای روی دستگاه دیگر، پشتیبان معتبر بگیرید. برای ارتقای نصب دارای داده از نصب جایگزین با همان امضا استفاده کنید؛ Uninstall یا Clear Data بخشی از این گردش کار نیست. خروجی Debug با کلید توسعه است و برای جایگزینی نسخه‌ای با امضای متفاوت مناسب نیست؛ در آن حالت نباید برای عبور از خطا برنامهٔ قبلی را حذف کرد.
