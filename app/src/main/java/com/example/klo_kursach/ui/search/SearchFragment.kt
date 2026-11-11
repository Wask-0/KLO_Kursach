package com.example.klo_kursach.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.klo_kursach.R
import com.example.klo_kursach.data.NotesRepository
import com.example.klo_kursach.databinding.FragmentSearchBinding
import com.example.klo_kursach.ui.notes.NotesAdapter
import com.example.klo_kursach.ui.notes.editorArgs
import com.google.android.material.chip.Chip

/** Полнотекстовый поиск по конспектам и тегам. */
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val adapter = NotesAdapter(onClick = { note ->
        findNavController().navigate(
            R.id.action_search_to_editor,
            editorArgs(this, note.id)
        )
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvResults.adapter = adapter
        binding.empty.emptyImage.setImageResource(R.drawable.ill_search)

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search(newText.orEmpty())
                return true
            }
        })

        setupTags()
        search(binding.searchView.query?.toString().orEmpty())
    }

    private fun setupTags() {
        val group = binding.chipGroupTags
        group.removeAllViews()
        NotesRepository.allTags().take(10).forEach { tag ->
            val chip = layoutInflater.inflate(R.layout.item_chip_filter, group, false) as Chip
            chip.text = "#$tag"
            chip.isCheckable = false
            chip.setOnClickListener { binding.searchView.setQuery("#$tag", true) }
            group.addView(chip)
        }
    }

    private fun search(query: String) {
        val results = NotesRepository.search(query)
        adapter.submitList(results)

        val hasQuery = query.isNotBlank()
        binding.tvResultsCount.isVisible = hasQuery
        binding.tvResultsCount.text = getString(R.string.search_results, results.size)

        binding.empty.root.isVisible = results.isEmpty()
        binding.empty.emptyTitle.setText(if (hasQuery) R.string.search_empty else R.string.nav_search)
        binding.empty.emptyText.setText(R.string.search_start)
        binding.empty.emptyText.isVisible = !hasQuery
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
