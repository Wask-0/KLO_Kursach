package com.example.klo_kursach.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.klo_kursach.R
import com.example.klo_kursach.databinding.FragmentAboutBinding
import com.example.klo_kursach.util.appVersion

/** Экран «О приложении». */
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvVersion.text = getString(R.string.about_version, requireContext().appVersion())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
