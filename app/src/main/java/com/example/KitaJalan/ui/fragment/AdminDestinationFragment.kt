package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.KitaJalan.data.model.DestinasiModel
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
            val repository = DestinasiRepository()
            DestinasiViewModel(repository)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    private fun showBottomSheetDialog(existingDestinasi: DestinasiModel? = null) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding =
            BottomAddDestinationSheetLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        // Isi field jika dalam mode edit
        existingDestinasi?.let { destinasi ->
            bottomSheetBinding.inputNamaDestinasi.setText(destinasi.namaDestinasi)
            bottomSheetBinding.inputKategori.setText(destinasi.kategori)
            bottomSheetBinding.inputFoto.setText(destinasi.foto)
            bottomSheetBinding.inputDeskripsi.setText(destinasi.deskripsi)
            bottomSheetBinding.inputHarga.setText(destinasi.harga.toString())
            bottomSheetBinding.inputLokasi.setText(destinasi.lokasi)
            bottomSheetBinding.inputFasilitas.setText(destinasi.fasilitas.joinToString(", "))
        }

        bottomSheetBinding.submitButton.setOnClickListener {
            val title = bottomSheetBinding.inputNamaDestinasi.text.toString().trim()
            val subtitle = bottomSheetBinding.inputKategori.text.toString().trim()
            val picAddress = bottomSheetBinding.inputFoto.text.toString().trim()
            val description = bottomSheetBinding.inputDeskripsi.text.toString().trim()
            val price = bottomSheetBinding.inputHarga.text.toString().trim()
            val location = bottomSheetBinding.inputLokasi.text.toString().trim()
            val facilitiesInput = bottomSheetBinding.inputFasilitas.text.toString().trim()

            if (title.isEmpty() || subtitle.isEmpty() || description.isEmpty() || price.isEmpty() || location.isEmpty()) {
                Snackbar.make(binding.root, "Semua field harus diisi!", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val facilities = if (facilitiesInput.isNotEmpty()) {
                facilitiesInput.split(",").map { it.trim() }
            } else {
                emptyList()
            }

            val finalPicAddress = if (picAddress.isEmpty()) {
                "https://images.unsplash.com/photo-1516117172878-fd2c41f4a759?w=1024"
            } else {
                picAddress
            }

            val destinasiRequest = DestinasiModel(
                id = existingDestinasi?.id ?: "",
                namaDestinasi = title,
                fasilitas = facilities,
                foto = finalPicAddress,
                harga = price.toDoubleOrNull() ?: 0.0,
                lokasi = location,
                kategori = subtitle,
                deskripsi = description
            )

            if (existingDestinasi == null) {
                createNewDestination(destinasiRequest)
            } else {
                updateDestination(destinasiRequest)
            }
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun getDestination() {
        destinasiViewModel.getDestinasi(requireContext())
        destinasiViewModel.data.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Empty -> {
                    binding.emptyWisata.root.visibility = View.VISIBLE
                    binding.loadingWisata.root.visibility = View.GONE
                    binding.errorWisata.root.visibility = View.GONE
                    binding.recyclerWisata.visibility = View.GONE
                    binding.emptyWisata.emptyMessage.text = resource.message
                }
                is Resource.Error -> {
                    binding.emptyWisata.root.visibility = View.GONE
                    binding.loadingWisata.root.visibility = View.GONE
                    binding.errorWisata.root.visibility = View.VISIBLE
                    binding.recyclerWisata.visibility = View.GONE
                    binding.errorWisata.errorMessage.text = resource.message
                }
                is Resource.Loading -> {
                    binding.emptyWisata.root.visibility = View.GONE
                    binding.loadingWisata.root.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.emptyWisata.root.visibility = View.GONE
                    binding.loadingWisata.root.visibility = View.GONE
                    destinasiAdapter.updateData(resource.data!!)
                }
            }
        }
    }

    private fun createNewDestination(destinasi: DestinasiModel) {
        destinasiViewModel.addDestinasi(requireContext(), listOf(destinasi))
        destinasiViewModel.createStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Destinasi berhasil ditambahkan!", Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Snackbar.make(binding.root, "Gagal menambahkan destinasi: ${resource.message}", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Loading -> {}
                is Resource.Empty -> {}
            }
        }
    }

    private fun updateDestination(destinasi: DestinasiModel) {
        destinasiViewModel.updateDestinasi(requireContext(), destinasi)
        destinasiViewModel.createStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Destinasi berhasil diperbarui!", Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Snackbar.make(binding.root, "Gagal memperbarui destinasi: ${resource.message}", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Loading -> {}
                is Resource.Empty -> {}
            }
        }
    }

    private fun onEditClick(destinasi: DestinasiModel) {
        showBottomSheetDialog(destinasi)
    }

    private fun onDeleteClick(destinasi: DestinasiModel) {
        destinasiViewModel.deleteDestinasi(requireContext(), destinasi.id!!)
        destinasiViewModel.deleteStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Destinasi berhasil dihapus!", Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Snackbar.make(binding.root, "Gagal menghapus destinasi: ${resource.message}", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Loading -> {}
                is Resource.Empty -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}