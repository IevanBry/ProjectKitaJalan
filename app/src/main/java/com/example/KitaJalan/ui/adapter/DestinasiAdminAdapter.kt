package com.example.KitaJalan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.databinding.ItemDestinasiAdminBinding
import com.squareup.picasso.Picasso

class DestinasiAdminAdapter(
    private var items: List<DestinasiModel>,
    private val onEditClick: (DestinasiModel) -> Unit, // Ubah ke DestinasiModel
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
            onEditClick(item) // Sekarang sesuai tipe
        }

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    class DestinasiViewHolder(val binding: ItemDestinasiAdminBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(destinasi: DestinasiModel) {
            binding.title.text = destinasi.namaDestinasi
            binding.subtitle.text = destinasi.kategori
            binding.price.text = "Harga: Rp${destinasi.harga}"
            binding.location.text = "Lokasi: ${destinasi.lokasi}"
            binding.description.text = "Deskripsi: ${destinasi.deskripsi}"
            binding.facilities.text = "Fasilitas: ${destinasi.fasilitas.joinToString(", ")}"

            Picasso.get()
                .load(destinasi.foto)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .fit()
                .centerCrop()
                .into(binding.image)
        }
    }
}