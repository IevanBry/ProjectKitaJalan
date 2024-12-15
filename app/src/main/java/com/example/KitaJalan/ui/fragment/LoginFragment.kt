package com.example.KitaJalan.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.KitaJalan.R
import com.example.KitaJalan.data.firebase.FirebaseAuthService
import com.example.KitaJalan.data.repository.FirebaseRepository
import com.example.KitaJalan.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.KitaJalan.utils.ViewModelFactory
import androidx.fragment.app.viewModels
import com.example.KitaJalan.ui.activity.AdminActivity
import com.example.KitaJalan.ui.activity.MainActivity
import com.example.KitaJalan.ui.viewModel.FirebaseViewModel
import com.example.KitaJalan.utils.Resource

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        if (isLoggedIn) {
            val userEmail = firebaseAuth.currentUser?.email
            if (userEmail == "admin@gmail.com") {
                // Redirect to AdminActivity if the email matches
                val intent = Intent(context, AdminActivity::class.java)
                startActivity(intent)
            } else {
                // Redirect to MainActivity if the email doesn't match
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
        }

        firebaseViewModel.loginState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    Log.d("Firebase User Authentication", "Mengirim Username Password...")
                }

                is Resource.Success -> {
                    val user = resource.data
                    Log.d("Firebase User Authentication", "Halo ${user?.email}")
                    val userEmail = user?.email
                    if (userEmail == "admin@gmail.com") {
                        val intent = Intent(context, AdminActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

                is Resource.Error -> {
                    Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }

        binding.btnSignUp.setOnClickListener {
            val registerFragment = RegisterFragment()
            replaceFragment(registerFragment)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    context,
                    "Username dan Password tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            firebaseViewModel.login(requireContext(), email, password)
        }

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}