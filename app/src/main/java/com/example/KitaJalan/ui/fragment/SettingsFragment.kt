package com.example.KitaJalan.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.KitaJalan.R
import com.example.KitaJalan.data.firebase.FirebaseAuthService
import com.example.KitaJalan.data.repository.FirebaseRepository
import com.example.KitaJalan.databinding.FragmentSettingsBinding
import com.example.KitaJalan.databinding.ModalCreateUserBinding
import com.example.KitaJalan.ui.activity.AuthenticationActivity
import com.example.KitaJalan.ui.adapter.SettingsAdapter
import com.example.KitaJalan.ui.adapter.SettingItem
import com.example.KitaJalan.ui.viewModel.FirebaseViewModel
import com.example.KitaJalan.utils.Resource
import com.example.KitaJalan.utils.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val firebaseViewModel: FirebaseViewModel by viewModels {
        ViewModelFactory(FirebaseViewModel::class.java) {
            val repository = FirebaseRepository(FirebaseAuthService())
            FirebaseViewModel(repository)
        }
    }

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        displayUsername()
        setupSettingsList()

        return binding.root
    }

    private fun displayUsername() {
        val currentUser = firebaseAuth.currentUser
        val email = currentUser?.email
        binding.email.text = email

        if (!email.isNullOrEmpty()) {
            val username = email.substringBefore('@')
            val firstName = username.substringBefore(".")

            val capitalizedFirstName = firstName.replaceFirstChar { it.uppercase() }

            binding.textNameSettings.text = capitalizedFirstName
        }
    }

    private fun setupSettingsList() {
        val settingsItems = mutableListOf(
            SettingItem("About Us", R.drawable.baseline_info_24),
            SettingItem("Log Out", R.drawable.baseline_logout_24)
        )

        val currentUser = firebaseAuth.currentUser
        val email = currentUser?.email

        if (email == "admin@gmail.com") {
            settingsItems.add(SettingItem("Create User", R.drawable.baseline_person_24))
        }

        val adapter = SettingsAdapter(requireContext(), R.layout.item_setting, settingsItems)
        binding.settingsListView.adapter = adapter

        binding.settingsListView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> replaceFragment(AboutUsFragment())
                1 -> logout()
                2 -> {
                    if (email == "admin@gmail.com") {
                        showCreateUserDialog()
                    }
                }
            }
        }
    }

    private fun showCreateUserDialog() {
        val dialogBinding = ModalCreateUserBinding.inflate(LayoutInflater.from(requireContext()))

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Create User")
            .setView(dialogBinding.root)
            .setPositiveButton("Create") { _, _ ->
                val email = dialogBinding.inputEmail.text.toString()
                val password = dialogBinding.inputPassword.text.toString()
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                } else if (!email.endsWith("@admin.com")) {
                    Toast.makeText(requireContext(), "Email must end with @admin.com", Toast.LENGTH_SHORT).show()
                } else {
                    firebaseViewModel.register(requireContext(), email, password)
                    observeRegisterState()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        dialogBuilder.create().show()
    }

    private fun observeRegisterState() {
        firebaseViewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Registering user...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "User registered successfully", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                }

                is Resource.Empty -> {}
            }
        }
    }

    private fun logout() {
        firebaseAuth.signOut()
        Toast.makeText(requireContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireContext(), AuthenticationActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}