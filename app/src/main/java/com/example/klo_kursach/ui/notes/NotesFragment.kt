package com.example.klo_kursach.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.klo_kursach.R
import com.example.klo_kursach.data.NotesRepository
import com.example.klo_kursach.data.model.Note
import com.example.klo_kursach.data.model.NoteSort
import com.example.klo_kursach.databinding.FragmentNotesBinding
import com.google.android.material.chip.Chip

/** Главный экран: все конспекты с фильтром по предметам и сортировкой. */
class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private var sort = NoteSort.DATE
    private var selectedSubjectId: Long? = null

    private val adapter = NotesAdapter(
        onClick = { openEditor(it.id) },
        onMoreClick = { note, anchor -> showNoteMenu(note, anchor) }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvNotes.adapter = adapter
        binding.empty.emptyTitle.setText(R.string.notes_empty_title)
        binding.empty.emptyText.setText(R.string.notes_empty_text)

        binding.fabAdd.setOnClickListener {
            openEditor(-1L, selectedSubjectId ?: -1L)
        }
        // Сворачиваем расширенную FAB при прокрутке списка
        binding.rvNotes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) binding.fabAdd.shrink() else if (dy < 0) binding.fabAdd.extend()
            }
        })

        setupSubjectChips()
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
        refresh()
    }

    private fun setupSubjectChips() {
        val group = binding.chipGroupSubjects
        group.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        fun addChip(text: String, subjectId: Long?) {
            val chip = inflater.inflate(R.layout.item_chip_filter, group, false) as Chip
            chip.id = View.generateViewId()
            chip.text = text
            chip.tag = subjectId
            chip.isChecked = subjectId == selectedSubjectId
            group.addView(chip)
        }

        addChip(getString(R.string.filter_all), null)
        NotesRepository.getSubjects().forEach { addChip(it.name, it.id) }

        group.setOnCheckedStateChangeListener { chipGroup, checkedIds ->
            val chip = checkedIds.firstOrNull()?.let { chipGroup.findViewById<Chip>(it) }
            selectedSubjectId = chip?.tag as? Long
            refresh()
        }
    }

    private fun refresh() {
        val b = _binding ?: return // экран мог быть уже закрыт (например, «Отменить» в Snackbar)
        val notes = NotesRepository.getNotes(selectedSubjectId, sort)
        adapter.submitList(notes)
        b.empty.root.isVisible = notes.isEmpty()
    }

    private fun openEditor(noteId: Long, subjectId: Long = -1L) {
        findNavController().navigate(
            R.id.action_notes_to_editor,
            editorArgs(this, noteId, subjectId)
        )
    }

    private fun showNoteMenu(note: Note, anchor: View) {
        PopupMenu(requireContext(), anchor).apply {
            inflate(R.menu.menu_note_item)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.item_edit -> openEditor(note.id)
                    R.id.item_export -> openExport(R.id.action_notes_to_export, note.id)
                    R.id.item_delete -> deleteNoteWithUndo(note, binding.root) { refresh() }
                }
                true
            }
            show()
        }
    }

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_notes, menu)
        }

        override fun onPrepareMenu(menu: Menu) {
            val checkedId = when (sort) {
                NoteSort.DATE -> R.id.sort_date
                NoteSort.TITLE -> R.id.sort_title
                NoteSort.SUBJECT -> R.id.sort_subject
            }
            menu.findItem(checkedId)?.isChecked = true
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            sort = when (menuItem.itemId) {
                R.id.sort_date -> NoteSort.DATE
                R.id.sort_title -> NoteSort.TITLE
                R.id.sort_subject -> NoteSort.SUBJECT
                else -> return false
            }
            menuItem.isChecked = true
            refresh()
            return true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
