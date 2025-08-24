package rm.mz.parcel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import rm.mz.parcel.navigation.ParcelNavHost
import rm.mz.parcel.navigation.Screen
import rm.mz.parcel.ui.theme.ParcelTheme
import rm.mz.parcel.ui.theme.ThemeManager
import rm.mz.parcel.util.AuthUtil
import rm.mz.parcel.ui.viewmodel.SettingsViewModel
import rm.mz.parcel.ui.viewmodel.SettingsViewModelFactory
import javax.inject.Inject
import androidx.lifecycle.ViewModelProvider


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authUtil: AuthUtil

    @Inject
    lateinit var settingsViewModelFactory: SettingsViewModelFactory

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        settingsViewModel = ViewModelProvider(this, settingsViewModelFactory)[SettingsViewModel::class.java]
        
        // Initialize theme manager
        ThemeManager.initialize(settingsViewModel.darkThemeEnabled)
        
        setContent {
            val isDarkTheme = ThemeManager.isDarkTheme()
            
            ParcelTheme(
                darkTheme = isDarkTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // Observe settings
                    val locationEnabled = settingsViewModel.locationTrackingEnabled.collectAsState(initial = true).value
                    val notificationsEnabled = settingsViewModel.notificationsEnabled.collectAsState(initial = true).value
                    
                    // Apply settings
                    LaunchedEffect(locationEnabled) {
                        if (locationEnabled) {
                            // Request location permissions if needed
                            requestLocationPermissions()
                        }
                    }
                    
                    LaunchedEffect(notificationsEnabled) {
                        if (notificationsEnabled) {
                            // Request notification permissions if needed
                            requestNotificationPermissions()
                        }
                    }
                    
                    // Observe auth state changes
                    LaunchedEffect(Unit) {
                        authUtil.authStateFlow.collect { isLoggedIn ->
                            if (!isLoggedIn) {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0)
                                }
                            }
                        }
                    }
                    
                    ParcelNavHost(
                        navController = navController,
                        startDestination = if (authUtil.isUserLoggedIn()) {
                            Screen.Home.route
                        } else {
                            Screen.Login.route
                        },
                        authUtil = authUtil
                    )
                }
            }
        }
    }
    
    private fun requestLocationPermissions() {
        // Implement location permission request
    }
    
    private fun requestNotificationPermissions() {
        // Implement notification permission request
    }
}