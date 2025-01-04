package com.example.KitaJalan.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.KitaJalan.ui.fragment.AdminEventFragment
import com.example.KitaJalan.R
import com.example.KitaJalan.databinding.ActivityAdminBinding
import com.example.KitaJalan.ui.fragment.AdminDestinationFragment
import com.example.KitaJalan.ui.fragment.SettingsFragment
import com.google.firebase.auth.FirebaseAuth

class AdminActivity : AppCompatActivity() {

    private var _binding: ActivityAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email ?: ""

        if (email != "admin@gmail.com") {
            binding.bottomNavigationView.menu.findItem(R.id.event).isVisible = false
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(AdminDestinationFragment())
                R.id.event -> replaceFragment(AdminEventFragment())
                R.id.settings -> replaceFragment(SettingsFragment())
                else -> {
                }
            }
            true
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AdminDestinationFragment())
            .commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}