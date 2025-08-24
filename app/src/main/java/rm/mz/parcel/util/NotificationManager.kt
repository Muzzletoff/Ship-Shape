package rm.mz.parcel.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import rm.mz.parcel.R
import javax.inject.Inject
import javax.inject.Singleton
import rm.mz.parcel.ui.viewmodel.SettingsViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.activity.ComponentActivity
import rm.mz.parcel.ui.viewmodel.SettingsViewModelFactory

@Singleton
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val settingsViewModel by lazy {
        val factory = SettingsViewModelFactory(dataStore)
        ViewModelProvider(context as ComponentActivity, factory)[SettingsViewModel::class.java]
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Parcel Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for parcel updates"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    suspend fun showNotification(title: String, message: String) {
        if (settingsViewModel.notificationsEnabled.first()) {
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        }
    }

    companion object {
        private const val CHANNEL_ID = "parcel_notifications"
    }
} 