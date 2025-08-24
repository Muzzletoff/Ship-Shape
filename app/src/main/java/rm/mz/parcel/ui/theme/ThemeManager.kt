package rm.mz.parcel.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.Flow

object ThemeManager {
    private var isDarkTheme: Flow<Boolean>? = null
    
    fun initialize(darkThemeFlow: Flow<Boolean>) {
        isDarkTheme = darkThemeFlow
    }
    
    @Composable
    fun isDarkTheme(): Boolean {
        val darkTheme by isDarkTheme?.collectAsState(initial = false) ?: return false
        return darkTheme
    }
} 