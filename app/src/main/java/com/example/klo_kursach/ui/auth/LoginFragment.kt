package com.example.klo_kursach.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.klo_kursach.R
import com.example.klo_kursach.data.UserSession
import com.example.klo_kursach.databinding.FragmentLoginBinding

/** Экран входа. */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLogin.setOnClickListener {
            val emailOk = binding.tilEmail.requireEmail()
            val passwordOk = binding.tilPassword.requirePassword()
            if (emailOk && passwordOk) {
                UserSession.login(requireContext(), binding.tilEmail.text())
                (requireActivity() as AuthActivity).openMain()
            }
        }
        binding.btnToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
        binding.btnOffline.setOnClickListener {
            UserSession.continueAsGuest(requireContext())
            (requireActivity() as AuthActivity).openMain()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
