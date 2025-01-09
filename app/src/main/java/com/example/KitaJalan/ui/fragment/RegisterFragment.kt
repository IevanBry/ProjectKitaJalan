package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.KitaJalan.R
import com.example.KitaJalan.data.firebase.FirebaseAuthService
import com.example.KitaJalan.data.repository.FirebaseRepository
import com.example.KitaJalan.databinding.FragmentRegisterBinding
import com.example.KitaJalan.ui.viewModel.FirebaseViewModel
import com.example.KitaJalan.utils.Resource
import com.example.KitaJalan.utils.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val firebaseViewModel: FirebaseViewModel by viewModels {
        ViewModelFactory(FirebaseViewModel::class.java) {
            val repository = FirebaseRepository(FirebaseAuthService())
            FirebaseViewModel(repository)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        setupListeners()
        observeRegisterState()

        return binding.root
    }

    private fun setupListeners() {
        binding.btnSignIn.setOnClickListener {
            navigateToLogin()
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.passwordInput.text.toString()
            val confirmPassword = binding.confirmpasswordInput.text.toString()

            if (validateInputs(email, password, confirmPassword)) {
                registerUser(email, password)
            }
        }
    }

    private fun validateInputs(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        return when {
            email.isEmpty() -> {
                Toast.makeText(requireContext(), "Email harus diisi!", Toast.LENGTH_SHORT).show()
                false
            }
            !email.endsWith("@gmail.com") -> {
                Toast.makeText(requireContext(), "Email harus menggunakan domain @gmail.com!", Toast.LENGTH_SHORT).show()
                false
            }
            password.isEmpty() -> {
                Toast.makeText(requireContext(), "Password harus diisi!", Toast.LENGTH_SHORT).show()
                false
            }
            password.length < 6 -> {
                Toast.makeText(requireContext(), "Password harus terdiri dari minimal 6  karakter!", Toast.LENGTH_SHORT).show()
                false
            }
            confirmPassword.isEmpty() -> {
                Toast.makeText(requireContext(), "Konfirmasi Password harus diisi!", Toast.LENGTH_SHORT).show()
                false
            }
            password != confirmPassword -> {
                Toast.makeText(requireContext(), "Password tidak cocok!", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun registerUser(email: String, password: String) {
        firebaseViewModel.register(requireContext(), email, password)
    }

    private fun observeRegisterState() {
        firebaseViewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Sedang diproses!", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Terjadi Kesalahan", Toast.LENGTH_SHORT).show()
                }
                is Resource.Empty -> {
                }
            }
        }
    }

    private fun navigateToLogin() {
        FirebaseAuth.getInstance().signOut()
        val loginFragment = LoginFragment()
        replaceFragment(loginFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}