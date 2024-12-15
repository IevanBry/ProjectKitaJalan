package com.example.KitaJalan.data.network

import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.data.model.DestinasiPostRequest
import com.example.KitaJalan.data.model.DestinasiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

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
}