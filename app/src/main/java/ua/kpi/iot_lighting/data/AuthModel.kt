package ua.kpi.iot_lighting.data

data class UserProfile(
    val id: String,
    val name: String,
    val group: String,
    val email: String,
    val password: String = "",
    val role: String = "Інженер IoT-систем",
    val variant: String = "13"
) {
    val fullName: String get() = name
    val studentGroup: String get() = group
}

data class AuthValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)

object AuthValidator {
    fun validateLogin(email: String, password: String): AuthValidationResult {
        if (email.isBlank() || !email.contains("@") || !email.contains(".")) {
            return AuthValidationResult(false, "Введіть коректний Email")
        }
        if (password.length < 6) {
            return AuthValidationResult(false, "Пароль має містити щонайменше 6 символів")
        }
        return AuthValidationResult(true)
    }

    fun validateRegistration(
        name: String,
        group: String,
        email: String,
        password: String,
        confirmPassword: String = password
    ): AuthValidationResult {
        if (name.isBlank() || name.trim().length < 2) {
            return AuthValidationResult(false, "Вкажіть ПІБ користувача")
        }
        if (group.isBlank()) {
            return AuthValidationResult(false, "Вкажіть академічну групу")
        }
        if (email.isBlank() || !email.contains("@") || !email.contains(".")) {
            return AuthValidationResult(false, "Введіть коректну адресу Email")
        }
        if (password.isBlank() || password.length < 6) {
            return AuthValidationResult(false, "Пароль має містити щонайменше 6 символів")
        }
        if (password != confirmPassword) {
            return AuthValidationResult(false, "Паролі не співпадають! Перевірте підтвердження.")
        }
        return AuthValidationResult(true)
    }

    fun validateRegistration(
        fullName: String,
        email: String,
        password: String,
        group: String
    ): AuthValidationResult = validateRegistration(
        name = fullName,
        group = group,
        email = email,
        password = password,
        confirmPassword = password
    )
}
