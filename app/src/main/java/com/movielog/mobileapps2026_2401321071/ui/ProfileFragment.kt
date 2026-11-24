package com.movielog.mobileapps2026_2401321071.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.movielog.mobileapps2026_2401321071.R
import com.movielog.mobileapps2026_2401321071.data.model.UserProfile
import com.movielog.mobileapps2026_2401321071.data.repository.FirebaseAuthRepository
import com.movielog.mobileapps2026_2401321071.databinding.FragmentProfileBinding
import com.movielog.mobileapps2026_2401321071.util.QrCodeUtils
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authRepository = FirebaseAuthRepository()
    private var currentProfile: UserProfile? = null

    private val qrScanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val scannedUserId = result.contents
            val bundle = bundleOf("userId" to scannedUserId)
            findNavController().navigate(R.id.action_profileFragment_to_profileDetailFragment, bundle)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfile()

        binding.btnScanQr.setOnClickListener {
            val options = ScanOptions()
                .setPrompt("Scan user profile QR code")
                .setBeepEnabled(true)
                .setOrientationLocked(false)
            qrScanLauncher.launch(options)
        }

        binding.btnEditProfile.setOnClickListener {
            val uid = currentProfile?.userId ?: authRepository.currentUserId ?: "default_user"
            val bundle = bundleOf("userId" to uid)
            findNavController().navigate(R.id.action_profileFragment_to_profileDetailFragment, bundle)
        }
    }

    private fun loadProfile() {
        val uid = authRepository.currentUserId ?: "user_12345"
        val bitmap = QrCodeUtils.generateQrCodeBitmap(uid)
        binding.ivQrCode.setImageBitmap(bitmap)

        viewLifecycleOwner.lifecycleScope.launch {
            val result = authRepository.getCurrentProfile()
            result.onSuccess { profile ->
                if (profile != null) {
                    currentProfile = profile
                    binding.tvUsername.text = profile.username
                    binding.tvEmail.text = profile.email
                    binding.tvBio.text = profile.bio
                } else {
                    val defaultProfile = UserProfile(
                        userId = uid,
                        username = "MovieFan",
                        email = "moviefan@movielog.com",
                        bio = "Movie log enthusiast"
                    )
                    currentProfile = defaultProfile
                    binding.tvUsername.text = defaultProfile.username
                    binding.tvEmail.text = defaultProfile.email
                    binding.tvBio.text = defaultProfile.bio
                }
            }.onFailure {
                binding.tvUsername.text = "MovieFan"
                binding.tvEmail.text = "moviefan@movielog.com"
                binding.tvBio.text = "Movie log enthusiast"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
