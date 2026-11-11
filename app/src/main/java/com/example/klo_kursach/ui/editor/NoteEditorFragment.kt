package com.example.klo_kursach.ui.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.klo_kursach.R
import com.example.klo_kursach.data.NotesRepository
import com.example.klo_kursach.data.model.Note
import com.example.klo_kursach.databinding.FragmentNoteEditorBinding
import com.example.klo_kursach.util.RESULT_INSERT_TEXT
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * Создание и редактирование конспекта: название, предмет, теги, текст
 * и панель форматирования. Отсюда открываются OCR, ИИ-пересказ и экспорт.
 */
class NoteEditorFragment : Fragment() {

    private var _binding: FragmentNoteEditorBinding? = null
    private val binding get() = _binding!!

    /** id редактируемого конспекта; -1 — новый, ещё не сохранённый. */
    private var noteId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = savedInstanceState?.getLong(STATE_NOTE_ID)
            ?: requireArguments().getLong("noteId", -1L)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNoteEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupSubjects()
        fillFromNote()
        setupFormatBar()
        receiveInsertedText()
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupSubjects() {
        val names = NotesRepository.getSubjects().map { it.name }.toTypedArray()
        binding.actSubject.setSimpleItems(names)
    }

    private fun fillFromNote() {
        val note = NotesRepository.getNote(noteId)
        val subjectId = note?.subjectId ?: requireArguments().getLong("subjectId", -1L)
        NotesRepository.getSubject(subjectId)?.let { binding.actSubject.setText(it.name, false) }
        if (note != null) {
            binding.etTitle.setText(note.title)
            binding.etTags.setText(note.tags.joinToString(", "))
            binding.etBody.setText(note.content)
        }
    }

    // ----- Форматирование (Markdown-разметка; визуальное оформление — на следующем этапе) -----

    private fun setupFormatBar() = with(binding) {
        btnBold.setOnClickListener { etBody.wrapSelection("**") }
        btnItalic.setOnClickListener { etBody.wrapSelection("_") }
        btnHeading.setOnClickListener { etBody.prefixLine("# ") }
        btnList.setOnClickListener { etBody.prefixLine("• ") }
        btnChecklist.setOnClickListener { etBody.prefixLine("☐ ") }
        btnAttachImage.setOnClickListener { showInProgress() }
        btnAttachAudio.setOnClickListener { showInProgress() }
        btnOcr.setOnClickListener { openOcr() }
        btnAi.setOnClickListener { openSummary() }
    }

    private fun EditText.wrapSelection(marker: String) {
        val start = minOf(selectionStart, selectionEnd).coerceAtLeast(0)
        val end = maxOf(selectionStart, selectionEnd).coerceAtLeast(0)
        text.insert(end, marker)
        text.insert(start, marker)
        setSelection(if (start == end) start + marker.length else end + marker.length * 2)
    }

    private fun EditText.prefixLine(prefix: String) {
        val cursor = selectionStart.coerceAtLeast(0)
        val lineStart = text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)).let {
            if (it < 0 || cursor == 0) 0 else it + 1
        }
        text.insert(lineStart, prefix)
    }

    /** Текст, возвращённый экранами OCR / ИИ, добавляется в конец конспекта. */
    private fun receiveInsertedText() {
        val handle = findNavController().currentBackStackEntry?.savedStateHandle ?: return
        handle.getLiveData<String>(RESULT_INSERT_TEXT).observe(viewLifecycleOwner) { text ->
            if (text.isNullOrBlank()) return@observe
            val body = binding.etBody.text
            if (body.isNotEmpty() && !body.endsWith("\n")) body.append("\n")
            body.append(text)
            handle.remove<String>(RESULT_INSERT_TEXT)
        }
    }

    // ----- Действия -----

    private fun saveNote(): Note? {
        val title = binding.etTitle.text.toString().trim()
        if (title.isEmpty()) {
            binding.etTitle.error = getString(R.string.error_title_required)
            binding.etTitle.requestFocus()
            return null
        }
        val subjectName = binding.actSubject.text.toString()
        val subject = NotesRepository.getSubjects().firstOrNull { it.name == subjectName }
        val tags = binding.etTags.text.toString()
            .split(",", " ")
            .map { it.trim().removePrefix("#") }
            .filter { it.isNotEmpty() }
            .distinct()

        val existing = NotesRepository.getNote(noteId)
        val saved = NotesRepository.save(
            (existing ?: Note(title = title, content = "", subjectId = null)).copy(
                title = title,
                content = binding.etBody.text.toString(),
                subjectId = subject?.id,
                tags = tags
            )
        )
        noteId = saved.id
        requireActivity().invalidateOptionsMenu()
        return saved
    }

    private fun openOcr() = findNavController().navigate(R.id.action_editor_to_ocr)

    private fun openSummary() = findNavController().navigate(
        R.id.action_editor_to_summary,
        bundleOf("sourceText" to binding.etBody.text.toString())
    )

    private fun confirmDelete() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete_text)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.menu_delete) { _, _ ->
                NotesRepository.delete(noteId)
                Toast.makeText(requireContext(), R.string.note_deleted, Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun showInProgress() {
        Snackbar.make(binding.root, R.string.feature_in_progress, Snackbar.LENGTH_SHORT).show()
    }

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_note_editor, menu)
        }

        override fun onPrepareMenu(menu: Menu) {
            menu.findItem(R.id.action_delete)?.isVisible = noteId > 0
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.action_save -> saveNote()?.let {
                    Toast.makeText(requireContext(), R.string.note_saved, Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                R.id.action_ocr -> openOcr()
                R.id.action_ai_summary -> openSummary()
                R.id.action_export -> saveNote()?.let {
                    findNavController().navigate(R.id.action_editor_to_export, bundleOf("noteId" to it.id))
                }
                R.id.action_delete -> confirmDelete()
                else -> return false
            }
            return true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(STATE_NOTE_ID, noteId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private companion object {
        const val STATE_NOTE_ID = "state_note_id"
    }
}
