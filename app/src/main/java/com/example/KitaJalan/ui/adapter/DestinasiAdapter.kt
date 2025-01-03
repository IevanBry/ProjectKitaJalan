package com.example.KitaJalan.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.databinding.ItemDestinasiBinding
import com.squareup.picasso.Picasso

class DestinasiAdapter(
    private var items: List<DestinasiModel>,
    private val context: Context,
    private val onItemClick: (DestinasiModel) -> Unit
) : RecyclerView.Adapter<DestinasiAdapter.DestinasiViewHolder>() {

    fun updateData(newItems: List<DestinasiModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinasiViewHolder {
        val binding = ItemDestinasiBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DestinasiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DestinasiViewHolder, position: Int) {
        val destinasi = items[position]

        holder.binding.destinasiName.text = destinasi.namaDestinasi
        holder.binding.destinasiPrice.text = "Rp ${String.format("%,.0f", destinasi.harga)}"
        holder.binding.ratingBar.rating = destinasi.averageRating.toFloat()
        holder.binding.destinasiRating.text =
            "${"%.1f".format(destinasi.averageRating)}/5 (${destinasi.totalComments} Reviews)"
        loadImage(destinasi.foto, holder.binding.imageDestinasi)

        holder.binding.root.setOnClickListener {
            onItemClick(destinasi)
        }
    }

    private fun loadImage(picAddress: String, imageView: ImageView) {
        if (picAddress.startsWith("http") || picAddress.startsWith("https")) {
            Picasso.get()
                .load(picAddress)
                .error(R.drawable.bali)
                .into(imageView)
        } else {
            val drawableResourceId =
                context.resources.getIdentifier(picAddress, "drawable", context.packageName)
            imageView.setImageResource(drawableResourceId)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class DestinasiViewHolder(val binding: ItemDestinasiBinding) :
        RecyclerView.ViewHolder(binding.root)
}