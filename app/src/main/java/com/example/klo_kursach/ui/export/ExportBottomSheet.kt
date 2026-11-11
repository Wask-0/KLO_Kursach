package com.example.klo_kursach.ui.export

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.klo_kursach.R
import com.example.klo_kursach.data.NotesRepository
import com.example.klo_kursach.databinding.BottomSheetExportBinding
import com.example.klo_kursach.databinding.ItemExportOptionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/** Нижний лист выбора формата экспорта конспекта (PDF / TXT / поделиться). */
class ExportBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetExportBinding? = null
    private val binding get() = _binding!!

    private val noteId: Long by lazy { requireArguments().getLong("noteId", -1L) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetExportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.optionPdf.setup(R.drawable.ic_pdf, R.string.export_pdf, R.string.export_pdf_desc) {
            showInProgress()
        }
        binding.optionTxt.setup(R.drawable.ic_txt, R.string.export_txt, R.string.export_txt_desc) {
            showInProgress()
        }
        binding.optionShare.setup(R.drawable.ic_share, R.string.export_share, R.string.export_share_desc) {
            shareNote()
        }
    }

    private fun ItemExportOptionBinding.setup(icon: Int, title: Int, desc: Int, onClick: () -> Unit) {
        ivIcon.setImageResource(icon)
        tvTitle.setText(title)
        tvDesc.setText(desc)
        root.setOnClickListener { onClick() }
    }

    private fun shareNote() {
        val note = NotesRepository.getNote(noteId) ?: return dismiss()
        val text = buildString {
            appendLine(note.title)
            appendLine()
            append(note.content)
        }
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).setType("text/plain")
                    .putExtra(Intent.EXTRA_SUBJECT, note.title)
                    .putExtra(Intent.EXTRA_TEXT, text),
                getString(R.string.export_share)
            )
        )
        dismiss()
    }

    private fun showInProgress() {
        Toast.makeText(requireContext(), R.string.feature_in_progress, Toast.LENGTH_SHORT).show()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
