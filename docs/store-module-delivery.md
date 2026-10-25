# گزارش تحویل ماژول فروشگاه FinTrack

شروع بررسی: ۲۰۲۶/۱۰/۲۳ — تکمیل اعتبارسنجی: ۲۰۲۶/۱۰/۲۴ (Asia/Tehran) — شاخه: `secondMadule` — مبنای کار: `ca3e6bc`

در بررسی پایانی، HEAD شاخه روی `6bcd534` دیده شد و تغییرات پیاده‌سازی در آن ثبت شده‌اند؛ عامل حاضر هیچ دستور Commit یا Push اجرا نکرده است. پس از نصب APK نهایی، اجرای دستی مجدد با timeout راه‌اندازی و ناپایداری System UI شبیه‌ساز روبه‌رو شد؛ بنابراین اجرای دستی اخیر موفق اعلام نمی‌شود. نتایج موفق ۲۲ تست دستگاه و ۲۷ تست Unit مربوط به اعتبارسنجی تکمیل‌شدهٔ همین سورس هستند.

## دامنهٔ پیاده‌سازی

- مدیریت مشخصات فروشگاه، برند، لوگو، فروشندگان، کالا و تصویر کالا، مواد اولیه، خرید و هزینه ارسال.
- داشبورد فروش امروز، تعداد فاکتور، موجودی و سفارش‌های باز؛ Bottom Navigation مستقل و ورود از Landing.
- فروش پنج‌مرحله‌ای: مشتری، کارت کالا و شمارنده محدود به موجودی، تخفیف ثابت/درصدی و مالیات، نقدی/اقساطی، بازبینی و ثبت فاکتور.
- پیش‌سفارش: مشتری، شرح و تاریخ تحویل، مواد مصرفی، هزینه مواد/تولید/سود/ارسال و قیمت توافقی، پیش‌پرداخت درصدی/مبلغی، فاکتور.
- گردش وضعیت NEW → IN_PROGRESS → READY → DELIVERED؛ لغو پیش از تحویل و بازگردانی مواد فقط یک بار. تحویل به تسویه وابسته نشده است.
- ثبت دریافت بعدی، جلوگیری از دریافت بیش از مانده، فیلتر فاکتورها، روزهای باقی‌مانده تا تحویل و جزئیات پرداخت/اقساط.
- PDF فارسی راست‌به‌چپ چندصفحه‌ای، لوگو، تاریخ شمسی، پیش‌نمایش داخلی، ذخیره با انتخاب کاربر و اشتراک امن FileProvider.

## معماری و ایمنی داده

کد جدید فروشگاه در `ir.siamak.fintrack.store` قرار دارد. Compose/Material3، Hilt، Room، Repository/UseCase، StateFlow و Stateهای immutable استفاده شده‌اند. محاسبات پولی داخل UseCase و با کنترل overflow انجام می‌شود. واحد پول تومان است. مثال ۱۰ میلیون با سه ماه پنج درصد، جمعاً ۱۱٫۵ میلیون است؛ اختلاف گردکردن در قسط آخر جبران می‌شود.

مسیرهای اصلی ViewModel مستقل دارند؛ مراحل یک Wizard و فرم‌های Dialog، کامپوننت stateless با State Hoisting و State مشترک همان جریان هستند. تاریخ‌ها به ISO ذخیره و به شمسی نمایش داده می‌شوند. سررسید اقساط از تاریخ انتخابی با `LocalDate.plusMonths` (ماه‌های تقویم میلادی) ساخته می‌شود.

ثبت فروش/سفارش شامل مشتری، اقلام، موجودی، پرداخت و تصویر ذخیره‌شدهٔ فاکتور در یک تراکنش Room است. تغییر هم‌زمان موجودی/قیمت باعث rollback می‌شود. شناسهٔ درخواست پایدار و شمارهٔ یکتای فاکتور مانع ثبت دوباره می‌شود. فرم‌ها با SavedStateHandle بازیابی می‌شوند و درخواست قبلاً ثبت‌شده را تشخیص می‌دهند.

متن، قیمت و اقلام فاکتور snapshot هستند و ویرایش یا حذف کاتالوگ آن‌ها را تغییر نمی‌دهد. تصاویر به URI با مجوز پایدار ارجاع می‌دهند؛ فایل تصویر خارجی به بایت مستقل آرشیو نشده است. حذف مادهٔ اولیهٔ سفارش فعال ممنوع است.

### Migration فروشگاه

