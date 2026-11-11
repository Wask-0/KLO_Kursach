package com.example.klo_kursach.ui.ocr

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.klo_kursach.R
import com.example.klo_kursach.databinding.FragmentOcrBinding
import com.example.klo_kursach.util.RESULT_INSERT_TEXT
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Распознавание печатного и рукописного текста с фотографии доски или слайда.
 * Макет экрана и выбор изображения готовы; распознавание будет выполнять ML Kit Text Recognition.
 */
class OcrFragment : Fragment() {

    private var _binding: FragmentOcrBinding? = null
    private val binding get() = _binding!!

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.ivPreview.setImageURI(uri)
            onImageSelected()
        }
    }

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            binding.ivPreview.setImageBitmap(bitmap)
            onImageSelected()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOcrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        val languages = resources.getStringArray(R.array.ocr_language_entries)
        if (actLanguage.text.isNullOrEmpty()) actLanguage.setText(languages.first(), false)

        btnGallery.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        btnCamera.setOnClickListener { takePhoto.launch(null) }
        btnRecognize.setOnClickListener { recognize() }
        btnCopy.setOnClickListener { copyResult() }
        btnInsert.setOnClickListener { insertIntoNote() }
    }

    private fun onImageSelected() {
        binding.ivPreview.isVisible = true
        binding.placeholder.isVisible = false
        binding.btnRecognize.isEnabled = true
    }

    private fun recognize() {
        binding.progress.isVisible = true
        binding.btnRecognize.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launch {
            delay(800) // имитация обработки до подключения ML Kit
            binding.progress.isVisible = false
            binding.btnRecognize.isEnabled = true
            binding.etResult.setText(R.string.ocr_stub_result)
        }
    }

    private fun copyResult() {
        val text = binding.etResult.text?.toString().orEmpty()
        if (text.isBlank()) return
        requireContext().getSystemService<ClipboardManager>()
            ?.setPrimaryClip(ClipData.newPlainText(getString(R.string.ocr_result), text))
        Toast.makeText(requireContext(), R.string.ocr_copied, Toast.LENGTH_SHORT).show()
    }

    private fun insertIntoNote() {
        val text = binding.etResult.text?.toString().orEmpty()
        if (text.isNotBlank()) {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(RESULT_INSERT_TEXT, text)
        }
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
