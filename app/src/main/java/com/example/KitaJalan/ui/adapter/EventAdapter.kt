package com.example.KitaJalan.ui.adapter

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.KitaJalan.R
import com.example.KitaJalan.data.model.EventModel
import com.example.KitaJalan.databinding.ItemEventBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(private var eventList: MutableList<EventModel>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventModel) {
            with(binding) {
                Picasso.get()
                    .load(event.gambarUrl)
                    .error(R.drawable.sample_event)
                    .fit()
                    .centerCrop()
                    .into(imageEvent)

                val dateFormatInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateFormatOutput = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

                val formattedTanggalMulai = try {
                    val date = dateFormatInput.parse(event.tanggalMulai)
                    dateFormatOutput.format(date!!)
                } catch (e: Exception) {
                    event.tanggalMulai
                }

                val formattedTanggalSelesai = try {
                    val date = dateFormatInput.parse(event.tanggalSelesai)
                    dateFormatOutput.format(date!!)
                } catch (e: Exception) {
                    event.tanggalSelesai
                }

                textJudulEvent.text = event.judul
                textDeskripsiEvent.text = event.deskripsi
                textTanggalMulai.text = "Tanggal Mulai: $formattedTanggalMulai"
                textTanggalSelesai.text = "Tanggal Selesai: $formattedTanggalSelesai"

                iconMap.setOnClickListener {
                    showLocationDialog(event.googleMapsUrl)
                }
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
            val context = binding.root.context
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleMapsUrl))
                intent.setPackage("com.google.android.apps.maps")
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    "Terjadi kesalahan saat membuka lokasi",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Terjadi kesalahan yang tidak diketahui",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    override fun getItemCount(): Int = eventList.size

    fun updateData(newEventList: List<EventModel>) {
        eventList.clear()
        eventList.addAll(newEventList)
        notifyDataSetChanged()
    }
}