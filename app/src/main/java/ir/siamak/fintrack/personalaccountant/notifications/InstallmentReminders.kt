package ir.siamak.fintrack.personalaccountant.notifications

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.siamak.fintrack.MainActivity
import ir.siamak.fintrack.FinTrackApplication
import ir.siamak.fintrack.personalaccountant.data.local.database.AppDatabase
import ir.siamak.fintrack.personalaccountant.data.mapper.toModel
import ir.siamak.fintrack.personalaccountant.domain.analytics.loanSchedule
import ir.siamak.fintrack.personalaccountant.domain.settings.SettingsRepository
import ir.siamak.fintrack.di.PersonalAccountingDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class InstallmentReminders @Inject constructor(@ApplicationContext private val context: Context) {
    fun schedule() {
        val now = ZonedDateTime.now()
        var next = now.toLocalDate().atTime(8,30).atZone(now.zone)
        if(!next.isAfter(now)) next = next.plusDays(1)
        val pending = PendingIntent.getBroadcast(context, 4201, Intent(context,InstallmentReminderReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        context.getSystemService(AlarmManager::class.java).setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.toInstant().toEpochMilli(), pending)
    }
    fun clear(id: Long) { context.getSystemService(NotificationManager::class.java).cancel("loan",id.hashCode()) }
}

@AndroidEntryPoint class InstallmentReminderReceiver : BroadcastReceiver() {
    @Inject @PersonalAccountingDatabase lateinit var db: AppDatabase
    @Inject lateinit var settings: SettingsRepository
    @Inject lateinit var reminders: InstallmentReminders
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_TIME_CHANGED || intent.action == Intent.ACTION_TIMEZONE_CHANGED) { reminders.schedule(); return }
        val pending = goAsync()
        CoroutineScope(SupervisorJob()+Dispatchers.IO).launch {
            try {
                val restored = FinTrackApplication.restoreState.filter { !it.checking }.first()
                if(restored.error != null) return@launch
                val options = settings.getSettings()
                if(!options.notificationEnabled || !options.installmentReminder) return@launch
                if(Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return@launch
                val manager = context.getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(NotificationChannel("installments","سررسید اقساط",NotificationManager.IMPORTANCE_DEFAULT))
                val today = LocalDate.now()
                db.installmentDao().getAllInstallments().first().filter { !it.isDeleted }.forEach { row ->
                    val loan = row.toModel(); val schedule = loanSchedule(loan)
                    val due = schedule.nextDue
                    if(due == null || due.isAfter(today.plusDays(3))) { reminders.clear(loan.id); return@forEach }
                    val open = PendingIntent.getActivity(context, 4202, Intent(context,MainActivity::class.java).putExtra("finance_destination","loans"), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                    val date = ir.siamak.fintrack.common.core.formatter.DateFormatter.formatJalali(due)
                    val message = loan.title + " · سررسید: " + date + if(due.isBefore(today)) " · سررسید گذشته" else ""
                    manager.notify("loan",loan.id.hashCode(), Notification.Builder(context,"installments")
                        .setSmallIcon(android.R.drawable.ic_popup_reminder).setContentTitle("یادآوری قسط")
                        .setContentText(message).setStyle(Notification.BigTextStyle().bigText(message))
                        .setContentIntent(open).setAutoCancel(true).setVisibility(Notification.VISIBILITY_PRIVATE).build())
                }
            } catch(error: Exception) { android.util.Log.e("FinTrackReminder","Reminder check failed",error) }
            finally { reminders.schedule(); pending.finish() }
        }
    }
}
