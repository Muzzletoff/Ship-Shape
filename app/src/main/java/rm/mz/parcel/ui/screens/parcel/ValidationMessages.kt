package rm.mz.parcel.ui.screens.parcel

object ValidationMessages {
    fun getFieldError(fieldName: String, value: String): String? {
        return when {
            value.isBlank() -> "$fieldName cannot be empty"
            else -> null
        }
    }

    fun getEstimatedDaysError(value: String): String? {
        return when {
            value.isBlank() -> "Estimated days cannot be empty"
            value.toIntOrNull() == null -> "Must be a valid number"
            value.toInt() <= 0 -> "Must be greater than 0"
            else -> null
        }
    }
} 