`StoreDatabase` نسخهٔ ۲ با نام موجود `store_fintrack_db` فعال است. Migration غیرمخرب `1→2` هم ساختار قدیمی فعال `storeaccountant` و هم ساختار اولیهٔ `store` را پشتیبانی می‌کند. فروش‌ها، اقلام، مشتری، کاتالوگ و سابقه پرداخت حفظ می‌شوند؛ جدول‌های فروش قدیمی با نام `legacy_*_v1` برای حفظ ارجاع walletId آرشیو می‌مانند. شماره‌های تکراری/متعارض فاکتور رفع تعارض می‌شوند. اقلام و دریافتی پیش‌سفارش‌های قبلی و وضعیت لغوشده به snapshot منتقل می‌شوند.

فروش قدیمی فاقد سابقهٔ پرداخت، خودکار «تسویه‌شده» فرض نشده است. این موارد توضیح انتقال و ماندهٔ نیازمند تطبیق دارند. Schema نسخهٔ ۲ صادر شده است. از `fallbackToDestructiveMigration` استفاده نشده است.

`AppDatabase` شخصی نسخهٔ ۸ تغییری نکرده است؛ Migration شخصی `8→9` و قابلیت‌های Budget/Category/InstallmentPayment اضافه نشده‌اند. فایل‌های `storeaccountant` حذف یا تغییر نکرده‌اند. تنها اتصال Navigation اصلی به پیاده‌سازی مستقل `store` عوض شده است.

در بررسی جامع Lint، یک خطای ازقبل‌موجود در `InstallmentRoute` شخصی پیدا شد: خواندن مستقیم `StateFlow.value` در Composition. فقط همین اتصال UI به `collectAsStateWithLifecycle` اصلاح شد؛ هیچ قرارداد Domain یا دادهٔ شخصی تغییر نکرد.

## آزمون و خروجی

- Unit: ۲۷ تست، صفر شکست/خطا (محاسبات، اعتبارسنجی، snapshot و regressionهای موجود).
- دستگاه: ۲۲ تست روی Pixel_8 / Android 15 / API 35، صفر شکست/خطا؛ شامل چهار تست Migration فروشگاه، تراکنش اتمیک، rollback، رقابت موجودی، پیش‌پرداخت، لغو، snapshot، بازیابی فرم، PDF و UI/Navigation و regressionهای موجود.
- هر دو نتیجهٔ Unit و دستگاه با XML بررسی شدند. یک اجرای هم‌زمان Build/Lint/Device پیش از شروع تست به startup ANR شبیه‌ساز خورد؛ بعد از بستن daemon سنگین، اجرای مستقل instrumentation و سپس Gradle تک‌worker هر دو ۲۲ تست را کامل و بدون شکست اجرا کردند. فرمان Gradle دستگاه با `BUILD SUCCESSFUL` تمام شد.
- گزارش نهایی Lint هیچ issue در مسیر کد `store` ندارد. برای شبیه‌ساز، اجرای تست دستگاه و Lint به‌صورت جداگانه توصیه می‌شود؛ در اجرای طولانی هم‌زمان، System UI شبیه‌ساز نیز ANR نشان داد.
- Debug APK و AndroidTest APK با آخرین اصلاح Migration ساخته شدند؛ Release unsigned نهایی نیز ساخته شد. اجرای `lintVitalRelease` و سپس `assembleRelease` بدون استثناکردن هیچ task موفق شد.
- موتور همراه AGP 8.7.3 ابتدا به خطای داخلی `IncompatibleClassChangeError` در `NonNullableMutableLiveDataDetector` کتابخانه Lifecycle رسید. موتور مستقل Lint در `gradle.properties` روی 8.8.2 تنظیم شد؛ هیچ قانون خاموش و هیچ کتابخانه runtime یا AGP ارتقا داده نشد. خطای قدیمی InstallmentRoute و پیشنهاد State عددی فیلتر فروشگاه اصلاح شدند. اجرای نهایی Unit/Debug/Release/Lint با `BUILD SUCCESSFUL` تمام شد؛ نتیجهٔ Lint جامع: صفر خطا، ۶۲ هشدار و یک مورد Information. هشدارهای باقی‌مانده در HTML/XML کامل گزارش شده‌اند و suppress یا baseline نشده‌اند.
- PDF چندصفحه‌ای روی Android PdfRenderer رندر و تصویر فارسی از نظر خوانایی و بریدگی بررسی شد.
- ورود واقعی Landing → Store → Sales → Wizard با تست UI بررسی شد. برنامه روی Pixel_8 / API 35 نصب و اجرا شد.

