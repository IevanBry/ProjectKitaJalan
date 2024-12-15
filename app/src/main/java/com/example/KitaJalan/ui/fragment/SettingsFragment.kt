package com.example.KitaJalan.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.KitaJalan.R
import com.example.KitaJalan.databinding.FragmentSettingsBinding
import com.example.KitaJalan.ui.activity.AuthenticationActivity
import com.example.KitaJalan.ui.adapter.SettingsAdapter
import com.example.KitaJalan.ui.adapter.SettingItem
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        val settingsItems = listOf(
            SettingItem("About Us", R.drawable.baseline_info_24),
            SettingItem("Log Out", R.drawable.baseline_logout_24)
        )

        val adapter = SettingsAdapter(requireContext(), R.layout.item_setting, settingsItems)
        binding.settingsListView.adapter = adapter

        binding.settingsListView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    replaceFragment(AboutUsFragment())
                }
                1 -> {
                    logout()
                }
            }
        }
        return binding.root
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
