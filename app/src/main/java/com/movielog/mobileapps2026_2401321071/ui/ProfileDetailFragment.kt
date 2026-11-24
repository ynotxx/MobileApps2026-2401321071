package com.movielog.mobileapps2026_2401321071.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.movielog.mobileapps2026_2401321071.data.model.UserProfile
import com.movielog.mobileapps2026_2401321071.data.repository.FirebaseAuthRepository
import com.movielog.mobileapps2026_2401321071.databinding.FragmentProfileDetailBinding
import kotlinx.coroutines.launch

class ProfileDetailFragment : Fragment() {

    private var _binding: FragmentProfileDetailBinding? = null
    private val binding get() = _binding!!

    private val authRepository = FirebaseAuthRepository()
    private var targetUserId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        targetUserId = arguments?.getString("userId") ?: authRepository.currentUserId ?: "user_12345"

        viewLifecycleOwner.lifecycleScope.launch {
            val result = authRepository.getProfileById(targetUserId)
            result.onSuccess { profile ->
                if (profile != null) {
                    binding.etUsername.setText(profile.username)
                    binding.etEmail.setText(profile.email)
                    binding.etBio.setText(profile.bio)
                } else {
                    binding.etUsername.setText("Scanned User")
                    binding.etEmail.setText("scanned@movielog.com")
                    binding.etBio.setText("Movie log enthusiast")
                }
            }.onFailure {
                binding.etUsername.setText("User Profile")
                binding.etEmail.setText("user@movielog.com")
                binding.etBio.setText("Movie log enthusiast")
            }
        }

        binding.btnSaveProfile.setOnClickListener {
            val username = binding.etUsername.text?.toString()?.trim() ?: ""
            val email = binding.etEmail.text?.toString()?.trim() ?: ""
            val bio = binding.etBio.text?.toString()?.trim() ?: ""

            val updatedProfile = UserProfile(
                userId = targetUserId,
                username = username,
                email = email,
                bio = bio
            )

            viewLifecycleOwner.lifecycleScope.launch {
                authRepository.updateProfile(updatedProfile)
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