خروجی‌ها در این مسیرها هستند:

```text
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release-unsigned.apk
app/build/test-results/testDebugUnitTest/
app/build/outputs/androidTest-results/connected/
app/build/reports/lint-results-debug.html
app/build/reports/lint-results-debug.xml
app/build/reports/store/invoice-preview.png
app/build/reports/store/dashboard.png
```

فرمان‌های اعتبارسنجی اجراشده:

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest --offline --no-configuration-cache --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process'
# BUILD SUCCESSFUL؛ ۲۲ تست، صفر شکست/خطا؛ XML بررسی شد.
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:assembleRelease :app:lintDebug --offline --no-configuration-cache --max-workers=1 --console=plain '-Pkotlin.compiler.execution.strategy=in-process'
# BUILD SUCCESSFUL؛ Unit و هر دو APK و Lint؛ بدون حذف task یا خاموش‌کردن قانون.
```

SHA-256 خروجی نهایی:

```text
Debug (فایل موجود در بررسی پایانی): E1F986A29D53825B72123D0E05C241F57B01C2672DAC7753584D76BFFE39957B
Release unsigned: 86F23E62838AE6BA87F51E3F247BE4BD8D263F75FF1B9AAFEEE7DD389F632B58
```

## نکات تحویل

- Release بدون امضای انتشار است؛ انتشار واقعی به کلید و تصمیم مالک پروژه نیاز دارد.
- هشدار قبلی AGP 8.7.3 درباره compileSdk 36 و هشدارهای deprecation پروژه پنهان نشده‌اند؛ ارتقای گستردهٔ ابزارها در این تغییر انجام نشده است.
- لغو سفارش، دریافت قبلی را پاک نمی‌کند و به‌معنی استرداد واقعی وجه نیست؛ UI و PDF این موضوع را اعلام می‌کنند.
- مقدار مواد بر اساس واحد ثبت‌شده است؛ مثلاً برای ماده با واحد کیلو، ۲۰۰ گرم باید ۰٫۲ وارد شود. تبدیل خودکار واحد وجود ندارد.
- برنامه ثبت حسابداری محلی است؛ پرداخت بانکی آنلاین یا استرداد بانکی انجام نمی‌دهد.
- هیچ Commit، Push، حذف فایل موجود یا جابه‌جایی Package انجام نشده است. فایل قبلی `.idea/.gitignore` دست‌نخورده است.

## فایل‌های تغییرکرده

فهرست زیر شامل ۱۱۹ فایل source، schema، test و تنظیمات این تغییر است؛ خود این گزارش، خروجی‌های Gradle و فایل‌های موقت compiler جزو آن نیستند.

```text
app/schemas/ir.siamak.fintrack.store.data.local.StoreDatabase/2.json
app/src/androidTest/java/ir/siamak/fintrack/store/StoreMigrationTest.kt
app/src/androidTest/java/ir/siamak/fintrack/store/StoreNavigationTest.kt
app/src/androidTest/java/ir/siamak/fintrack/store/StorePersistenceTest.kt
app/src/androidTest/java/ir/siamak/fintrack/store/StoreWizardUiTest.kt
app/src/main/AndroidManifest.xml
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/baseinfo/installment/list/InstallmentRoute.kt
app/src/main/java/ir/siamak/fintrack/personalaccountant/presentation/navigation/AppNavGraph.kt
app/src/main/java/ir/siamak/fintrack/store/core/pdf/InvoicePdfGenerator.kt
app/src/main/java/ir/siamak/fintrack/store/core/validation/StoreValidators.kt
app/src/main/java/ir/siamak/fintrack/store/data/dao/CustomerDao.kt
app/src/main/java/ir/siamak/fintrack/store/data/dao/InvoiceDao.kt
app/src/main/java/ir/siamak/fintrack/store/data/dao/PreOrderDao.kt
app/src/main/java/ir/siamak/fintrack/store/data/dao/ProductDao.kt
app/src/main/java/ir/siamak/fintrack/store/data/dao/RawMaterialDao.kt
app/src/main/java/ir/siamak/fintrack/store/data/dao/SaleDao.kt
app/src/main/java/ir/siamak/fintrack/store/data/entity/InvoiceEntity.kt
app/src/main/java/ir/siamak/fintrack/store/data/local/StoreDatabase.kt
app/src/main/java/ir/siamak/fintrack/store/data/local/StoreMigrations.kt
app/src/main/java/ir/siamak/fintrack/store/data/local/StoreModule.kt
app/src/main/java/ir/siamak/fintrack/store/data/repository/InvoiceRepositoryImpl.kt
app/src/main/java/ir/siamak/fintrack/store/data/repository/MaterialRepositoryImpl.kt
app/src/main/java/ir/siamak/fintrack/store/data/repository/PreOrderRepositoryImpl.kt
app/src/main/java/ir/siamak/fintrack/store/data/repository/SaleRepositoryImpl.kt
app/src/main/java/ir/siamak/fintrack/store/data/repository/StoreRepositoryImpl.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/Customer.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/Discount.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/Installment.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/Invoice.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/Payment.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/PreOrder.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/PreOrderInvoice.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/PreOrderItem.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/PreOrderPricing.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/PreOrderStatus.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/Product.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/RawMaterial.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/Sale.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/SaleInvoice.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/SaleItem.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/Seller.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/Store.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/StoreOverview.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/StoreWorkspace.kt
app/src/main/java/ir/siamak/fintrack/store/domain/model/Tax.kt
app/src/main/java/ir/siamak/fintrack/store/domain/repository/InvoiceRepository.kt
app/src/main/java/ir/siamak/fintrack/store/domain/repository/PreOrderRepository.kt
app/src/main/java/ir/siamak/fintrack/store/domain/repository/SaleRepository.kt
app/src/main/java/ir/siamak/fintrack/store/domain/repository/StoreRepository.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/invoice/CalculateInvoiceBalanceUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/invoice/CalculateInvoicePaymentUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/material/AddMaterialUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/material/CalculateMaterialCostUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/preorder/CalculateAdvancePaymentUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/preorder/CalculatePreOrderPriceUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/preorder/CreatePreOrderUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/preorder/PlacePreOrderUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/preorder/ValidatePreOrderTransitionUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/product/UpdateProductUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/sale/ApplyDiscountUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/sale/CalculateInstallmentUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/sale/CalculateSaleAmountUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/sale/CheckoutSaleUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/sale/CreateSaleUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/store/CalculateStoreOverviewUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/store/ObserveStoreWorkspaceUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/domain/usecase/store/StoreCommandsUseCase.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/StoreUiState.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/StoreViewModel.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/baseinfo/BaseInfoHubScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/baseinfo/BaseInfoViewModel.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/baseinfo/material/AddMaterialScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/baseinfo/material/MaterialListScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/baseinfo/material/MaterialListViewModel.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/baseinfo/product/ProductManagementScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/baseinfo/seller/SellerManagementScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/baseinfo/store/StoreInfoScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/components/StoreUi.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/dashboard/StoreDashboardScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/dashboard/StoreDashboardUiState.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/dashboard/StoreDashboardViewModel.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/invoices/InvoiceListViewModel.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/invoices/InvoiceScreens.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/invoices/PreOrderInvoiceListScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/invoices/SaleInvoiceDetailScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/invoices/SaleInvoiceDetailViewModel.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/invoices/SaleInvoiceListScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/invoices/preorder/PreOrderInvoiceListScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/invoices/sale/SaleInvoiceDetailScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/invoices/sale/SaleInvoiceListScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/navigation/StoreNavGraph.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/PreOrderListScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/PreOrderListViewModel.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/create/CreatePreOrderEvent.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/create/CreatePreOrderScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/create/CreatePreOrderUiState.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/create/CreatePreOrderViewModel.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/create/PreOrderDescriptionScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/customer/PreOrderCustomerScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/invoice/PreOrderInvoiceScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/list/PreOrderListScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/materials/PreOrderMaterialScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/payment/PreOrderPaymentScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/preorder/pricing/PreOrderPricingScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/create/CreateSaleEvent.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/create/CreateSaleScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/create/CreateSaleUiState.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/create/CreateSaleViewModel.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/customer/SaleCustomerScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/discount/SaleDiscountScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/invoice/SaleInvoiceScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/list/SalesScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/list/SalesUiState.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/list/SalesViewModel.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/payment/SalePaymentScreen.kt
app/src/main/java/ir/siamak/fintrack/store/presentation/sales/products/SaleProductsScreen.kt
app/src/main/res/xml/store_file_paths.xml
app/src/test/java/ir/siamak/fintrack/store/domain/StoreCheckoutTest.kt
gradle.properties
```
