package com.example.klo_kursach.ui.auth

import android.util.Patterns
import com.example.klo_kursach.R
import com.google.android.material.textfield.TextInputLayout

internal fun TextInputLayout.text(): String = editText?.text?.toString()?.trim().orEmpty()

/** Проверка обязательного поля; возвращает true, если поле заполнено. */
internal fun TextInputLayout.requireNotEmpty(): Boolean {
    val ok = text().isNotEmpty()
    error = if (ok) null else context.getString(R.string.error_empty_field)
    return ok
}

internal fun TextInputLayout.requireEmail(): Boolean {
    if (!requireNotEmpty()) return false
    val ok = Patterns.EMAIL_ADDRESS.matcher(text()).matches()
    error = if (ok) null else context.getString(R.string.error_email)
    return ok
}

internal fun TextInputLayout.requirePassword(): Boolean {
    if (!requireNotEmpty()) return false
    val ok = text().length >= 6
    error = if (ok) null else context.getString(R.string.error_password_short)
    return ok
}
