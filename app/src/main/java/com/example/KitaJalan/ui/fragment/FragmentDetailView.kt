package com.example.KitaJalan.ui.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.KitaJalan.R
import com.example.KitaJalan.databinding.FragmentDetailViewBinding
import com.example.KitaJalan.ui.adapter.Fasilitas
import com.example.KitaJalan.ui.adapter.FasilitasAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale

class FragmentDetailView : Fragment() {

    private lateinit var binding: FragmentDetailViewBinding
    private var ticketCount = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailViewBinding.inflate(inflater, container, false)

        setupUI()
        setupTicketCounter()
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

        binding.bookBtn.setOnClickListener {
            val noHp = args?.getString("noHp")
            if (!noHp.isNullOrEmpty()) {
                redirectToWhatsApp(noHp)
            } else {
                Snackbar.make(binding.root, "Nomor HP tidak tersedia", Snackbar.LENGTH_SHORT).show()
            }
        }
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

    private fun redirectToWhatsApp(noHp: String) {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "user@example.com"
        val namaPemesan = currentUserEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
        val namaDestinasi = binding.textView4.text.toString()

        val formattedNumber = if (noHp.startsWith("+")) noHp else "+62${noHp.removePrefix("0")}"
        Log.d("FragmentDetailView", "Nomor HP: $formattedNumber")

        val message = """
        $namaDestinasi
        Nama Pemesan : $namaPemesan
        Jumlah Tiket : $ticketCount
    """.trimIndent()

        if (isAppInstalled("com.whatsapp")) {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("whatsapp://send?phone=$formattedNumber&text=${Uri.encode(message)}")
                    `package` = "com.whatsapp"
                }
                startActivity(intent)
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Gagal membuka WhatsApp", Snackbar.LENGTH_SHORT).show()
                Log.e("FragmentDetailView", "Error: ${e.message}", e)
            }
        } else {
            Snackbar.make(binding.root, "WhatsApp tidak tersedia di perangkat ini", Snackbar.LENGTH_SHORT).show()
            Log.d("FragmentDetailView", "WhatsApp tidak ditemukan")
        }
    }

    private fun setupTicketCounter() {
        binding.ticketCountText.text = ticketCount.toString()

        binding.minusButton.setOnClickListener {
            if (ticketCount > 1) {
                ticketCount--
                binding.ticketCountText.text = ticketCount.toString()
            }
        }

        binding.plusButton.setOnClickListener {
            ticketCount++
            binding.ticketCountText.text = ticketCount.toString()
        }
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            val packageManager = requireContext().packageManager
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun isWhatsAppAvailable(): Boolean {
        return isAppInstalled("com.whatsapp") || isAppInstalled("com.whatsapp.w4b")
    }
}