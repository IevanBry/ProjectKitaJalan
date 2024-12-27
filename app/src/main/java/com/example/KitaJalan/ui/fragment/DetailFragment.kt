package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.KitaJalan.R
import com.example.KitaJalan.data.repository.DestinasiRepository
import com.example.KitaJalan.databinding.FragmentDetailBinding
import com.example.KitaJalan.ui.viewModel.DestinasiViewModel
import com.example.KitaJalan.utils.Resource
import com.example.KitaJalan.utils.ViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

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

        binding.iconBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return binding.root
    }

    private fun setupTabsAndViewPager() {
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val fragments = listOf(
            FragmentDetailView().apply { arguments = this@DetailFragment.arguments },
            MapFragment().apply { arguments = this@DetailFragment.arguments },
            CommentFragment().apply { arguments = this@DetailFragment.arguments }
        )

        val adapter = object : androidx.viewpager2.adapter.FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Detail"
                1 -> "Map"
                2 -> "Komentar"
                else -> throw IllegalStateException("Unexpected position: $position")
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
                        binding.favoriteButton.setOnClickListener {
                            Log.d("Remove", "Remove Wishlist")
                            destinasiViewModel.removeFromWishlist(requireContext(), userId, destinasiId)
                        }
                    } else {
                        binding.favoriteButton.setImageResource(R.drawable.ic_favorite_shadow_24dp)
                        binding.favoriteButton.isEnabled = true

                        binding.favoriteButton.setOnClickListener {
                            Log.d("Add", "Add wishlist")
                            destinasiViewModel.addToWishlist(requireContext(), userId, destinasiId)
                        }
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
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Memproses wishlist...", Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    if (resource.data != null) {
                        binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
                        Toast.makeText(requireContext(), "Berhasil menambahkan wishlist", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.favoriteButton.setImageResource(R.drawable.ic_favorite_shadow_24dp)
                        Toast.makeText(requireContext(), "Berhasil menghapus wishlist", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Failed to update wishlist: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
}