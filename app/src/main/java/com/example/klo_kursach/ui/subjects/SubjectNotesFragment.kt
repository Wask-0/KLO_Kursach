package com.example.klo_kursach.ui.subjects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.klo_kursach.R
import com.example.klo_kursach.data.NotesRepository
import com.example.klo_kursach.databinding.FragmentSubjectNotesBinding
import com.example.klo_kursach.ui.notes.NotesAdapter
import com.example.klo_kursach.ui.notes.editorArgs

/** Конспекты внутри выбранного предмета (папки). */
class SubjectNotesFragment : Fragment() {

    private var _binding: FragmentSubjectNotesBinding? = null
    private val binding get() = _binding!!

    private val subjectId: Long by lazy { requireArguments().getLong("subjectId") }

    private val adapter = NotesAdapter(onClick = { openEditor(it.id) })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSubjectNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvNotes.adapter = adapter
        binding.empty.emptyImage.setImageResource(R.drawable.ill_empty_notes)
        binding.empty.emptyTitle.setText(R.string.subject_notes_empty)
        binding.empty.emptyText.setText(R.string.notes_empty_text)
        binding.fabAdd.setOnClickListener { openEditor(-1L) }

        val notes = NotesRepository.getNotes(subjectId)
        adapter.submitList(notes)
        binding.empty.root.isVisible = notes.isEmpty()
    }

    private fun openEditor(noteId: Long) {
        findNavController().navigate(
            R.id.action_subjectNotes_to_editor,
            editorArgs(this, noteId, subjectId)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
