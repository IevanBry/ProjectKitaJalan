package com.example.KitaJalan.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.KitaJalan.R
import com.example.KitaJalan.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

//        binding.btnRegister.setOnClickListener {
//            val username = binding.usernameInput.text.toString()
//            val email = binding.email.text.toString()
//            val password = binding.passwordInput.text.toString()
//            val confirmPassword = binding.confirmpasswordInput.text.toString()
//
//            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
//                if (password == confirmPassword) {
//                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
//                        if(it.isSuccessful){
//                            val uid = firebaseAuth.currentUser?.uid
//                            val database = FirebaseDatabase.getInstance().getReference("Users")
//
//                            val user = Users(
//                                id = 0,
//                                username = username,
//                                email = email,
//                                password = password,
//                            )
//
//                            uid?.let {
//                                database.child(it).setValue(user).addOnCompleteListener { dbTask ->
//                                    if (dbTask.isSuccessful) {
//                                        Toast.makeText(context, "Data berhasil disimpan ke database!", Toast.LENGTH_SHORT).show()
//                                        Log.d("Firebase", "Data disimpan: $user")
//                                        replaceFragment(LoginFragment())
//                                    } else {
//                                        Toast.makeText(context, "Gagal menyimpan data: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
//                                        Log.d("Firebase", "Gagal menyimpan data: ${dbTask.exception?.message}")
//                                    }
//                                }
//                            }
//                            replaceFragment(LoginFragment())
//                        } else {
//                            Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                } else {
//                    Toast.makeText(context, "Password tidak sama", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(context, "Tolons isi semua kolom!", Toast.LENGTH_SHORT).show()
//            }
//        }

        binding.btnSignIn.setOnClickListener {
            val loginFragment = LoginFragment()
            replaceFragment(loginFragment)
        }

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}