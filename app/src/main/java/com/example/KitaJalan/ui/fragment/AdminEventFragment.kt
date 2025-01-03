package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.EventModel
import com.example.KitaJalan.data.repository.EventRepository
import com.example.KitaJalan.databinding.BottomAddEventSheetLayoutBinding
import com.example.KitaJalan.databinding.FragmentAdminEventBinding
import com.example.KitaJalan.ui.adapter.EventAdminAdapter
import com.example.KitaJalan.ui.viewModel.EventViewModel
import com.example.KitaJalan.utils.Resource
import com.example.KitaJalan.utils.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import java.util.*

class AdminEventFragment : Fragment() {

    private var _binding: FragmentAdminEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdminAdapter: EventAdminAdapter

    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory(EventViewModel::class.java) {
            EventViewModel(EventRepository())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminEventBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupFAB()
        getEvents()
        return binding.root
    }

    private fun setupRecyclerView() {
        eventAdminAdapter = EventAdminAdapter(emptyList(), ::onEditClick, ::onDeleteClick)
        binding.recyclerEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEvent.adapter = eventAdminAdapter
    }

    private fun setupFAB() {
        binding.fab.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog(existingEvent: EventModel? = null) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding =
            BottomAddEventSheetLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        existingEvent?.let { event ->
            bottomSheetBinding.inputJudulEvent.setText(event.judul)
            bottomSheetBinding.inputDeskripsiEvent.setText(event.deskripsi)
            bottomSheetBinding.inputTanggalMulai.setText(event.tanggalMulai)
            bottomSheetBinding.inputTanggalSelesai.setText(event.tanggalSelesai)
            bottomSheetBinding.inputUrlLokasi.setText(event.googleMapsUrl)
            bottomSheetBinding.inputUrlGambar.setText(event.gambarUrl)
        }

        bottomSheetBinding.btnSimpanEvent.setOnClickListener {
            val title = bottomSheetBinding.inputJudulEvent.text.toString().trim()
            val description = bottomSheetBinding.inputDeskripsiEvent.text.toString().trim()
            val startDate = bottomSheetBinding.inputTanggalMulai.text.toString().trim()
            val endDate = bottomSheetBinding.inputTanggalSelesai.text.toString().trim()
            val googleMapsUrl = bottomSheetBinding.inputUrlLokasi.text.toString().trim()
            val imageUrl = bottomSheetBinding.inputUrlGambar.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || startDate.isEmpty() ||
                endDate.isEmpty() || googleMapsUrl.isEmpty() || imageUrl.isEmpty()) {
                binding.root.context?.let {
                    Snackbar.make(binding.root, "Semua field harus diisi!", Snackbar.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            val eventRequest = EventModel(
                id = existingEvent?.id ?: UUID.randomUUID().toString(),
                judul = title,
                deskripsi = description,
                tanggalMulai = startDate,
                tanggalSelesai = endDate,
                googleMapsUrl = googleMapsUrl,
                gambarUrl = imageUrl
            )

            if (existingEvent == null) {
                createNewEvent(eventRequest)
            } else {
                updateEvent(eventRequest)
            }
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun getEvents() {
        eventViewModel.getEvents(requireContext())
        eventViewModel.data.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Empty -> showEmptyMessage()
                is Resource.Error -> showErrorMessage(resource.message)
                is Resource.Loading -> showLoading()
                is Resource.Success -> {
                    hideAllMessages()
                    eventAdminAdapter.updateData(resource.data!!)
                }
            }
        }
    }

    private fun createNewEvent(event: EventModel) {
        eventViewModel.addEvent(requireContext(), listOf(event))
        eventViewModel.createStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Empty -> showEmptyMessage()
                is Resource.Success -> {
                    hideAllMessages()
                }
                is Resource.Error -> showErrorMessage(resource.message)
            }
        }
    }

    private fun updateEvent(event: EventModel) {
        eventViewModel.updateEvent(requireContext(), event)
        eventViewModel.createStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Empty -> showEmptyMessage()
                is Resource.Success -> {
                    hideAllMessages()
                }
                is Resource.Error -> showErrorMessage(resource.message)
            }
        }
    }

    private fun onEditClick(event: EventModel) {
        showBottomSheetDialog(event)
    }

    private fun onDeleteClick(event: EventModel) {
        eventViewModel.deleteEvent(requireContext(), event.id)
        eventViewModel.deleteStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> showLoading()
                is Resource.Empty -> showEmptyMessage()
                is Resource.Success -> {
                    hideAllMessages()
                    Snackbar.make(binding.root, "Event berhasil dihapus!", Snackbar.LENGTH_SHORT).show()
                }
                is Resource.Error -> showErrorMessage(resource.message)
            }
        }
    }

    private fun showEmptyMessage() {
        binding.emptyEvent.root.visibility = View.VISIBLE
        binding.loadingEvent.root.visibility = View.GONE
        binding.errorEvent.root.visibility = View.GONE
        binding.recyclerEvent.visibility = View.GONE
    }

    private fun showErrorMessage(message: String?) {
        binding.emptyEvent.root.visibility = View.GONE
        binding.loadingEvent.root.visibility = View.GONE
        binding.errorEvent.root.visibility = View.VISIBLE
        binding.recyclerEvent.visibility = View.GONE
        binding.errorEvent.errorMessage.text = message ?: "Terjadi kesalahan."
    }

    private fun showLoading() {
        binding.emptyEvent.root.visibility = View.GONE
        binding.loadingEvent.root.visibility = View.VISIBLE
        binding.errorEvent.root.visibility = View.GONE
        binding.recyclerEvent.visibility = View.GONE
    }

    private fun hideAllMessages() {
        binding.emptyEvent.root.visibility = View.GONE
        binding.loadingEvent.root.visibility = View.GONE
        binding.errorEvent.root.visibility = View.GONE
        binding.recyclerEvent.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}