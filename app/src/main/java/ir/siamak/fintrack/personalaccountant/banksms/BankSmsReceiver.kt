package ir.siamak.fintrack.personalaccountant.banksms

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import ir.siamak.fintrack.FinTrackApplication
import ir.siamak.fintrack.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@AndroidEntryPoint class BankSmsReceiver : BroadcastReceiver() {
    @Inject lateinit var inbox: BankSmsInbox
    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        val pending = goAsync()
        CoroutineScope(SupervisorJob()+Dispatchers.IO).launch {
            try { withTimeout(8000) {
                if(FinTrackApplication.restoreState.filter { !it.checking }.first().error != null) return@withTimeout
                if(!inbox.state.first().enabled) return@withTimeout
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                if(messages.isEmpty()) return@withTimeout
                val body = messages.joinToString("") { it.messageBody.orEmpty() }
                val sender = messages.first().originatingAddress.orEmpty()
                val time = messages.first().timestampMillis
                if(!inbox.capture(body,sender,time,automatic = true)) return@withTimeout
                if(Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return@withTimeout
                val manager = context.getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(NotificationChannel("bank_sms","بررسی پیامک بانکی",NotificationManager.IMPORTANCE_DEFAULT))
                val open = PendingIntent.getActivity(context,4203,Intent(context,MainActivity::class.java).putExtra("finance_destination","sms"),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                manager.notify("bank_sms",4203,Notification.Builder(context,"bank_sms").setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("پیامک بانکی آماده بررسی است").setContentText("برای انتخاب حساب و تأیید تراکنش وارد شوید.")
                    .setContentIntent(open).setAutoCancel(true).setVisibility(Notification.VISIBILITY_PRIVATE).build())
            } } catch(error: Exception) { android.util.Log.e("FinTrackSms","SMS review capture failed",error) }
            finally { pending.finish() }
        }
    }
}
