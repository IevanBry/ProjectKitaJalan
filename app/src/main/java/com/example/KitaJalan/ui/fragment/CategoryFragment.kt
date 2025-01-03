package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.KitaJalan.R
import com.example.KitaJalan.data.repository.DestinasiRepository
import com.example.KitaJalan.databinding.FragmentCategoryBinding
import com.example.KitaJalan.ui.adapter.DestinasiAdapter
import com.example.KitaJalan.ui.viewModel.DestinasiViewModel
import com.example.KitaJalan.utils.Resource
import com.example.KitaJalan.utils.ViewModelFactory

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val destinasiViewModel: DestinasiViewModel by viewModels {
        ViewModelFactory(DestinasiViewModel::class.java) {
            val repository = DestinasiRepository()
            DestinasiViewModel(repository)
        }
    }

    private lateinit var destinasiAdapter: DestinasiAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupSearchView()
        observeCategoryData()
        return binding.root
    }

    private fun setupRecyclerView() {
        destinasiAdapter = DestinasiAdapter(
            items = emptyList(),
            context = requireContext(),
            onItemClick = { destinasi ->
                val detailFragment = DetailFragment()
                val bundle = Bundle().apply {
                    putString("id", destinasi.id)
                    putString("namaDestinasi", destinasi.namaDestinasi)
                    putString("deskripsi", destinasi.deskripsi)
                    putString("lokasi", destinasi.lokasi)
                    putDouble("harga", destinasi.harga)
                    putString("foto", destinasi.foto)
                    putStringArrayList("fasilitas", ArrayList(destinasi.fasilitas))
                    putDouble("averageRating", destinasi.averageRating)
                }
                detailFragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        )

        binding.recyclerViewDestinasi.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = destinasiAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchDestinasi(it) }
                return true
            }
        })
    }

    private fun searchDestinasi(query: String) {
        val currentData = destinasiViewModel.data.value
        val category = arguments?.getString("category")

        if (currentData is Resource.Success && !query.isBlank()) {
            val filteredList = currentData.data?.filter { destinasi ->
                destinasi.namaDestinasi.contains(query, ignoreCase = true) &&
                        destinasi.kategori == category
            } ?: emptyList()

            destinasiAdapter.updateData(filteredList)

            if (filteredList.isEmpty()) {
                binding.emptyKategori.root.visibility = View.VISIBLE
                binding.emptyKategori.emptyMessage.text =
                    "Tidak ada destinasi yang sesuai dengan pencarian \"$query\""
                binding.recyclerViewDestinasi.visibility = View.GONE
            } else {
                binding.emptyKategori.root.visibility = View.GONE
                binding.recyclerViewDestinasi.visibility = View.VISIBLE
            }
        } else if (query.isBlank() && category != null) {
            val categoryFilteredData = currentData?.data?.filter { it.kategori == category } ?: emptyList()

            destinasiAdapter.updateData(categoryFilteredData)

            if (categoryFilteredData.isEmpty()) {
                binding.emptyKategori.root.visibility = View.VISIBLE
                binding.emptyKategori.emptyMessage.text =
                    "Tidak ada destinasi untuk kategori $category"
                binding.recyclerViewDestinasi.visibility = View.GONE
            } else {
                binding.emptyKategori.root.visibility = View.GONE
                binding.recyclerViewDestinasi.visibility = View.VISIBLE
            }
        } else {
            Toast.makeText(requireContext(), "Data destinasi belum tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeCategoryData() {
        val category = arguments?.getString("category")
        if (category.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Kategori tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        binding.categoryTitle.text = "Kategori: $category"

        destinasiViewModel.data.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val filteredData = resource.data?.filter { it.kategori == category }
                    if (!filteredData.isNullOrEmpty()) {
                        binding.recyclerViewDestinasi.visibility = View.VISIBLE
                        binding.loadingKategori.root.visibility = View.GONE
                        binding.emptyKategori.root.visibility = View.GONE
                        destinasiAdapter.updateData(filteredData)
                    } else {
                        binding.recyclerViewDestinasi.visibility = View.GONE
                        binding.loadingKategori.root.visibility = View.GONE
                        binding.emptyKategori.root.visibility = View.VISIBLE
                        binding.emptyKategori.emptyMessage.text =
                            "Tidak ada destinasi untuk kategori $category"
                    }
                }
                is Resource.Loading -> {
                    binding.loadingKategori.root.visibility = View.VISIBLE
                    binding.recyclerViewDestinasi.visibility = View.GONE
                    binding.emptyKategori.root.visibility = View.GONE
                    binding.errorKategori.root.visibility = View.GONE
                }
                is Resource.Error -> {
                    binding.loadingKategori.root.visibility = View.GONE
                    binding.errorKategori.root.visibility = View.VISIBLE
                    binding.errorKategori.errorMessage.text = resource.message ?: "Terjadi kesalahan"
                    binding.recyclerViewDestinasi.visibility = View.GONE
                    binding.errorKategori.retryButton.setOnClickListener {
                        destinasiViewModel.getDestinasi(requireContext())
                    }
                }
                is Resource.Empty -> {
                    binding.emptyKategori.root.visibility = View.VISIBLE
                    binding.loadingKategori.root.visibility = View.GONE
                    binding.errorKategori.root.visibility = View.GONE
                    binding.recyclerViewDestinasi.visibility = View.GONE
                    binding.emptyKategori.emptyMessage.text = "Tidak ada destinasi ditemukan"
                }
            }
        }

        destinasiViewModel.getDestinasi(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}