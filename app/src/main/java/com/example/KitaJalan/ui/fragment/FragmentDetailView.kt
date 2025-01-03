package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.KitaJalan.R
import com.example.KitaJalan.databinding.FragmentDetailViewBinding
import com.example.KitaJalan.ui.adapter.Fasilitas
import com.example.KitaJalan.ui.adapter.FasilitasAdapter
import java.text.NumberFormat
import java.util.Locale

class FragmentDetailView : Fragment() {

    private lateinit var binding: FragmentDetailViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailViewBinding.inflate(inflater, container, false)
        setupUI()
        setupFasilitas()
        return binding.root
    }

    private fun setupUI() {
        val args = arguments
        binding.textView4.text = args?.getString("namaDestinasi")
        binding.descriptionText.text = args?.getString("deskripsi")
        binding.locationText.text = args?.getString("lokasi")
        setupPrice(args?.getDouble("harga"))
        setupRating(args?.getDouble("averageRating", 0.0) ?: 0.0)
    }

    private fun setupPrice(harga: Double?) {
        binding.priceText.text = if (harga != null) {
            formatRupiah(harga)
        } else {
            "Harga tidak tersedia"
        }
    }

    private fun formatRupiah(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return formatter.format(amount)
    }

    private fun setupRating(averageRating: Double) {
        binding.rating.text = formatRating(averageRating)
    }

    private fun formatRating(rating: Double): String {
        return String.format("%.1f", rating)
    }

    private fun setupFasilitas() {
        val args = arguments
        val fasilitas: List<String> = args?.getStringArrayList("fasilitas") ?: emptyList()
        val fasilitasList = fasilitas.map { nama ->
            Fasilitas(
                name = nama,
                icon = getIconForFacility(nama)
            )
        }
        binding.fasilitasRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = FasilitasAdapter(fasilitasList)
        }
    }

    private fun getIconForFacility(name: String): Int {
        return when (name.lowercase()) {
            "wifi" -> R.drawable.wifi
            "toilet" -> R.drawable.toilet
            "gym" -> R.drawable.gym
            else -> R.drawable.gym
        }
    }
}