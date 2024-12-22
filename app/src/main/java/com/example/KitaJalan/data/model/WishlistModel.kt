package com.example.KitaJalan.data.model

data class WishlistResponse(
    val cursor: String,
    val items: List<WishlistModel>,
    val next_page: String,
)

data class WishlistModel(
    val idWishlist: String = "",
    val idDestinasi: String = "",
    val tanggalDitambahkan: String = "",
    val userId: String = ""
)

data class WishlistPostRequest(
    val idDestinasi: String,
    val tanggalDitambahkan: String,
    val userId: String
)