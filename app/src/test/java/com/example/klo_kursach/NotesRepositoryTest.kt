package com.example.klo_kursach

import com.example.klo_kursach.data.NotesRepository
import com.example.klo_kursach.data.model.Note
import com.example.klo_kursach.data.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NotesRepositoryTest {

    @Test
    fun searchByTag_returnsOnlyTaggedNotes() {
        val results = NotesRepository.search("#экзамен")
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { note -> "экзамен" in note.tags })
    }

    @Test
    fun saveAndDelete_note() {
        val saved = NotesRepository.save(Note(title = "Тест", content = "текст", subjectId = 1))
        assertTrue(saved.id > 0)
        assertEquals("Тест", NotesRepository.getNote(saved.id)?.title)
        NotesRepository.delete(saved.id)
        assertNull(NotesRepository.getNote(saved.id))
    }

    @Test
    fun userInitials() {
        assertEquals("КЛ", User("Корнилов Лев", "С322", "a@b.ru").initials)
    }
}
