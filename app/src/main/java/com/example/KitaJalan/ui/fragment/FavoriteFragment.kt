package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.ui.adapter.FavoriteAdapter
import com.example.KitaJalan.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var favoriteList: List<DestinasiModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        favoriteList = getFavoriteData()

        favoriteAdapter = FavoriteAdapter(requireContext(), favoriteList)

        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavorites.adapter = favoriteAdapter

        return binding.root
    }

    private fun getFavoriteData(): List<DestinasiModel> {
        return listOf(
            DestinasiModel(namaDestinasi = "Destinasi 1", foto = "https://example.com/image1.jpg"),
            DestinasiModel(namaDestinasi = "Destinasi 2", foto = "https://example.com/image2.jpg"),
            DestinasiModel(namaDestinasi = "Destinasi 3", foto = "https://example.com/image3.jpg")
        )
    }
}
