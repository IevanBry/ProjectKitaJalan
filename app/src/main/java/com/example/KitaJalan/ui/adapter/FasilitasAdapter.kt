package com.example.KitaJalan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.KitaJalan.databinding.ItemFasilitasBinding

data class Fasilitas(
    val name: String,
    val icon: Int
)

class FasilitasAdapter(private val facilities: List<Fasilitas>) :
    RecyclerView.Adapter<FasilitasAdapter.FacilityViewHolder>() {

    inner class FacilityViewHolder(val binding: ItemFasilitasBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityViewHolder {
        val binding = ItemFasilitasBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FacilityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FacilityViewHolder, position: Int) {
        val facility = facilities[position]
        with(holder.binding) {
            facilityIcon.setImageResource(facility.icon)
            facilityName.text = facility.name
        }
    }

    override fun getItemCount(): Int = facilities.size
}