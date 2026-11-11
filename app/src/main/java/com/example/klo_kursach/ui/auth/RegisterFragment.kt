package com.example.klo_kursach.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.klo_kursach.R
import com.example.klo_kursach.data.UserSession
import com.example.klo_kursach.data.model.User
import com.example.klo_kursach.databinding.FragmentRegisterBinding

/** Экран регистрации: ФИО, учебная группа, email и пароль. */
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnRegister.setOnClickListener { register() }
        binding.btnToLogin.setOnClickListener { findNavController().navigateUp() }
    }

    private fun register() = with(binding) {
        val checks = listOf(
            tilFullName.requireNotEmpty(),
            tilGroup.requireNotEmpty(),
            tilEmail.requireEmail(),
            tilPassword.requirePassword()
        )
        val passwordsMatch = tilPassword.text() == tilPasswordRepeat.text()
        tilPasswordRepeat.error = if (passwordsMatch) null else getString(R.string.error_password_mismatch)

        if (checks.all { it } && passwordsMatch) {
            UserSession.register(
                requireContext(),
                User(tilFullName.text(), tilGroup.text(), tilEmail.text())
            )
            (requireActivity() as AuthActivity).openMain()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
