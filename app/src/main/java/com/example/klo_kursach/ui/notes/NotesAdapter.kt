package com.example.klo_kursach.ui.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.klo_kursach.R
import com.example.klo_kursach.data.NotesRepository
import com.example.klo_kursach.data.model.Note
import com.example.klo_kursach.databinding.ItemNoteBinding
import com.example.klo_kursach.util.formatDate
import com.example.klo_kursach.util.subjectColor

/** Адаптер списка конспектов (используется на экранах «Конспекты», «Предмет» и «Поиск»). */
class NotesAdapter(
    private val onClick: (Note) -> Unit,
    private val onMoreClick: ((Note, View) -> Unit)? = null
) : ListAdapter<Note, NotesAdapter.NoteViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder =
        NoteViewHolder(ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) = holder.bind(getItem(position))

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) = with(binding) {
            val context = root.context
            val subject = NotesRepository.getSubject(note.subjectId)

            tvSubject.isVisible = subject != null
            tvSubject.text = subject?.name
            viewSubjectColor.setBackgroundColor(context.subjectColor(subject?.colorIndex ?: 0))

            tvTitle.text = note.title
            tvPreview.text = note.content
            tvPreview.isVisible = note.content.isNotBlank()

            tvTags.isVisible = note.tags.isNotEmpty()
            tvTags.text = note.tags.joinToString(" ") { "#$it" }
            tvDate.text = context.getString(R.string.note_updated, note.updatedAt.formatDate())

            root.setOnClickListener { onClick(note) }
            btnMore.isVisible = onMoreClick != null
            btnMore.setOnClickListener { onMoreClick?.invoke(note, it) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem == newItem
        }
    }
}
