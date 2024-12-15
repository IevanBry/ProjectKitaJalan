package com.example.KitaJalan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.EventModel
import com.example.KitaJalan.databinding.ItemEventAdminBinding
import com.squareup.picasso.Picasso

class EventAdminAdapter(
    private val events: List<EventModel>
) : RecyclerView.Adapter<EventAdminAdapter.EventViewHolder>() {

    class EventViewHolder(private val binding: ItemEventAdminBinding) :
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
        }
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