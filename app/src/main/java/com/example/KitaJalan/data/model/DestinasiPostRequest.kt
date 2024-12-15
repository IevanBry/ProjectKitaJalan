package com.example.KitaJalan.data.model

class DestinasiPostRequest(
    val namaDestinasi: String,
    val fasilitas: List<String> ,
    val foto: String,
    val harga: Double,
    val lokasi: String,
    val kategori: String,
    val deskripsi: String
)