package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.data.repository.DestinasiRepository
import com.example.KitaJalan.databinding.FragmentSearchBinding
import com.example.KitaJalan.ui.adapter.DestinasiAdapter
import com.example.KitaJalan.ui.viewModel.DestinasiViewModel
import com.example.KitaJalan.utils.Resource
import com.example.KitaJalan.utils.ViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val destinasiViewModel: DestinasiViewModel by viewModels {
        ViewModelFactory(DestinasiViewModel::class.java) {
            val repository = DestinasiRepository()
            DestinasiViewModel(repository)
        }
    }

    private var allDestinasiList: List<DestinasiModel> = emptyList()
    private lateinit var destinasiAdapter: DestinasiAdapter

    private val filterOptions = listOf(
        "Price: Low to High",
        "Price: High to Low",
        "Top Rated",
        "Popular"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupDestinasiRecyclerView()
        setupFilterChips()
        observeDestinasiData()
        setupSearchView()

        return binding.root
    }

    private fun setupSearchView() {
        val searchView = binding.searchView

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchDestinasi(it) }
                return true
            }
        })

        val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)

        editText.setTextColor(android.graphics.Color.BLACK)

        editText.setHintTextColor(android.graphics.Color.BLACK)

        val searchPlate = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        searchPlate.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(resources.getColor(R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
    }

    private fun setupFilterChips() {
        val chipGroup: ChipGroup = binding.chipGroup

        filterOptions.forEach { filterOption ->
            val chip = Chip(requireContext()).apply {
                text = filterOption
                isCheckable = true
                isChecked = false
                setChipBackgroundColorResource(R.color.purple)
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    chip.setChipBackgroundColorResource(R.color.purple_chip_selected)
                    applyFilter(filterOption)
                } else {
                    chip.setChipBackgroundColorResource(R.color.purple)
                }
            }

            chipGroup.addView(chip)
        }

        chipGroup.setOnCheckedStateChangeListener { _, _ ->
            destinasiAdapter.updateData(allDestinasiList)
        }
    }

    private fun applyFilter(filterOption: String) {
        val sortedList = when (filterOption) {
            "Price: Low to High" -> allDestinasiList.sortedBy { it.harga }
            "Price: High to Low" -> allDestinasiList.sortedByDescending { it.harga }
            "Top Rated" -> allDestinasiList.sortedByDescending { it.averageRating }
            "Popular" -> allDestinasiList.sortedByDescending { it.totalComments }
            else -> allDestinasiList
        }

        destinasiAdapter.updateData(sortedList)
        binding.recyclerViewDestinasi.scrollToPosition(0)
    }

    private fun setupDestinasiRecyclerView() {
        destinasiAdapter = DestinasiAdapter(
            items = emptyList(),
            context = requireContext(),
        ) { selectedItem ->
            navigateToDetail(selectedItem)
        }
        binding.recyclerViewDestinasi.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = destinasiAdapter
        }
    }

    private fun searchDestinasi(query: String) {
        val filteredList = if (query.isEmpty()) {
            allDestinasiList
        } else {
            allDestinasiList.filter { destinasi ->
                destinasi.namaDestinasi.contains(query, ignoreCase = true)
            }
        }

        if (filteredList.isEmpty()) {
            binding.emptySearch.root.visibility = View.VISIBLE
            binding.emptySearch.emptyMessage.text = "Tidak ada destinasi yang sesuai dengan pencarian \"$query\""
            binding.recyclerViewDestinasi.visibility = View.GONE
        } else {
            binding.emptySearch.root.visibility = View.GONE
            binding.recyclerViewDestinasi.visibility = View.VISIBLE
            destinasiAdapter.updateData(filteredList)
        }
    }

    private fun observeDestinasiData() {
        destinasiViewModel.getDestinasi(requireContext())
        destinasiViewModel.data.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Empty -> {
                    binding.emptySearch.root.visibility = View.VISIBLE
                    binding.loadingSearch.root.visibility = View.GONE
                    binding.errorSearch.root.visibility = View.GONE
                    binding.recyclerViewDestinasi.visibility = View.GONE
                    binding.emptySearch.emptyMessage.text = resource.message
                }
                is Resource.Loading -> {
                    binding.emptySearch.root.visibility = View.GONE
                    binding.loadingSearch.root.visibility = View.VISIBLE
                    binding.errorSearch.root.visibility = View.GONE
                    binding.recyclerViewDestinasi.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.emptySearch.root.visibility = View.GONE
                    binding.loadingSearch.root.visibility = View.GONE
                    binding.errorSearch.root.visibility = View.GONE
                    binding.recyclerViewDestinasi.visibility = View.VISIBLE

                    resource.data?.let {
                        allDestinasiList = it
                        destinasiAdapter.updateData(allDestinasiList)
                    }
                }
                is Resource.Error -> {
                    binding.emptySearch.root.visibility = View.GONE
                    binding.loadingSearch.root.visibility = View.GONE
                    binding.errorSearch.root.visibility = View.VISIBLE
                    binding.recyclerViewDestinasi.visibility = View.GONE
                    binding.errorSearch.errorMessage.text = resource.message
                    binding.errorSearch.retryButton.setOnClickListener {
                        destinasiViewModel.getDestinasi(requireContext(), forceRefresh = true)
                    }
                }
            }
        }
    }

    private fun navigateToDetail(destinasi: DestinasiModel) {
        val detailFragment = DetailFragment()
        val bundle = Bundle().apply {
            putString("id", destinasi.id)
            putString("namaDestinasi", destinasi.namaDestinasi)
            putString("deskripsi", destinasi.deskripsi)
            putString("lokasi", destinasi.lokasi)
            putDouble("harga", destinasi.harga)
            putString("foto", destinasi.foto)
            putStringArrayList("fasilitas", ArrayList(destinasi.fasilitas))
            putString("noHp", destinasi.noHp)
            putDouble("averageRating", destinasi.averageRating)
        }
        detailFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}