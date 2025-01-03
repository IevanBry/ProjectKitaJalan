package com.example.KitaJalan.ui.adapter

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.EventModel
import com.example.KitaJalan.databinding.ItemEventAdminBinding
import com.squareup.picasso.Picasso

class EventAdminAdapter(
    private var events: List<EventModel>,
    private val onEditClick: (EventModel) -> Unit,
    private val onDeleteClick: (EventModel) -> Unit
) : RecyclerView.Adapter<EventAdminAdapter.EventViewHolder>() {

    inner class EventViewHolder(private val binding: ItemEventAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventModel) {
            binding.judulEvent.text = event.judul
            binding.deskripsiEvent.text = event.deskripsi
            binding.tanggalMulai.text = "Mulai: ${event.tanggalMulai}"
            binding.tanggalSelesai.text = "Selesai: ${event.tanggalSelesai}"

            Picasso.get()
                .load(event.gambarUrl)
                .error(R.drawable.sample_event)
                .into(binding.gambarEvent)

            binding.iconLocation.setOnClickListener {
                showLocationDialog(event.googleMapsUrl)
            }

            binding.iconEdit.setOnClickListener {
                onEditClick(event)
            }

            binding.iconDelete.setOnClickListener {
                onDeleteClick(event)
            }
        }

        private fun showLocationDialog(googleMapsUrl: String) {
            val context = binding.root.context
            AlertDialog.Builder(context)
                .setTitle("Buka Lokasi")
                .setMessage("Apakah Anda ingin membuka lokasi ini di Google Maps?")
                .setPositiveButton("Ya") { _, _ ->
                    openGoogleMaps(googleMapsUrl)
                }
                .setNegativeButton("Tidak", null)
                .show()
        }

        private fun openGoogleMaps(googleMapsUrl: String) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleMapsUrl))
                binding.root.context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateData(newEvents: List<EventModel>) {
        this.events = newEvents
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding =
            ItemEventAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size
}