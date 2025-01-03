package com.example.KitaJalan.data.network

import com.example.KitaJalan.data.model.DestinasiPostRequest
import com.example.KitaJalan.data.model.DestinasiResponse
import com.example.KitaJalan.data.model.WishlistPostRequest
import com.example.KitaJalan.data.model.WishlistResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("destinasi")
    suspend fun createDestinasi(
        @Header("Authorization") token: String,
        @Body destinasi: List<DestinasiPostRequest>,
    ): DestinasiResponse

    @GET("destinasi")
    suspend fun getDestinasi(
        @Header("Authorization") token: String
    ): DestinasiResponse

    @GET("destinasi/{uuid}")
    suspend fun getDestinasiByUuid(
        @Header("Authorization") token: String,
        @Path("uuid") uuid: String
    ): DestinasiResponse

    @GET("wishlist")
    suspend fun getWishlist(
        @Header("Authorization") token: String,
        @Query("userId") userId: String
    ): WishlistResponse

    @POST("wishlist")
    suspend fun addWishlist(
        @Header("Authorization") token: String,
        @Body wishlist: List<WishlistPostRequest>,
    ): WishlistResponse
}