package com.example.KitaJalan.data.model

data class DestinasiResponse(
    val cursor: String,
    val items: List<DestinasiModel>,
    val next_page: String,
)

data class DestinasiModel (
    val id: String = "",
    val adminId: String = "",
    val namaDestinasi: String = "",
    val fasilitas: List<String> = emptyList(),
    val foto: String = "",
    val harga: Double = 0.0,
    val lokasi: String = "",
    val kategori: String = "",
    val deskripsi: String = "",
    val averageRating: Double = 0.0,
    val totalComments: Int = 0
)