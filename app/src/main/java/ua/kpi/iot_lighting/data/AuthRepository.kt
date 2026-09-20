package ua.kpi.iot_lighting.data

import android.content.Context
import android.content.SharedPreferences

class AuthRepository(private val prefs: SharedPreferences) {

    constructor(context: Context) : this(
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    )

    companion object {
        const val PREFS_NAME = "iot_lighting_auth_prefs"
        private const val KEY_REGISTERED_EMAILS = "registered_emails"
        private const val KEY_ACTIVE_USER_EMAIL = "active_user_email"

        const val DEFAULT_USER_EMAIL = "student@kpi.ua"
        const val DEFAULT_USER_PASSWORD = "password123"
        const val DEFAULT_USER_NAME = "Студент-Інженер"
        const val DEFAULT_USER_GROUP = "ТВ-13"
        const val DEFAULT_USER_ROLE = "Інженер IoT-систем"
        const val DEFAULT_USER_VARIANT = "13"
        const val DEFAULT_USER_ID = "default_student_13"

        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(context: Context): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    init {
        seedDefaultUserIfNeeded()
    }

    private fun normalizeEmail(email: String): String = email.trim().lowercase()

    private fun seedDefaultUserIfNeeded() {
        val registeredEmails = prefs.getStringSet(KEY_REGISTERED_EMAILS, emptySet()) ?: emptySet()
        val defaultNormalized = normalizeEmail(DEFAULT_USER_EMAIL)
        if (registeredEmails.isEmpty() || !registeredEmails.contains(defaultNormalized)) {
            val defaultUser = UserProfile(
                id = DEFAULT_USER_ID,
                name = DEFAULT_USER_NAME,
                group = DEFAULT_USER_GROUP,
                email = DEFAULT_USER_EMAIL,
                password = DEFAULT_USER_PASSWORD,
                role = DEFAULT_USER_ROLE,
                variant = DEFAULT_USER_VARIANT
            )
            saveUserToPrefs(defaultUser)
        }
    }

    private fun saveUserToPrefs(user: UserProfile) {
        val normEmail = normalizeEmail(user.email)
        val currentSet = prefs.getStringSet(KEY_REGISTERED_EMAILS, emptySet())?.toMutableSet() ?: mutableSetOf()
        currentSet.add(normEmail)

        prefs.edit()
            .putStringSet(KEY_REGISTERED_EMAILS, currentSet)
            .putString("user_${normEmail}_id", user.id)
            .putString("user_${normEmail}_name", user.name)
            .putString("user_${normEmail}_group", user.group)
            .putString("user_${normEmail}_email", user.email)
            .putString("user_${normEmail}_password", user.password)
            .putString("user_${normEmail}_role", user.role)
            .putString("user_${normEmail}_variant", user.variant)
            .apply()
    }

    private fun getUserFromPrefs(normEmail: String): UserProfile? {
        val id = prefs.getString("user_${normEmail}_id", null) ?: return null
        val name = prefs.getString("user_${normEmail}_name", "") ?: ""
        val group = prefs.getString("user_${normEmail}_group", "") ?: ""
        val email = prefs.getString("user_${normEmail}_email", normEmail) ?: normEmail
        val password = prefs.getString("user_${normEmail}_password", "") ?: ""
        val role = prefs.getString("user_${normEmail}_role", DEFAULT_USER_ROLE) ?: DEFAULT_USER_ROLE
        val variant = prefs.getString("user_${normEmail}_variant", DEFAULT_USER_VARIANT) ?: DEFAULT_USER_VARIANT

        return UserProfile(
            id = id,
            name = name,
            group = group,
            email = email,
            password = password,
            role = role,
            variant = variant
        )
    }

    fun registerUser(user: UserProfile): Result<Unit> {
        val normEmail = normalizeEmail(user.email)
        val registeredEmails = prefs.getStringSet(KEY_REGISTERED_EMAILS, emptySet()) ?: emptySet()
        if (registeredEmails.contains(normEmail)) {
            return Result.failure(IllegalArgumentException("Користувач із таким Email вже існує"))
        }

        saveUserToPrefs(user)
        return Result.success(Unit)
    }

    fun authenticate(email: String, password: String): UserProfile? {
        val normEmail = normalizeEmail(email)
        val user = getUserFromPrefs(normEmail) ?: return null
        return if (user.password == password) user else null
    }

    fun saveSession(userId: String) {
        val registeredEmails = prefs.getStringSet(KEY_REGISTERED_EMAILS, emptySet()) ?: emptySet()
        for (normEmail in registeredEmails) {
            if (prefs.getString("user_${normEmail}_id", null) == userId) {
                prefs.edit().putString(KEY_ACTIVE_USER_EMAIL, normEmail).apply()
                return
            }
        }
        val user = getUserFromPrefs(normalizeEmail(userId))
        if (user != null) {
            prefs.edit().putString(KEY_ACTIVE_USER_EMAIL, normalizeEmail(userId)).apply()
        }
    }

    fun saveSession(user: UserProfile) {
        prefs.edit().putString(KEY_ACTIVE_USER_EMAIL, normalizeEmail(user.email)).apply()
    }

    fun saveSessionByEmail(email: String) {
        prefs.edit().putString(KEY_ACTIVE_USER_EMAIL, normalizeEmail(email)).apply()
    }

    fun getActiveSession(): UserProfile? {
        val activeEmail = prefs.getString(KEY_ACTIVE_USER_EMAIL, null) ?: return null
        return getUserFromPrefs(activeEmail)
    }

    fun clearSession() {
        prefs.edit().remove(KEY_ACTIVE_USER_EMAIL).apply()
    }

    fun getDefaultUser(): UserProfile {
        return getUserFromPrefs(normalizeEmail(DEFAULT_USER_EMAIL)) ?: UserProfile(
            id = DEFAULT_USER_ID,
            name = DEFAULT_USER_NAME,
            group = DEFAULT_USER_GROUP,
            email = DEFAULT_USER_EMAIL,
            password = DEFAULT_USER_PASSWORD,
            role = DEFAULT_USER_ROLE,
            variant = DEFAULT_USER_VARIANT
        )
    }
}
