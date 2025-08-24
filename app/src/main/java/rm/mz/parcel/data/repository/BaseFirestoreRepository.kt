package rm.mz.parcel.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions

abstract class BaseFirestoreRepository(
    protected val firestore: FirebaseFirestore,
    protected val auth: FirebaseAuth,
    protected val functions: FirebaseFunctions
) {
    protected val currentUserId: String?
        get() = auth.currentUser?.uid

    protected val currentUserEmail: String?
        get() = auth.currentUser?.email

    protected fun requireAuth(): String {
        return currentUserId ?: throw Exception("User not authenticated")
    }
} 