package com.example.KitaJalan.data.model

data class DestinasiModel(
    val id: String = "",
    val adminId: String = "",
    val namaDestinasi: String = "",
    val fasilitas: List<String> = emptyList(),
    val foto: String = "",
    val harga: Double = 0.0,
    val lokasi: String = "",
    val noHp: String = "",
    val kategori: String = "",
    val deskripsi: String = "",
    val averageRating: Double = 0.0,
    val totalComments: Int = 0
)