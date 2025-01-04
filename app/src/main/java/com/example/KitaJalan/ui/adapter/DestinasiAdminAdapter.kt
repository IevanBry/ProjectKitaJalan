package com.example.KitaJalan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.databinding.ItemDestinasiAdminBinding
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class DestinasiAdminAdapter(
    private var items: List<DestinasiModel>,
    private val onEditClick: (DestinasiModel) -> Unit,
    private val onDeleteClick: (DestinasiModel) -> Unit
) : RecyclerView.Adapter<DestinasiAdminAdapter.DestinasiViewHolder>() {

    fun updateData(newItems: List<DestinasiModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinasiViewHolder {
        val binding = ItemDestinasiAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DestinasiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DestinasiViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.binding.btnEdit.setOnClickListener {
            onEditClick(item)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class DestinasiViewHolder(val binding: ItemDestinasiAdminBinding) : RecyclerView.ViewHolder(binding.root) {

        private val decimalFormat: DecimalFormat

        init {
            val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
                decimalSeparator = '.'
                groupingSeparator = ','
            }
            decimalFormat = DecimalFormat("#,###.##", symbols)
            decimalFormat.isGroupingUsed = true
        }

        fun bind(destinasi: DestinasiModel) {
            binding.apply {
                title.text = destinasi.namaDestinasi
                subtitle.text = destinasi.kategori
                price.text = "Harga: Rp${formatHarga(destinasi.harga)}"
                location.text = "Lokasi: ${destinasi.lokasi}"
                description.text = "Deskripsi: ${destinasi.deskripsi}"
                facilities.text = "Fasilitas: ${destinasi.fasilitas.joinToString(", ")}"

                Picasso.get()
                    .load(destinasi.foto)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .fit()
                    .centerCrop()
                    .into(image)
            }
        }

        private fun formatHarga(harga: Double): String {
            return if (harga % 1.0 == 0.0) {
                decimalFormat.applyPattern("#,###")
                decimalFormat.format(harga.toInt())
            } else {
                decimalFormat.applyPattern("#,###.##")
                decimalFormat.format(harga)
            }
        }
    }
}