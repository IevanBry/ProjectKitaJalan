package com.example.KitaJalan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.KitaJalan.databinding.SlideItemBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AutoSliderAdapter(
    private val images: List<Any>,
    private val viewPager: ViewPager2
) : RecyclerView.Adapter<AutoSliderAdapter.SliderViewHolder>() {
    private var currentPosition = 0

    init {
        startAutoSlider()
    }

    class SliderViewHolder(val binding: SlideItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = SlideItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val image = images[position]

        if (image is String) {
            Picasso.get()
                .load(image)
                .into(holder.binding.imageSlider)
        } else if (image is Int) {
            holder.binding.imageSlider.setImageResource(image)
        }
    }

    private fun startAutoSlider() {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(2000)
                currentPosition = (currentPosition + 1) % itemCount
                viewPager.setCurrentItem(currentPosition, true)
            }
        }
    }
}
