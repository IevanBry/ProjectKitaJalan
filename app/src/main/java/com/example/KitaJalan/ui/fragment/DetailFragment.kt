package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.KitaJalan.R
import com.example.KitaJalan.databinding.FragmentDetailBinding
import com.squareup.picasso.Picasso
import androidx.viewpager2.widget.ViewPager2
import com.example.KitaJalan.data.model.WishlistPostRequest
import com.example.KitaJalan.data.network.RetrofitInstance
import com.example.KitaJalan.data.repository.DestinasiRepository
import com.example.KitaJalan.data.repository.WishlistRepository
import com.example.KitaJalan.ui.viewModel.WishlistViewModel
import com.example.KitaJalan.utils.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    private val wishlistViewModel: WishlistViewModel by viewModels {
        ViewModelFactory(WishlistViewModel::class.java) {
            WishlistViewModel(
                WishlistRepository(RetrofitInstance.getCrudApi()),
                DestinasiRepository(RetrofitInstance.getCrudApi())
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        val args = arguments
        val uuid = args?.getString("_uuid")

        Picasso.get()
            .load(args?.getString("foto"))
            .error(R.drawable.bali)
            .into(binding.detailImg)

        setupTabsAndViewPager()

        if (uuid != null) {
            checkFavoriteStatus(uuid)
        }
        binding.favoriteButton.setOnClickListener {
            if (uuid != null) {
                addToWishlist(uuid)
            } else {
                Toast.makeText(requireContext(), "Destinasi ID tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

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

    private fun checkFavoriteStatus(uuid: String) {
        wishlistViewModel.isFavorited(uuid,
            onResult = { isFavorited ->
                if (isFavorited) {
                    binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
                } else {
                    binding.favoriteButton.setImageResource(R.drawable.ic_favorite_shadow_24dp)
                }
            },
            onError = { error ->
                Toast.makeText(requireContext(), "Gagal memeriksa status favorit: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun addToWishlist(uuid: String) {
        wishlistViewModel.isFavorited(uuid,
            onResult = { isFavorited ->
                if (isFavorited) {
                    Toast.makeText(requireContext(), "Destinasi sudah ada di wishlist.", Toast.LENGTH_SHORT).show()
                } else {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                    val wishlistRequest = WishlistPostRequest(
                        idDestinasi = uuid,
                        tanggalDitambahkan = currentDate,
                        userId = userId,
                    )
                    wishlistViewModel.addToWishlist(requireContext(), listOf(wishlistRequest),
                        onSuccess = {
                            Toast.makeText(requireContext(), "Berhasil menambahkan ke wishlist!", Toast.LENGTH_SHORT).show()
                            binding.favoriteButton.setImageResource(R.drawable.baseline_favorite_24)
                        },
                        onError = { error ->
                            Toast.makeText(requireContext(), "Gagal menambahkan ke wishlist: $error", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            onError = { error ->
                Toast.makeText(requireContext(), "Gagal memeriksa status favorit: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }
}