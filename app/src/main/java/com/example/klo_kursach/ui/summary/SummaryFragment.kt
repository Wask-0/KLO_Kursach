package com.example.klo_kursach.ui.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.klo_kursach.R
import com.example.klo_kursach.databinding.FragmentSummaryBinding
import com.example.klo_kursach.util.RESULT_INSERT_TEXT
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Генерация краткого содержания лекции.
 * Пока ИИ-сервис не подключён, используется простой локальный алгоритм
 * (выбор первых предложений), чтобы экран можно было проверить.
 */
class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            binding.etSource.setText(requireArguments().getString("sourceText").orEmpty())
        }
        binding.btnGenerate.setOnClickListener { generate() }
        binding.btnSaveResult.setOnClickListener { saveToNote() }
    }

    private fun generate() {
        val source = binding.etSource.text?.toString().orEmpty().trim()
        if (source.isEmpty()) {
            binding.tilSource.error = getString(R.string.ai_empty_source)
            return
        }
        binding.tilSource.error = null

        val sentences = when (binding.toggleLength.checkedButtonId) {
            R.id.btn_length_short -> 2
            R.id.btn_length_detailed -> 6
            else -> 4
        }

        binding.progress.isVisible = true
        binding.btnGenerate.isEnabled = false
        viewLifecycleOwner.lifecycleScope.launch {
            delay(800) // имитация запроса к ИИ
            binding.progress.isVisible = false
            binding.btnGenerate.isEnabled = true
            binding.tvResult.text = summarize(source, sentences)
            binding.resultBlock.isVisible = true
        }
    }

    private fun summarize(text: String, maxSentences: Int): String =
        text.split(Regex("(?<=[.!?])\\s+|\\n+"))
            .map { it.trim().trimStart('•', '#', '☐', ' ') }
            .filter { it.length > 3 }
            .take(maxSentences)
            .joinToString("\n") { "• $it" }

    private fun saveToNote() {
        val result = binding.tvResult.text.toString()
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            RESULT_INSERT_TEXT,
            getString(R.string.ai_summary_header) + "\n" + result
        )
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
