package com.example.klo_kursach.data.model

/** Данные пользователя: ФИО, учебная группа, email. */
data class User(
    val fullName: String,
    val group: String,
    val email: String
) {
    /** Инициалы для аватара, например «КЛ». */
    val initials: String
        get() = fullName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
}
