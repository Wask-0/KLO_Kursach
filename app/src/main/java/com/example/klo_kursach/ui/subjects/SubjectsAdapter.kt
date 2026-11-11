package com.example.klo_kursach.ui.subjects

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.klo_kursach.R
import com.example.klo_kursach.data.NotesRepository
import com.example.klo_kursach.data.model.Subject
import com.example.klo_kursach.databinding.ItemSubjectBinding
import com.example.klo_kursach.util.subjectColor

/** Адаптер сетки предметов (папок). */
class SubjectsAdapter(
    private val onClick: (Subject) -> Unit,
    private val onLongClick: (Subject) -> Unit
) : ListAdapter<Subject, SubjectsAdapter.SubjectViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SubjectViewHolder(ItemSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) = holder.bind(getItem(position))

    inner class SubjectViewHolder(private val binding: ItemSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subject: Subject) = with(binding) {
            val context = root.context
            ImageViewCompat.setImageTintList(
                ivFolder,
                ColorStateList.valueOf(context.subjectColor(subject.colorIndex))
            )
            tvName.text = subject.name
            tvCount.text = context.getString(
                R.string.subject_notes_count,
                NotesRepository.countNotes(subject.id)
            )
            root.setOnClickListener { onClick(subject) }
            root.setOnLongClickListener {
                onLongClick(subject)
                true
            }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<Subject>() {
            override fun areItemsTheSame(oldItem: Subject, newItem: Subject) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Subject, newItem: Subject) = oldItem == newItem
        }
    }
}
