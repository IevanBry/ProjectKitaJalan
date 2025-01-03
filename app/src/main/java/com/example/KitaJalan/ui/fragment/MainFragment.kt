package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.data.repository.DestinasiRepository
import com.example.KitaJalan.databinding.FragmentMainBinding
import com.example.KitaJalan.ui.adapter.AutoSliderAdapter
import com.example.KitaJalan.ui.adapter.CategoryAdapter
import com.example.KitaJalan.ui.adapter.CategoryItem
import com.example.KitaJalan.ui.adapter.TrendsAdapter
import com.example.KitaJalan.ui.viewModel.DestinasiViewModel
import com.example.KitaJalan.utils.Resource
import com.example.KitaJalan.utils.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MainFragment : Fragment() {

    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val destinasiViewModel: DestinasiViewModel by viewModels {
        ViewModelFactory(DestinasiViewModel::class.java) {
            val repository = DestinasiRepository()
            DestinasiViewModel(repository)
        }
    }

    private lateinit var trendsAdapter: TrendsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        displayUsername()
        categoryAdapter()
        setupTrendsRecyclerView()
        setupAutoSlider()
//        addDestinasiData()
        observeDestinasiData()

        return binding.root
    }

    private fun displayUsername(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val email = currentUser?.email

        if (!email.isNullOrEmpty()) {
            val username = email.substringBefore('@')
            val firstName = username.substringBefore(".")

            val capitalizedFirstName = firstName.replaceFirstChar { it.uppercase() }

            binding.username.text = capitalizedFirstName
        }
    }

    private fun setupAutoSlider() {
        val images = listOf(
            R.drawable.image_slider3,
            R.drawable.image_slider2,
            R.drawable.image_slider
        )
        binding.autoSlider.adapter = AutoSliderAdapter(images, binding.autoSlider)
        binding.wornIndicator.attachTo(binding.autoSlider)
    }

    private fun setupTrendsRecyclerView() {
        trendsAdapter = TrendsAdapter(emptyList(), requireContext()) { selectedItem ->
            val detailFragment = DetailFragment()
            val bundle = Bundle().apply {
                putString("id", selectedItem.id)
                putString("namaDestinasi", selectedItem.namaDestinasi)
                putString("deskripsi", selectedItem.deskripsi)
                putString("lokasi", selectedItem.lokasi)
                putDouble("harga", selectedItem.harga)
                putString("foto", selectedItem.foto)
                putStringArrayList("fasilitas", ArrayList(selectedItem.fasilitas))
            }
            detailFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.destinasiRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = trendsAdapter
        }
    }

    private fun categoryAdapter() {
        destinasiViewModel.data.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val destinations = resource.data ?: emptyList()
                    val uniqueCategories = destinations.map { it.kategori }.distinct()

                    val categoryList = uniqueCategories.map { category ->
                        val iconResId = when (category) {
                            "Wisata Alam" -> R.drawable.cat1
                            "Wisata Hiburan" -> R.drawable.cat2
                            else -> R.drawable.logo
                        }
                        CategoryItem(category, iconResId)
                    }

                    val categoryAdapter = CategoryAdapter(categoryList) { categoryItem ->
                        val categoryFragment = CategoryFragment()
                        val bundle = Bundle().apply {
                            putString("category", categoryItem.title)
                        }
                        categoryFragment.arguments = bundle

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, categoryFragment)
                            .addToBackStack(null)
                            .commit()
                    }

                    binding.recyclerGrid.apply {
                        layoutManager = GridLayoutManager(requireContext(), 2)
                        adapter = categoryAdapter
                    }
                }
                is Resource.Empty -> {
                    Toast.makeText(requireContext(), "No destinations found", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Failed to load categories", Toast.LENGTH_SHORT).show()
                }
                else -> {
                }
            }
        }
    }

    private fun observeDestinasiData() {
        destinasiViewModel.getDestinasi(requireContext())
        destinasiViewModel.data.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Empty -> {
                    binding.emptyNewHoriList.root.visibility = View.VISIBLE
                    binding.loadingNewHoriList.root.visibility = View.GONE
                    binding.errorNewHorilist.root.visibility = View.GONE
                    binding.destinasiRecycler.visibility = View.GONE
                    binding.emptyNewHoriList.emptyMessage.text = resource.message
                    Log.d("Data Destinasi", "Data Kosong. (${resource.message})")
                }
                is Resource.Loading -> {
                    binding.emptyNewHoriList.root.visibility = View.GONE
                    binding.loadingNewHoriList.root.visibility = View.VISIBLE
                    binding.errorNewHorilist.root.visibility = View.GONE
                    binding.destinasiRecycler.visibility = View.GONE
                    Log.d("Data Destinasi", "Mohon Tunggu..")
                }
                is Resource.Success -> {
                    binding.emptyNewHoriList.root.visibility = View.GONE
                    binding.loadingNewHoriList.root.visibility = View.GONE
                    binding.errorNewHorilist.root.visibility = View.GONE
                    binding.destinasiRecycler.visibility = View.VISIBLE
                    Log.d("Data Destinasi", "Data berhasil didapatkan")
                    resource.data?.let {
                        trendsAdapter.updateData(it)
                    }
                }
                is Resource.Error -> {
                    binding.emptyNewHoriList.root.visibility = View.GONE
                    binding.loadingNewHoriList.root.visibility = View.GONE
                    binding.errorNewHorilist.root.visibility = View.VISIBLE
                    binding.destinasiRecycler.visibility = View.GONE
                    binding.errorNewHorilist.errorMessage.text = resource.message
                    binding.errorNewHorilist.retryButton.setOnClickListener {
                        destinasiViewModel.getDestinasi(requireContext(), true)
                    }
                    Log.d("Data Destinasi", resource.message.toString())
                }
            }
        }
    }

    private fun addDestinasiData() {
        val destinasiList = listOf(
            DestinasiModel(
                namaDestinasi = "Taman Mini Indonesia Indah",
                fasilitas = listOf("Kamar Mandi", "Tempat Parkir", "Kantin"),
                foto = "https://example.com/foto1.jpg",
                harga = 50000.0,
                lokasi = "Jakarta Timur, Indonesia",
                kategori = "Wisata Alam",
                deskripsi = "Taman Mini Indonesia Indah adalah taman wisata budaya dengan miniatur Indonesia.",
            ),
            DestinasiModel(
                namaDestinasi = "Ancol Dreamland",
                fasilitas = listOf("Kolam Renang", "Kamar Mandi", "Area Parkir"),
                foto = "https://example.com/foto2.jpg",
                harga = 150000.0,
                lokasi = "Jakarta Utara, Indonesia",
                kategori = "Wisata Hiburan",
                deskripsi = "Ancol Dreamland adalah kawasan wisata terbesar di Jakarta dengan berbagai wahana.",
            ),
            DestinasiModel(
                namaDestinasi = "Kawah Putih",
                fasilitas = listOf("Parkir", "Toilet", "Warung Makan"),
                foto = "https://example.com/foto3.jpg",
                harga = 20000.0,
                lokasi = "Bandung, Indonesia",
                kategori = "Wisata Alam",
                deskripsi = "Kawah Putih adalah danau kawah vulkanik yang indah dengan air berwarna putih kehijauan.",
            )

        )
//        destinasiViewModel.addDestinasi(requireContext(), destinasiList)
    }


    @Override
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
}