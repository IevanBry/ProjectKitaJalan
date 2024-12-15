package com.example.KitaJalan.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.databinding.FavItemBinding
import com.squareup.picasso.Picasso

class FavoriteAdapter(
    private val context: Context,
    private val favoriteList: List<DestinasiModel>) :
    RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    class FavoriteViewHolder(private val binding: FavItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favoriteItem: DestinasiModel, context: Context) {
            binding.favTextView.text = favoriteItem.namaDestinasi
            loadImage(favoriteItem.foto, binding.favImageView, context)
        }

        private fun loadImage(picAddress: String, imageView: ImageView, context: Context) {
            if (picAddress.startsWith("http") || picAddress.startsWith("https")) {
                Picasso.get()
                    .load(picAddress)
                    .error(R.drawable.logo)
                    .into(imageView)
            } else {
                // Memuat gambar dari resource drawable
                val drawableResourceId = context.resources.getIdentifier(picAddress, "drawable", context.packageName)
                imageView.setImageResource(drawableResourceId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = FavItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favoriteItem = favoriteList[position]
        holder.bind(favoriteItem, context) // Mengirim context untuk memuat gambar
    }

    override fun getItemCount(): Int = favoriteList.size
}