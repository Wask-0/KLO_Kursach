package com.example.klo_kursach.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.klo_kursach.R
import com.example.klo_kursach.data.NotesRepository
import com.example.klo_kursach.data.UserSession
import com.example.klo_kursach.databinding.FragmentProfileBinding
import com.example.klo_kursach.ui.auth.AuthActivity
import com.google.android.material.snackbar.Snackbar

/** Профиль: данные пользователя, статистика, переход к настройкам и выход. */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        val context = requireContext()
        val user = if (UserSession.isGuest(context)) null else UserSession.currentUser(context)

        tvFullName.text = user?.fullName?.ifBlank { null } ?: getString(R.string.guest_name)
        tvAvatar.text = user?.initials?.ifBlank { null } ?: getString(R.string.guest_name).take(1)
        tvGroup.text = getString(
            R.string.profile_group,
            user?.group?.ifBlank { null } ?: getString(R.string.guest_group)
        )
        tvEmail.text = user?.email
        tvEmail.isVisible = user != null

        tvStatNotes.text = NotesRepository.getNotes().size.toString()
        tvStatSubjects.text = NotesRepository.getSubjects().size.toString()
        tvStatTags.text = NotesRepository.allTags().size.toString()

        btnEditProfile.setOnClickListener {
            Snackbar.make(root, R.string.feature_in_progress, Snackbar.LENGTH_SHORT).show()
        }
        btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_settings)
        }
        btnLogout.setOnClickListener {
            UserSession.logout(context)
            startActivity(
                Intent(context, AuthActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
