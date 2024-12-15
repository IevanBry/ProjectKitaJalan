package com.example.KitaJalan.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.EventModel
import com.example.KitaJalan.databinding.BottomAddDestinationSheetLayoutBinding
import com.example.KitaJalan.databinding.BottomAddEventSheetLayoutBinding
import com.example.KitaJalan.databinding.FragmentEventBinding
import com.example.KitaJalan.ui.adapter.EventAdminAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminEventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdminAdapter: EventAdminAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupFab()

        return binding.root
    }

    private fun setupRecyclerView() {
        val events = listOf(
            EventModel(
                judul = "Festival Musik",
                deskripsi = "Festival musik terbesar di kota.",
                tanggalMulai = "2024-07-01",
                tanggalSelesai = "2024-07-05",
                gambarUrl = "https://example.com/image1.jpg"
            ),
            EventModel(
                judul = "Pameran Seni",
                deskripsi = "Pameran seni modern di museum.",
                tanggalMulai = "2024-07-10",
                tanggalSelesai = "2024-07-15",
                gambarUrl = "https://example.com/image2.jpg"
            )
        )

        eventAdminAdapter = EventAdminAdapter(events)
        binding.recyclerEvent.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerEvent.adapter = eventAdminAdapter
    }

    private fun showEventBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding =
            BottomAddEventSheetLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        bottomSheetDialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.btnSubmitUrl.setOnClickListener {
            val imageUrl = bottomSheetBinding.inputUrlGambar.text.toString()
            if (imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(imageUrl)
                    .error(R.drawable.sample_event)
                    .into(bottomSheetBinding.previewGambarEvent)
            } else {
                Toast.makeText(requireContext(), "URL gambar tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetBinding.inputTanggalMulai.setOnClickListener {
            initDatePicker(bottomSheetBinding.inputTanggalMulai)
        }

        bottomSheetBinding.inputTanggalSelesai.setOnClickListener {
            initDatePicker(bottomSheetBinding.inputTanggalSelesai)
        }

        bottomSheetBinding.btnSimpanEvent.setOnClickListener {
            val judulEvent = bottomSheetBinding.inputJudulEvent.text.toString()
            val deskripsiEvent = bottomSheetBinding.inputDeskripsiEvent.text.toString()
            val tanggalMulai = bottomSheetBinding.inputTanggalMulai.text.toString()
            val tanggalSelesai = bottomSheetBinding.inputTanggalSelesai.text.toString()

            if (judulEvent.isEmpty() || deskripsiEvent.isEmpty() ||
                tanggalMulai.isEmpty() || tanggalSelesai.isEmpty()) {
                Toast.makeText(requireContext(), "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("EventInput", "Judul: $judulEvent")
                Log.d("EventInput", "Deskripsi: $deskripsiEvent")
                Log.d("EventInput", "Tanggal Mulai: $tanggalMulai")
                Log.d("EventInput", "Tanggal Selesai: $tanggalSelesai")
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

    private fun setupFab()
    {
        binding.fab.setOnClickListener {
            showEventBottomSheet()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initDatePicker(inputField: TextInputEditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pilih Tanggal")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
        datePicker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val selectedDate = dateFormat.format(Date(selection))
            inputField.setText(selectedDate)
        }
    }
}