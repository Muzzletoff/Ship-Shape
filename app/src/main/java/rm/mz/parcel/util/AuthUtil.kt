package rm.mz.parcel.util

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthUtil @Inject constructor(
    private val auth: FirebaseAuth
) {
    private val _authStateFlow = MutableStateFlow(auth.currentUser != null)
    val authStateFlow: StateFlow<Boolean> = _authStateFlow

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _authStateFlow.value = firebaseAuth.currentUser != null
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    fun signOut() {
        auth.signOut()
    }
} 