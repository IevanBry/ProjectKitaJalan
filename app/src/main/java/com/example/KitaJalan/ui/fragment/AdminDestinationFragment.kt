package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.data.model.DestinasiPostRequest
import com.example.KitaJalan.data.repository.DestinasiRepository
import com.example.KitaJalan.databinding.BottomAddDestinationSheetLayoutBinding
import com.example.KitaJalan.databinding.FragmentAdminDestinationBinding
import com.example.KitaJalan.ui.adapter.DestinasiAdminAdapter
import com.example.KitaJalan.ui.viewModel.DestinasiViewModel
import com.example.KitaJalan.utils.Resource
import com.example.KitaJalan.utils.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat
import java.util.UUID

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

    private fun showBottomSheetDialog(existingDestinasi: DestinasiPostRequest? = null) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding =
            BottomAddDestinationSheetLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        val fasilitasList = mutableListOf<String>()

        existingDestinasi?.let { destinasi ->
            fasilitasList.addAll(destinasi.fasilitas)
            destinasi.fasilitas.forEach { fasilitas ->
                addChipToGroup(fasilitas, bottomSheetBinding.chipGroupFasilitas, fasilitasList)
            }
            bottomSheetBinding.inputNamaDestinasi.setText(destinasi.namaDestinasi)
            bottomSheetBinding.inputKategori.setText(destinasi.kategori)
            bottomSheetBinding.inputFoto.setText(destinasi.foto)
            bottomSheetBinding.inputDeskripsi.setText(destinasi.deskripsi)
            val formattedPrice = if (destinasi.harga % 1 == 0.0) {
                destinasi.harga.toInt().toString()
            } else {
                DecimalFormat("#.##").format(destinasi.harga)
            }
            bottomSheetBinding.inputHarga.setText(formattedPrice)
            bottomSheetBinding.inputLokasi.setText(destinasi.lokasi)
        }

        val fasilitasOptions = listOf("Kolam Renang", "Kamar Mandi", "Area Parkir", "Restoran", "WiFi Gratis")
        bottomSheetBinding.inputFasilitasDropdown.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            fasilitasOptions.forEach { fasilitas ->
                popupMenu.menu.add(fasilitas)
            }
            popupMenu.setOnMenuItemClickListener { menuItem ->
                val selectedFasilitas = menuItem.title.toString()

                if (!fasilitasList.contains(selectedFasilitas)) {
                    fasilitasList.add(selectedFasilitas)
                    addChipToGroup(selectedFasilitas, bottomSheetBinding.chipGroupFasilitas, fasilitasList)
                }
                true
            }
            popupMenu.show()
        }

        // Daftar kategori
        val categories = listOf("Pantai", "Pegunungan", "Taman Hiburan", "Kawasan Kota")
        bottomSheetBinding.inputKategori.setOnClickListener {
            showCategoryPopup(it, categories) { selectedCategory ->
                bottomSheetBinding.inputKategori.setText(selectedCategory)
            }
        }

        bottomSheetBinding.submitButton.setOnClickListener {
            val title = bottomSheetBinding.inputNamaDestinasi.text.toString().trim()
            val subtitle = bottomSheetBinding.inputKategori.text.toString().trim()
            val picAddress = bottomSheetBinding.inputFoto.text.toString().trim()
            val description = bottomSheetBinding.inputDeskripsi.text.toString().trim()
            val price = bottomSheetBinding.inputHarga.text.toString().trim()
            val location = bottomSheetBinding.inputLokasi.text.toString().trim()

            if (title.isEmpty() || subtitle.isEmpty() || description.isEmpty() || price.isEmpty() || location.isEmpty()) {
                Snackbar.make(binding.root, "Semua field harus diisi!", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val destinasiRequest = if (existingDestinasi != null) {
                DestinasiPostRequest(
                    id = existingDestinasi.id, // Pastikan `DestinasiPostRequest` memiliki field `id`
                    namaDestinasi = title,
                    fasilitas = fasilitasList,
                    foto = picAddress,
                    harga = price.toDoubleOrNull() ?: 0.0,
                    lokasi = location,
                    kategori = subtitle,
                    deskripsi = description
                )
            } else {
                DestinasiPostRequest(
                    namaDestinasi = title,
                    fasilitas = fasilitasList,
                    foto = picAddress,
                    harga = price.toDoubleOrNull() ?: 0.0,
                    lokasi = location,
                    kategori = subtitle,
                    deskripsi = description
                )
            }

            if (existingDestinasi == null) {
                createNewDestination(destinasiRequest)
            } else {
                updateDestination(destinasiRequest)
            }
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun addChipToGroup(fasilitas: String, chipGroup: ChipGroup, fasilitasList: MutableList<String>) {
        val chip = Chip(requireContext()).apply {
            text = fasilitas
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                chipGroup.removeView(this)
                fasilitasList.remove(fasilitas)
            }
        }
        chipGroup.addView(chip)
    }

    private fun showCategoryPopup(view: View, categories: List<String>, onCategorySelected: (String) -> Unit) {
        val popupMenu = PopupMenu(requireContext(), view)
        categories.forEach { category ->
            popupMenu.menu.add(category)
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            onCategorySelected(menuItem.title.toString())
            true
        }
        popupMenu.show()
    }

    private fun getDestination() {
        destinasiViewModel.getDestinasi(requireContext())
        destinasiViewModel.data.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Empty -> showEmptyMessage(resource.message)
                is Resource.Error -> showErrorMessage(resource.message)
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    hideAllMessages()
                    destinasiAdapter.updateData(resource.data!!)
                }
            }
        }
    }

    private fun createNewDestination(destinasi: DestinasiPostRequest) {
        destinasiViewModel.addDestinasi(requireContext(), listOf(destinasi))
        destinasiViewModel.createStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Destinasi berhasil ditambahkan!", Snackbar.LENGTH_SHORT).show()
                    getDestination() // Refresh data setelah penambahan
                }
                is Resource.Error -> {
                    Snackbar.make(binding.root, "Gagal menambahkan destinasi: ${resource.message}", Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun updateDestination(destinasi: DestinasiPostRequest) {
        destinasiViewModel.updateDestinasi(requireContext(), destinasi)
        destinasiViewModel.createStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Destinasi berhasil diperbarui!", Snackbar.LENGTH_SHORT).show()
                    getDestination() // Refresh data setelah pembaruan
                }
                is Resource.Error -> {
                    Snackbar.make(binding.root, "Gagal memperbarui destinasi: ${resource.message}", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Loading -> {

                }
                else -> {}
            }
        }
    }

    private fun onEditClick(destinasi: DestinasiModel) {
        val destinasiPostRequest = DestinasiPostRequest(
            id = destinasi.id,
            namaDestinasi = destinasi.namaDestinasi,
            fasilitas = destinasi.fasilitas,
            foto = destinasi.foto,
            harga = destinasi.harga,
            lokasi = destinasi.lokasi,
            kategori = destinasi.kategori,
            deskripsi = destinasi.deskripsi
        )
        showBottomSheetDialog(destinasiPostRequest)
    }

    private fun onDeleteClick(destinasi: DestinasiModel) {
        destinasiViewModel.deleteDestinasi(requireContext(), destinasi.id!!)
        destinasiViewModel.deleteStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Destinasi berhasil dihapus!", Snackbar.LENGTH_SHORT).show()
                    getDestination() // Refresh data setelah penghapusan
                }
                is Resource.Error -> {
                    Snackbar.make(binding.root, "Gagal menghapus destinasi: ${resource.message}", Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun showEmptyMessage(message: String?) {
        binding.emptyWisata.root.visibility = View.VISIBLE
        binding.loadingWisata.root.visibility = View.GONE
        binding.errorWisata.root.visibility = View.GONE
        binding.recyclerWisata.visibility = View.GONE
        binding.emptyWisata.emptyMessage.text = message ?: "Tidak ada data."
    }

    private fun showErrorMessage(message: String?) {
        binding.emptyWisata.root.visibility = View.GONE
        binding.loadingWisata.root.visibility = View.GONE
        binding.errorWisata.root.visibility = View.VISIBLE
        binding.recyclerWisata.visibility = View.GONE
        binding.errorWisata.errorMessage.text = message ?: "Terjadi kesalahan."
    }

    private fun showLoading() {
        binding.emptyWisata.root.visibility = View.GONE
        binding.loadingWisata.root.visibility = View.VISIBLE
        binding.errorWisata.root.visibility = View.GONE
        binding.recyclerWisata.visibility = View.GONE
    }

    private fun hideAllMessages() {
        binding.emptyWisata.root.visibility = View.GONE
        binding.loadingWisata.root.visibility = View.GONE
        binding.errorWisata.root.visibility = View.GONE
        binding.recyclerWisata.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}