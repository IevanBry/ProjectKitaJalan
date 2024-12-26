package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.KitaJalan.R
import com.example.KitaJalan.data.repository.DestinasiRepository
import com.example.KitaJalan.databinding.FragmentDetailBinding
import com.example.KitaJalan.ui.viewModel.DestinasiViewModel
import com.example.KitaJalan.utils.Resource
import com.example.KitaJalan.utils.ViewModelFactory
import com.squareup.picasso.Picasso
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    private val destinasiViewModel: DestinasiViewModel by viewModels {
        ViewModelFactory(DestinasiViewModel::class.java) {
            val repository = DestinasiRepository()
            DestinasiViewModel(repository)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        val args = arguments

        Picasso.get()
            .load(args?.getString("foto"))
            .error(R.drawable.bali)
            .into(binding.detailImg)

        setupTabsAndViewPager()
        setupWishlistButton()
        observeWishlistStatus()

        return binding.root
    }

    private fun setupTabsAndViewPager() {
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = object : androidx.viewpager2.adapter.FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment {
                val bundle = arguments
                return when (position) {
                    0 -> FragmentDetailView().apply { arguments = bundle }
                    1 -> MapFragment().apply { arguments = bundle }
                    2 -> CommentFragment().apply { arguments = bundle }
                    else -> throw IllegalStateException("Unexpected position: $position")
                }
            }
        }

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Detail"
                1 -> tab.text = "Map"
                2 -> tab.text = "Komentar"
            }
        }.attach()
    }
    private fun setupWishlistButton() {
        val args = arguments
        val destinasiId = args?.getString("id") ?: ""
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "You must log in to add to wishlist", Toast.LENGTH_SHORT).show()
            return
        }

        destinasiViewModel.getWishlist(requireContext(), userId)
        destinasiViewModel.wishlistData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val wishlist = resource.data ?: emptyList()
                    if (wishlist.contains(destinasiId)) {
                        binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
                        binding.favoriteButton.isEnabled = false
                    } else {
                        binding.favoriteButton.setImageResource(R.drawable.ic_favorite_shadow_24dp)
                        binding.favoriteButton.isEnabled = true
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Failed to load wishlist: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun observeWishlistStatus() {
        destinasiViewModel.wishlistStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Empty ->{
                }
                is Resource.Error ->{
                }
                is Resource.Loading ->{
                }
                is Resource.Success ->{
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}