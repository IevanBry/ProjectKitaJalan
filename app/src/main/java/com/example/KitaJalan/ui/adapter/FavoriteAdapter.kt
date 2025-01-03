package com.example.KitaJalan.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.databinding.FavItemBinding
import com.squareup.picasso.Picasso

class FavoriteAdapter(
    private val context: Context,
    private var destinationList: List<DestinasiModel>,
    private val onItemClick: (DestinasiModel) -> Unit // Callback untuk item klik
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(private val binding: FavItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(destination: DestinasiModel) {
            binding.favTextView.text = destination.namaDestinasi
            binding.favDateTextView.text = destination.lokasi

            Picasso.get()
                .load(destination.foto)
                .placeholder(com.example.KitaJalan.R.drawable.logo)
                .error(com.example.KitaJalan.R.drawable.logo)
                .into(binding.favImageView)

            binding.root.setOnClickListener {
                onItemClick(destination)
            }
        }
    }

    fun updateData(newDestinationList: List<DestinasiModel>) {
        destinationList = newDestinationList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = FavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(destinationList[position])
    }

    override fun getItemCount(): Int = destinationList.size
}