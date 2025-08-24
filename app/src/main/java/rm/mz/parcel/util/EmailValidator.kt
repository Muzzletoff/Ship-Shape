package rm.mz.parcel.util

object EmailValidator {
    private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()

    fun isValid(email: String): Boolean {
        return email.isNotBlank() && EMAIL_REGEX.matches(email)
    }

    fun getErrorMessage(email: String): String? {
        return when {
            email.isBlank() -> "Email cannot be empty"
            !EMAIL_REGEX.matches(email) -> "Invalid email format"
            else -> null
        }
    }
} 