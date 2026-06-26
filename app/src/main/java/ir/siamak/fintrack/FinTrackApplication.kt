package ir.siamak.fintrack

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * کلاس Application اصلی پروژه.
 *
 * این کلاس نقطه شروع Hilt در اپلیکیشن است و باید
 * در فایل Manifest نیز به عنوان application name ثبت شود.
 */
@HiltAndroidApp
class FinTrackApplication : Application()
