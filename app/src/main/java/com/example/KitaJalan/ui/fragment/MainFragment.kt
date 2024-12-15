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
import com.example.KitaJalan.data.model.DestinasiPostRequest
import com.example.KitaJalan.data.network.RetrofitInstance
import com.example.KitaJalan.data.repository.DestinasiRepository
import com.example.KitaJalan.databinding.FragmentMainBinding
import com.example.KitaJalan.ui.adapter.AutoSliderAdapter
import com.example.KitaJalan.ui.adapter.CategoryAdapter
import com.example.KitaJalan.ui.adapter.CategoryItem
import com.example.KitaJalan.ui.adapter.TrendsAdapter
import com.example.KitaJalan.ui.viewmodel.DestinasiViewModel
import com.example.KitaJalan.utils.Resource
import com.example.KitaJalan.utils.ViewModelFactory

class MainFragment : Fragment() {

    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val destinasiViewModel: DestinasiViewModel by viewModels {
        ViewModelFactory(DestinasiViewModel::class.java) {
            val repository = DestinasiRepository(RetrofitInstance.getCrudApi())
            DestinasiViewModel(repository)
        }
    }

    private lateinit var trendsAdapter: TrendsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        categoryAdapter()
        setupTrendsRecyclerView()
//        addDestinasiData()
        setupAutoSlider()
        observeDestinasiData()

        return binding.root
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
                putString("namaDestinasi", selectedItem.namaDestinasi)
                putString("deskripsi", selectedItem.deskripsi)
                putString("lokasi", selectedItem.lokasi)
                putString("harga", "Rp. ${selectedItem.harga}")
                putString("foto", selectedItem.foto)
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
        val categoryList = listOf(
            CategoryItem("Wisata", R.drawable.cat1),
            CategoryItem("Event", R.drawable.cat2),
        )

        val categoryAdapter = CategoryAdapter(categoryList) { categoryItem ->
            Toast.makeText(requireContext(), "Clicked: ${categoryItem.title}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerGrid.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = categoryAdapter
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
                    val trendsItem = resource.data!!.items
                    trendsAdapter.updateData(trendsItem)
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
            DestinasiPostRequest(
                namaDestinasi = "Taman Mini Indonesia Indah",
                fasilitas = listOf("Kamar Mandi", "Tempat Parkir", "Kantin"),
                foto = "https://example.com/foto1.jpg",
                harga = 50000.0,
                lokasi = "Jakarta Timur, Indonesia",
                kategori = "Wisata Alam",
                deskripsi = "Taman Mini Indonesia Indah adalah taman wisata budaya dengan miniatur Indonesia."
            ),
            DestinasiPostRequest(
                namaDestinasi = "Ancol Dreamland",
                fasilitas = listOf("Kolam Renang", "Kamar Mandi", "Area Parkir"),
                foto = "https://example.com/foto2.jpg",
                harga = 150000.0,
                lokasi = "Jakarta Utara, Indonesia",
                kategori = "Wisata Hiburan",
                deskripsi = "Ancol Dreamland adalah kawasan wisata terbesar di Jakarta dengan berbagai wahana."
            ),
            DestinasiPostRequest(
                namaDestinasi = "Kawah Putih",
                fasilitas = listOf("Parkir", "Toilet", "Warung Makan"),
                foto = "https://example.com/foto3.jpg",
                harga = 20000.0,
                lokasi = "Bandung, Indonesia",
                kategori = "Wisata Alam",
                deskripsi = "Kawah Putih adalah danau kawah vulkanik yang indah dengan air berwarna putih kehijauan."
            )
        )
        destinasiViewModel.addDestinasi(requireContext(), destinasiList)
        destinasiViewModel.createStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
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
                else -> {}
            }
        }
    }

    @Override
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
}