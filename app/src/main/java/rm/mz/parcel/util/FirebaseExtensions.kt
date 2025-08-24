package rm.mz.parcel.util

import com.google.firebase.auth.SignInMethodQueryResult

fun SignInMethodQueryResult.isEmpty(): Boolean {
    return signInMethods?.isEmpty() ?: true
} 