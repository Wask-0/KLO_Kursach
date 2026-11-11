package com.example.klo_kursach.data.model

/** Конспект лекции. */
data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val subjectId: Long?,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/** Способ сортировки списка конспектов. */
enum class NoteSort { DATE, TITLE, SUBJECT }
