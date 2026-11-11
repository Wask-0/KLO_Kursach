package com.example.klo_kursach.ui.subjects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.klo_kursach.R
import com.example.klo_kursach.data.NotesRepository
import com.example.klo_kursach.data.model.Subject
import com.example.klo_kursach.databinding.DialogAddSubjectBinding
import com.example.klo_kursach.databinding.FragmentSubjectsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Структурирование конспектов по предметам (папкам). */
class SubjectsFragment : Fragment() {

    private var _binding: FragmentSubjectsBinding? = null
    private val binding get() = _binding!!

    private val adapter = SubjectsAdapter(
        onClick = { subject ->
            findNavController().navigate(
                R.id.action_subjects_to_subjectNotes,
                bundleOf("subjectId" to subject.id, "subjectName" to subject.name)
            )
        },
        onLongClick = { subject -> showSubjectDialog(R.string.menu_rename, subject) }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvSubjects.adapter = adapter
        binding.empty.emptyImage.setImageResource(R.drawable.ic_folder)
        binding.empty.emptyTitle.setText(R.string.nav_subjects)
        binding.empty.emptyText.setText(R.string.subjects_empty)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_subjects, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId != R.id.action_add_subject) return false
                showSubjectDialog(R.string.menu_add_subject, null)
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        refresh()
    }

    private fun refresh() {
        val subjects = NotesRepository.getSubjects()
        adapter.submitList(subjects)
        binding.empty.root.isVisible = subjects.isEmpty()
    }

    /** Диалог добавления (subject == null) или переименования предмета. */
    private fun showSubjectDialog(@StringRes title: Int, subject: Subject?) {
        val dialogBinding = DialogAddSubjectBinding.inflate(layoutInflater)
        dialogBinding.etSubjectName.setText(subject?.name)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(if (subject == null) R.string.add else R.string.ok) { _, _ ->
                val name = dialogBinding.etSubjectName.text?.toString().orEmpty()
                if (name.isNotBlank()) {
                    if (subject == null) NotesRepository.addSubject(name)
                    else NotesRepository.renameSubject(subject.id, name)
                    refresh()
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
