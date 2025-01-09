package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.KitaJalan.R
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
import com.google.firebase.auth.FirebaseAuth
import java.text.DecimalFormat


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

    private lateinit var currentAdminId: String
    private lateinit var currentAdminEmail: String
    private var isSuperAdmin: Boolean = false

    private var allDestinasiList: List<DestinasiModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAdminDestinationBinding.inflate(inflater, container, false)
        setupFAB()
        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getAdminIdAndFetchDestinations()
        setupObservers()
        setupSearchView()
//        insertStaticDestinationsToFirebase()
    }

    private fun setupRecyclerView() {
        destinasiAdapter = DestinasiAdminAdapter(emptyList(), ::onEditClick, ::onDeleteClick)
//        val gridLayoutManager = GridLayoutManager(context, 2) // 2 adalah jumlah kolom dalam grid
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
            bottomSheetBinding.inputNoHp.setText(destinasi.noHp)
        }

        val fasilitasOptions = listOf("Kolam Renang", "Kamar Mandi", "Area Parkir", "Restoran", "WiFi Gratis", "Gym")
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

        val categories = listOf("Pantai", "Hotel", "Taman Hiburan", "Wisata Alam")
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
            val noHp = bottomSheetBinding.inputNoHp.text.toString().trim()

            if (title.isEmpty() || subtitle.isEmpty() || description.isEmpty() || price.isEmpty() || location.isEmpty() || noHp.isEmpty()) {
                Snackbar.make(binding.root, "Semua field harus diisi!", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val destinasiRequest = if (existingDestinasi != null) {
                DestinasiPostRequest(
                    id = existingDestinasi.id,
                    namaDestinasi = title,
                    fasilitas = fasilitasList,
                    foto = picAddress,
                    harga = price.toDoubleOrNull() ?: 0.0,
                    lokasi = location,
                    noHp = noHp,
                    kategori = subtitle,
                    deskripsi = description,
                    adminId = existingDestinasi.adminId
                )
            } else {
                DestinasiPostRequest(
                    namaDestinasi = title,
                    fasilitas = fasilitasList,
                    foto = picAddress,
                    harga = price.toDoubleOrNull() ?: 0.0,
                    lokasi = location,
                    noHp = noHp,
                    kategori = subtitle,
                    deskripsi = description,
                    adminId = currentAdminId
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

    private fun getAdminIdAndFetchDestinations() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Snackbar.make(binding.root, "User not logged in", Snackbar.LENGTH_LONG).show()
            return
        }

        currentAdminId = currentUser.uid
        currentAdminEmail = currentUser.email ?: ""

        val userNameRaw = currentAdminEmail.substringBefore("@")
        val userName = if (userNameRaw.isNotEmpty()) {
            userNameRaw.replaceFirstChar { it.uppercase() }
        } else {
            "User"
        }

        isSuperAdmin = currentAdminEmail.equals("admin@gmail.com", ignoreCase = true)

        if (isSuperAdmin) {
            destinasiViewModel.getDestinasi(requireContext())
        } else {
            destinasiViewModel.getDestinasiByAdmin(requireContext(), currentAdminId, userName)
        }
    }

    private fun setupObservers() {
        destinasiViewModel.data.observe(viewLifecycleOwner) { resource ->
            if (isSuperAdmin) {
                when (resource) {
                    is Resource.Empty -> showEmptyMessage(resource.message)
                    is Resource.Error -> showErrorMessage(resource.message)
                    is Resource.Loading -> showLoading()
                    is Resource.Success -> {
                        hideAllMessages()
                        allDestinasiList = resource.data!!
                        destinasiAdapter.updateData(allDestinasiList)
                    }
                }
            }
        }

        destinasiViewModel.adminData.observe(viewLifecycleOwner) { resource ->
            if (!isSuperAdmin) {
                when (resource) {
                    is Resource.Empty -> showEmptyMessage(resource.message)
                    is Resource.Error -> showErrorMessage(resource.message)
                    is Resource.Loading -> showLoading()
                    is Resource.Success -> {
                        hideAllMessages()
                        allDestinasiList = resource.data!!
                        destinasiAdapter.updateData(allDestinasiList)
                    }
                }
            }
        }

        destinasiViewModel.createStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Operasi berhasil!", Snackbar.LENGTH_SHORT).show()
                    refreshData()
                }
                is Resource.Error -> {
                    Snackbar.make(binding.root, "Operasi gagal: ${resource.message}", Snackbar.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                }
                else -> {}
            }
        }

        destinasiViewModel.deleteStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Snackbar.make(binding.root, "Destinasi berhasil dihapus!", Snackbar.LENGTH_SHORT).show()
                    refreshData()
                }
                is Resource.Error -> {
                    Snackbar.make(binding.root, "Gagal menghapus destinasi: ${resource.message}", Snackbar.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun setupSearchView() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterDestinasi(newText)
                return true
            }
        })

        val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        editText.setTextColor(android.graphics.Color.BLACK)

        editText.setHintTextColor(android.graphics.Color.BLACK)

        val searchPlate = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        searchPlate.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
        searchIcon.setColorFilter(getResources().getColor(R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
    }

    private fun filterDestinasi(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            allDestinasiList
        } else {
            allDestinasiList.filter { destinasi ->
                destinasi.namaDestinasi.contains(query, ignoreCase = true) ||
                        destinasi.kategori.contains(query, ignoreCase = true)
            }
        }
        destinasiAdapter.updateData(filteredList)
    }

    private fun refreshData() {
        if (isSuperAdmin) {
            destinasiViewModel.getDestinasi(requireContext(), forceRefresh = true)
        } else {
            val userNameRaw = currentAdminEmail.substringBefore("@")
            val userName = if (userNameRaw.isNotEmpty()) {
                userNameRaw.replaceFirstChar { it.uppercase() }
            } else {
                "User"
            }
            destinasiViewModel.getDestinasiByAdmin(requireContext(), currentAdminId, userName, forceRefresh = true)
        }
    }

    private fun createNewDestination(destinasi: DestinasiPostRequest) {
        destinasiViewModel.addDestinasi(requireContext(), listOf(destinasi))
    }

    private fun updateDestination(destinasi: DestinasiPostRequest) {
        destinasiViewModel.updateDestinasi(requireContext(), destinasi)
    }

    private fun onEditClick(destinasi: DestinasiModel) {
        val destinasiPostRequest = DestinasiPostRequest(
            id = destinasi.id,
            namaDestinasi = destinasi.namaDestinasi,
            fasilitas = destinasi.fasilitas,
            foto = destinasi.foto,
            harga = destinasi.harga,
            lokasi = destinasi.lokasi,
            noHp = destinasi.noHp,
            kategori = destinasi.kategori,
            deskripsi = destinasi.deskripsi,
            adminId = destinasi.adminId
        )
        showBottomSheetDialog(destinasiPostRequest)
    }

    private fun onDeleteClick(destinasi: DestinasiModel) {
        destinasiViewModel.deleteDestinasi(requireContext(), destinasi.id!!)
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

    private fun insertStaticDestinationsToFirebase() {
        val staticDestinations = listOf(
            DestinasiPostRequest(
                namaDestinasi = "Pantai Cinta Pekanbaru",
                fasilitas = listOf("Area Parkir", "Restoran", "WiFi Gratis"),
                foto = "https://ik.imagekit.io/pyg91hrnd/Pantai%20Cinta%20Pekanbaru.jpeg?updatedAt=1736426959053",
                harga = 15000.0,
                lokasi = "Jl. Raya Pasir Putih, Pekanbaru",
                kategori = "Wisata Alam",
                deskripsi = "Pantai buatan yang menawarkan suasana romantis dan pemandangan yang menenangkan di pinggir kota Pekanbaru.",
                adminId = currentAdminId,
                noHp = "081234567896"
            )
        )

        staticDestinations.forEach { destinasi ->
            destinasiViewModel.addDestinasi(requireContext(), listOf(destinasi))
        }
        Snackbar.make(binding.root, "Data destinasi statis berhasil ditambahkan!", Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}