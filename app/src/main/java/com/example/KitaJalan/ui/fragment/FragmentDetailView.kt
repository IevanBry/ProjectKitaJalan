package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.KitaJalan.databinding.FragmentDetailViewBinding

class FragmentDetailView : Fragment() {

    private lateinit var binding: FragmentDetailViewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailViewBinding.inflate(inflater, container, false)

        val args = arguments
        binding.textView4.text = args?.getString("namaDestinasi")
        binding.descriptionText.text = args?.getString("deskripsi")
        binding.locationText.text = args?.getString("lokasi")
        binding.priceText.text = args?.getString("harga")

        return binding.root
    }

}