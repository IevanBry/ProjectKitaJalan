package com.example.KitaJalan.ui.fragment

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.data.network.RetrofitInstance
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
            val repository = DestinasiRepository(RetrofitInstance.getCrudApi())
            DestinasiViewModel(repository)
        }
    }

    private var allDestinasiList: List<DestinasiModel> = emptyList()

    private lateinit var destinasiAdapter: DestinasiAdapter

    val filterOptions = listOf(
        "Price: Low to High",
        "Top Rated",
        "Popular",
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupDestinasiRecyclerView()
        setupFilterChips()
        observeDestinasiData()
        setupSearchView()

        return binding.root
    }

    private fun setupSearchView() {
        val searchView = binding.searchView

        val searchPlate = searchView.findViewById<LinearLayout>(androidx.appcompat.R.id.search_plate)
        searchPlate?.setBackgroundColor(Color.TRANSPARENT)

        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)

        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.BLACK)
        searchEditText.background = null
        searchEditText.setHintTextColor(Color.BLACK)

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
    private fun setupFilterChips() {
        val chipGroup: ChipGroup = binding.chipGroup

        filterOptions.forEach { filterOption ->
            val chip = Chip(requireContext())
            chip.text = filterOption
            chip.isCheckable = true
            chip.isChecked = false

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    applyFilter(filterOption)
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun applyFilter(filterOption: String) {

    }

    private fun setupDestinasiRecyclerView() {
        destinasiAdapter = DestinasiAdapter(emptyList(), requireContext())
        binding.recyclerViewDestinasi.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = destinasiAdapter
        }
    }
    private fun searchDestinasi(query: String) {
        if (query.isEmpty()) {
            destinasiAdapter.updateData(allDestinasiList)
        } else {
            val filteredList = allDestinasiList.filter { destinasi ->
                destinasi.namaDestinasi.contains(query, ignoreCase = true)
            }
            destinasiAdapter.updateData(filteredList)
        }
    }

    private fun observeDestinasiData() {
        destinasiViewModel.getDestinasi(requireContext())
        destinasiViewModel.data.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    allDestinasiList = resource.data!!.items
                    destinasiAdapter.updateData(allDestinasiList)
                }
                is Resource.Loading -> {
                    // Handle loading state
                }
                is Resource.Error -> {
                    // Handle error state
                }
                is Resource.Empty -> {
                    // Handle empty state
                }
            }
        }
    }
}
