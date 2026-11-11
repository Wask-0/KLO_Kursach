package com.example.klo_kursach.data

import android.content.Context
import androidx.core.content.edit
import com.example.klo_kursach.data.model.User

/** Хранение сессии и профиля пользователя в SharedPreferences. */
object UserSession {
    private const val PREFS = "user_session"
    private const val KEY_LOGGED_IN = "logged_in"
    private const val KEY_GUEST = "guest"
    private const val KEY_NAME = "full_name"
    private const val KEY_GROUP = "group"
    private const val KEY_EMAIL = "email"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isLoggedIn(context: Context): Boolean = prefs(context).getBoolean(KEY_LOGGED_IN, false)

    fun isGuest(context: Context): Boolean = prefs(context).getBoolean(KEY_GUEST, false)

    fun currentUser(context: Context): User? {
        val p = prefs(context)
        val email = p.getString(KEY_EMAIL, null) ?: return null
        return User(
            fullName = p.getString(KEY_NAME, "").orEmpty(),
            group = p.getString(KEY_GROUP, "").orEmpty(),
            email = email
        )
    }

    fun register(context: Context, user: User) = prefs(context).edit {
        putBoolean(KEY_LOGGED_IN, true)
        putBoolean(KEY_GUEST, false)
        putString(KEY_NAME, user.fullName)
        putString(KEY_GROUP, user.group)
        putString(KEY_EMAIL, user.email)
    }

    /** Вход по email. Проверка пароля появится вместе с серверным API. */
    fun login(context: Context, email: String) = prefs(context).edit {
        putBoolean(KEY_LOGGED_IN, true)
        putBoolean(KEY_GUEST, false)
        if (prefs(context).getString(KEY_EMAIL, null) != email) {
            putString(KEY_EMAIL, email)
            putString(KEY_NAME, email.substringBefore('@'))
            putString(KEY_GROUP, "")
        }
    }

    fun continueAsGuest(context: Context) = prefs(context).edit {
        putBoolean(KEY_LOGGED_IN, true)
        putBoolean(KEY_GUEST, true)
    }

    fun logout(context: Context) = prefs(context).edit {
        putBoolean(KEY_LOGGED_IN, false)
        putBoolean(KEY_GUEST, false)
    }
}
