package com.example.klo_kursach.ui.notes

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.klo_kursach.R
import com.example.klo_kursach.data.NotesRepository
import com.example.klo_kursach.data.model.Note
import com.google.android.material.snackbar.Snackbar

/** Аргументы для открытия редактора конспекта. */
fun editorArgs(fragment: Fragment, noteId: Long = -1L, subjectId: Long = -1L) = bundleOf(
    "noteId" to noteId,
    "subjectId" to subjectId,
    "title" to fragment.getString(
        if (noteId > 0) R.string.title_editor_edit else R.string.title_editor_new
    )
)

/** Удаление конспекта с возможностью отмены через Snackbar. */
fun Fragment.deleteNoteWithUndo(note: Note, anchor: View, onChanged: () -> Unit) {
    NotesRepository.delete(note.id)
    onChanged()
    Snackbar.make(anchor, R.string.note_deleted, Snackbar.LENGTH_LONG)
        .setAction(R.string.undo) {
            NotesRepository.restore(note)
            onChanged()
        }
        .show()
}

/** Переход к нижнему листу экспорта. */
fun Fragment.openExport(actionId: Int, noteId: Long) {
    findNavController().navigate(actionId, bundleOf("noteId" to noteId))
}
