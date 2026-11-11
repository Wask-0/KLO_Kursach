package com.example.klo_kursach.data.model

/**
 * Предмет (папка) для группировки конспектов.
 * @param colorIndex индекс цвета в массиве ресурсов R.array.subject_colors
 */
data class Subject(
    val id: Long,
    val name: String,
    val colorIndex: Int
)
