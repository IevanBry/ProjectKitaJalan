package com.example.KitaJalan.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.databinding.ItemDestinasiBinding
import com.example.KitaJalan.ui.viewModel.CommentViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class DestinasiAdapter(
    private var items: List<DestinasiModel>,
    private val context: Context,
    private val commentViewModel: CommentViewModel,
    private val lifecycleOwner: LifecycleOwner,
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

        fetchAndDisplayRating(destinasi.id, holder)
        holder.binding.destinasiName.text = destinasi.namaDestinasi
        holder.binding.destinasiPrice.text = "Rp ${String.format("%,.0f", destinasi.harga)}"
        loadImage(destinasi.foto, holder.binding.imageDestinasi)

        holder.binding.root.setOnClickListener {
            onItemClick(destinasi)
        }
    }

    private fun fetchAndDisplayRating(destinasiId: String, holder: DestinasiViewHolder) {
        lifecycleOwner.lifecycleScope.launch {
            commentViewModel.calculateAverageRating(destinasiId) { averageRating ->
                holder.binding.ratingBar.rating = averageRating.toFloat()
                holder.binding.destinasiRating.text = "${"%.1f".format(averageRating)}/5"
            }

            commentViewModel.getTotalComments(destinasiId) { totalComments ->
                val currentText = holder.binding.destinasiRating.text.toString()
                holder.binding.destinasiRating.text = "$currentText ($totalComments Reviews)"
            }
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