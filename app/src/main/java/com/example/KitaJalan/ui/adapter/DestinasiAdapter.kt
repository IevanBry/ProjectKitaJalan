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
    private val context: Context
) : RecyclerView.Adapter<DestinasiAdapter.DestinasiViewHolder>() {

    fun updateData(newItems: List<DestinasiModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun getAllData(): List<DestinasiModel> {
        return items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinasiViewHolder {
        val binding = ItemDestinasiBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DestinasiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DestinasiViewHolder, position: Int) {
        val trend = items[position]

        holder.binding.destinasiName.text = trend.namaDestinasi
        loadImage(trend.foto, holder.binding.imageDestinasi)

        holder.itemView.setOnClickListener {
        }
    }

    private fun loadImage(picAddress: String, imageView: ImageView) {
        if (picAddress.startsWith("http") || picAddress.startsWith("https")) {
            Picasso.get()
                .load(picAddress)
                .error(R.drawable.logo)  // Default image in case of error
                .into(imageView)
        } else {
            val drawableResourceId = context.resources.getIdentifier(picAddress, "drawable", context.packageName)
            imageView.setImageResource(drawableResourceId)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class DestinasiViewHolder(val binding: ItemDestinasiBinding) : RecyclerView.ViewHolder(binding.root)
}