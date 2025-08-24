package rm.mz.parcel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    companion object {
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val DARK_THEME_ENABLED = booleanPreferencesKey("dark_theme_enabled")
        private val LOCATION_TRACKING_ENABLED = booleanPreferencesKey("location_tracking_enabled")
    }

    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: true
    }

    val darkThemeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_THEME_ENABLED] ?: false
    }

    val locationTrackingEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[LOCATION_TRACKING_ENABLED] ?: true
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_ENABLED] = enabled
        }
    }

    suspend fun setLocationTrackingEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOCATION_TRACKING_ENABLED] = enabled
        }
    }
} 