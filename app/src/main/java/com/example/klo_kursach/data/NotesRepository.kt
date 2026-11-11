package com.example.klo_kursach.data

import com.example.klo_kursach.data.model.Note
import com.example.klo_kursach.data.model.NoteSort
import com.example.klo_kursach.data.model.Subject

/**
 * Хранилище конспектов и предметов.
 *
 * На этапе разработки интерфейса данные хранятся в памяти и заполнены примерами,
 * чтобы экраны можно было проверить. На следующем этапе класс будет переведён
 * на локальную базу данных Room (офлайн-режим).
 */
object NotesRepository {

    private val subjects = mutableListOf(
        Subject(1, "Разработка мобильных приложений", 0),
        Subject(2, "Базы данных", 1),
        Subject(3, "Математика", 2),
        Subject(4, "Английский язык", 3),
        Subject(5, "Компьютерные сети", 4)
    )

    private val notes = mutableListOf(
        Note(
            id = 1,
            title = "Жизненный цикл Activity",
            content = "# Жизненный цикл\nonCreate → onStart → onResume → onPause → onStop → onDestroy.\n" +
                "• В onCreate выполняется инициализация интерфейса.\n" +
                "• В onPause нужно сохранять несохранённые данные.",
            subjectId = 1,
            tags = listOf("android", "экзамен"),
            updatedAt = System.currentTimeMillis() - 2 * HOUR
        ),
        Note(
            id = 2,
            title = "Navigation Component",
            content = "Граф навигации описывает экраны (destinations) и переходы (actions) между ними. " +
                "NavHostFragment служит контейнером, NavController выполняет переходы.",
            subjectId = 1,
            tags = listOf("android", "навигация"),
            updatedAt = System.currentTimeMillis() - DAY
        ),
        Note(
            id = 3,
            title = "Нормализация отношений",
            content = "1НФ — атомарные значения атрибутов.\n2НФ — нет частичных зависимостей от ключа.\n" +
                "3НФ — нет транзитивных зависимостей.",
            subjectId = 2,
            tags = listOf("нф", "экзамен"),
            updatedAt = System.currentTimeMillis() - 2 * DAY
        ),
        Note(
            id = 4,
            title = "Пределы функций",
            content = "Первый замечательный предел: lim(x→0) sin x / x = 1.\n" +
                "Второй замечательный предел: lim(x→∞) (1 + 1/x)^x = e.",
            subjectId = 3,
            tags = listOf("пределы"),
            updatedAt = System.currentTimeMillis() - 3 * DAY
        ),
        Note(
            id = 5,
            title = "Present Perfect",
            content = "have/has + V3. Используется для действий, результат которых важен сейчас.",
            subjectId = 4,
            tags = listOf("grammar"),
            updatedAt = System.currentTimeMillis() - 5 * DAY
        ),
        Note(
            id = 6,
            title = "Модель OSI",
            content = "Семь уровней: физический, канальный, сетевой, транспортный, сеансовый, " +
                "представления, прикладной.",
            subjectId = 5,
            tags = listOf("osi", "экзамен"),
            updatedAt = System.currentTimeMillis() - 7 * DAY
        )
    )

    private var nextNoteId = (notes.maxOfOrNull { it.id } ?: 0) + 1
    private var nextSubjectId = (subjects.maxOfOrNull { it.id } ?: 0) + 1

    // ----- Предметы -----

    fun getSubjects(): List<Subject> = subjects.toList()

    fun getSubject(id: Long?): Subject? = subjects.firstOrNull { it.id == id }

    fun addSubject(name: String): Subject {
        val subject = Subject(nextSubjectId++, name.trim(), subjects.size)
        subjects += subject
        return subject
    }

    fun renameSubject(id: Long, name: String) {
        val index = subjects.indexOfFirst { it.id == id }
        if (index >= 0) subjects[index] = subjects[index].copy(name = name.trim())
    }

    fun countNotes(subjectId: Long): Int = notes.count { it.subjectId == subjectId }

    // ----- Конспекты -----

    fun getNotes(subjectId: Long? = null, sort: NoteSort = NoteSort.DATE): List<Note> {
        val filtered = if (subjectId == null) notes else notes.filter { it.subjectId == subjectId }
        return when (sort) {
            NoteSort.DATE -> filtered.sortedByDescending { it.updatedAt }
            NoteSort.TITLE -> filtered.sortedBy { it.title.lowercase() }
            NoteSort.SUBJECT -> filtered.sortedBy { getSubject(it.subjectId)?.name.orEmpty() }
        }
    }

    fun getNote(id: Long): Note? = notes.firstOrNull { it.id == id }

    /** Сохраняет новый (id = 0) или изменённый конспект и возвращает сохранённую версию. */
    fun save(note: Note): Note {
        val now = System.currentTimeMillis()
        val index = notes.indexOfFirst { it.id == note.id }
        return if (note.id == 0L || index < 0) {
            note.copy(id = nextNoteId++, createdAt = now, updatedAt = now).also { notes += it }
        } else {
            note.copy(updatedAt = now).also { notes[index] = it }
        }
    }

    fun delete(id: Long): Note? {
        val note = getNote(id) ?: return null
        notes.remove(note)
        return note
    }

    /** Возвращает удалённый конспект (для «Отменить» в Snackbar). */
    fun restore(note: Note) {
        if (getNote(note.id) == null) notes += note
    }

    // ----- Поиск -----

    fun allTags(): List<String> =
        notes.flatMap { it.tags }.groupingBy { it }.eachCount()
            .entries.sortedByDescending { it.value }.map { it.key }

    /** Полнотекстовый поиск по названию, тексту и тегам. Запрос вида «#тег» ищет только по тегам. */
    fun search(query: String): List<Note> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        if (q.startsWith("#")) {
            val tag = q.removePrefix("#")
            return notes.filter { note -> note.tags.any { it.lowercase() == tag } }
                .sortedByDescending { it.updatedAt }
        }
        return notes.filter { note ->
            note.title.lowercase().contains(q) ||
                note.content.lowercase().contains(q) ||
                note.tags.any { it.lowercase().contains(q) }
        }.sortedByDescending { it.updatedAt }
    }

    private const val HOUR = 60 * 60 * 1000L
    private const val DAY = 24 * HOUR
}
