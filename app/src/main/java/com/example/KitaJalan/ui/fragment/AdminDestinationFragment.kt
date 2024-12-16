package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.data.model.DestinasiPostRequest
import com.example.KitaJalan.data.network.RetrofitInstance
import com.example.KitaJalan.data.repository.DestinasiRepository
import com.example.KitaJalan.databinding.BottomAddDestinationSheetLayoutBinding
import com.example.KitaJalan.databinding.FragmentAdminDestinationBinding
import com.example.KitaJalan.ui.adapter.DestinasiAdminAdapter
import com.example.KitaJalan.ui.viewModel.DestinasiViewModel
import com.example.KitaJalan.utils.Resource
import com.example.KitaJalan.utils.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

class AdminDestinationFragment : Fragment() {

    private var _binding: FragmentAdminDestinationBinding? = null
    private val binding get() = _binding!!

    private lateinit var destinasiAdapter: DestinasiAdminAdapter

    private val destinasiViewModel: DestinasiViewModel by viewModels {
        ViewModelFactory(DestinasiViewModel::class.java) {
            val repository = DestinasiRepository(RetrofitInstance.getCrudApi())
            DestinasiViewModel(repository)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminDestinationBinding.inflate(inflater, container, false)
        setupFAB()
        setupRecyclerView()
        getDestination()

        return binding.root

    }
    private fun setupRecyclerView() {
        destinasiAdapter = DestinasiAdminAdapter(emptyList(), ::onEditClick, ::onDeleteClick)
        binding.recyclerWisata.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerWisata.adapter = destinasiAdapter
    }

    private fun setupFAB() {
        binding.fab.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding =
            BottomAddDestinationSheetLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.submitButton.setOnClickListener {
            val title = bottomSheetBinding.inputNamaDestinasi.text.toString().trim()
            val subtitle = bottomSheetBinding.inputKategori.text.toString().trim()
            val picAddress = bottomSheetBinding.inputFoto.text.toString().trim()
            val description = bottomSheetBinding.inputDeskripsi.text.toString().trim()
            val price = bottomSheetBinding.inputHarga.text.toString().trim()
            val location = bottomSheetBinding.inputLokasi.text.toString().trim()
            val facilitiesInput = bottomSheetBinding.inputFasilitas.text.toString().trim()

            val defaultImage = "https://images.unsplash.com/photo-1516117172878-fd2c41f4a759?w=1024"

            if (title.isEmpty() || subtitle.isEmpty() || description.isEmpty() || price.isEmpty() || location.isEmpty()) {
                Snackbar.make(
                    binding.root,
                    "Semua field harus diisi!",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val facilities = if (facilitiesInput.isNotEmpty()) {
                facilitiesInput.split(",").map { it.trim() }
            } else {
                emptyList<String>()
            }

            val finalPicAddress = if (picAddress.isEmpty()) defaultImage else picAddress

            val destinasiRequest = DestinasiPostRequest(
                namaDestinasi = title,
                fasilitas = facilities,
                foto = finalPicAddress,
                harga = price.toDoubleOrNull() ?:0.0,
                lokasi = location,
                kategori = subtitle,
                deskripsi = description
            )

            // createNewDestination(destinasiRequest)

            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun getDestination() {
        destinasiViewModel.getDestinasi(requireContext())
        destinasiViewModel.data.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Empty -> {
                    Log.d("Data Wisata", "Data Kosong. (${resource.message})")
                    binding.emptyWisata.root.visibility = View.VISIBLE
                    binding.loadingWisata.root.visibility = View.GONE
                    binding.errorWisata.root.visibility = View.GONE
                    binding.recyclerWisata.visibility = View.GONE

                    binding.emptyWisata.emptyMessage.text = resource.message
                }

                is Resource.Error -> {
                    Log.e("Data User", resource.message.toString())
                    binding.emptyWisata.root.visibility = View.GONE
                    binding.loadingWisata.root.visibility = View.GONE
                    binding.errorWisata.root.visibility = View.VISIBLE
                    binding.recyclerWisata.visibility = View.GONE

                    binding.errorWisata.errorMessage.text = resource.message

                    binding.errorWisata.retryButton.setOnClickListener {
                        destinasiViewModel.getDestinasi(requireContext(), true)
                    }
                }

                is Resource.Loading -> {
                    Log.d("Data User", "Mohon Tunggu...")
                    binding.emptyWisata.root.visibility = View.GONE
                    binding.loadingWisata.root.visibility = View.VISIBLE
                    binding.errorWisata.root.visibility = View.GONE
                    binding.recyclerWisata.visibility = View.GONE
                }

                is Resource.Success -> {
                    Log.d("Data User", "Data berhasil didapatkan")
                    binding.emptyWisata.root.visibility = View.GONE
                    binding.loadingWisata.root.visibility = View.GONE
                    binding.errorWisata.root.visibility = View.GONE
                    binding.recyclerWisata.visibility = View.VISIBLE

                    val trendsItem = resource.data!!.items
                    destinasiAdapter.updateData(trendsItem)
                }
            }
        }
    }

    private fun onEditClick(destinasi: DestinasiModel) {
    }

    private fun onDeleteClick(destinasi: DestinasiModel) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}