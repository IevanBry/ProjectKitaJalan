package com.example.KitaJalan.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.databinding.ViewholderTrendBinding
import com.squareup.picasso.Picasso

class TrendsAdapter(
    private var items: List<DestinasiModel>,
    private val context: Context,
    private val onItemClick: (DestinasiModel) -> Unit
) : RecyclerView.Adapter<TrendsAdapter.TrendViewHolder>() {

    fun updateData(newItems: List<DestinasiModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendViewHolder {
        val binding = ViewholderTrendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TrendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrendViewHolder, position: Int) {
        val trend = items[position]

        holder.binding.title.text = trend.namaDestinasi
        holder.binding.subtitle.text = trend.deskripsi
        loadImage(trend.foto, holder.binding.pic)

        holder.itemView.setOnClickListener {
            onItemClick(trend)
        }
    }

    private fun loadImage(picAddress: String, imageView: ImageView) {
        if (picAddress.startsWith("http") || picAddress.startsWith("https")) {
            Picasso.get()
                .load(picAddress)
                .error(R.drawable.bali)
                .into(imageView)
        } else {
            val drawableResourceId = context.resources.getIdentifier(picAddress, "drawable", context.packageName)
            imageView.setImageResource(drawableResourceId)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class TrendViewHolder(val binding: ViewholderTrendBinding) : RecyclerView.ViewHolder(binding.root)
}