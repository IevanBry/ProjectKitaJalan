package com.example.KitaJalan.ui.fragment

import WishlistViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.databinding.FragmentFavoriteBinding
import com.example.KitaJalan.ui.adapter.FavoriteAdapter
import com.example.KitaJalan.utils.Resource
import com.example.KitaJalan.utils.ViewModelFactory
import com.example.KitaJalan.data.repository.WishlistRepository
import com.example.KitaJalan.data.repository.DestinasiRepository

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter

    private val wishlistViewModel: WishlistViewModel by viewModels {
        ViewModelFactory(WishlistViewModel::class.java) {
            WishlistViewModel(
                WishlistRepository(),
                DestinasiRepository()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeFilteredDestinations()

        wishlistViewModel.fetchWishlistAndMatchDestinations(requireContext())

        return binding.root
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter(requireContext(), emptyList()) { selectedDestination ->
            navigateToDetail(selectedDestination)
        }
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavorites.adapter = favoriteAdapter
    }

    private fun observeFilteredDestinations() {
        wishlistViewModel.filteredDestinations.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.emptyNewHoriList.root.visibility = View.GONE
                    binding.loadingNewHoriList.root.visibility = View.VISIBLE
                    binding.errorNewHorilist.root.visibility = View.GONE
                    binding.recyclerViewFavorites.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.emptyNewHoriList.root.visibility = View.GONE
                    binding.loadingNewHoriList.root.visibility = View.GONE
                    binding.errorNewHorilist.root.visibility = View.GONE
                    binding.recyclerViewFavorites.visibility = View.VISIBLE
                    favoriteAdapter.updateData(resource.data ?: emptyList())
                }

                is Resource.Error -> {
                    binding.emptyNewHoriList.root.visibility = View.GONE
                    binding.loadingNewHoriList.root.visibility = View.GONE
                    binding.errorNewHorilist.root.visibility = View.VISIBLE
                    binding.recyclerViewFavorites.visibility = View.GONE
                    binding.errorNewHorilist.errorMessage.text = resource.message
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Empty -> {
                    binding.emptyNewHoriList.root.visibility = View.VISIBLE
                    binding.loadingNewHoriList.root.visibility = View.GONE
                    binding.errorNewHorilist.root.visibility = View.GONE
                    binding.recyclerViewFavorites.visibility = View.GONE
                    binding.emptyNewHoriList.emptyMessage.text = resource.message
                }
            }
        }
    }

    private fun navigateToDetail(destination: DestinasiModel) {
        val detailFragment = DetailFragment()
        val bundle = Bundle().apply {
            putString("id", destination.id)
            putString("namaDestinasi", destination.namaDestinasi)
            putString("deskripsi", destination.deskripsi)
            putString("lokasi", destination.lokasi)
            putDouble("harga", destination.harga)
            putString("foto", destination.foto)
            putStringArrayList("fasilitas", ArrayList(destination.fasilitas))
        }
        detailFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }
}