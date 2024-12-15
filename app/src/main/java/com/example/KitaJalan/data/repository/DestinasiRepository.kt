package com.example.KitaJalan.data.repository

import com.example.KitaJalan.data.model.DestinasiPostRequest
import com.example.KitaJalan.data.model.DestinasiResponse
import com.example.KitaJalan.data.network.ApiService

class DestinasiRepository( private val api: ApiService) {
    private val tokenBearer = "Bearer PcsPAEEisvYRT1l85x9n_02CzP_cNb9zNY-hpOtXmJ_1kxJqAw"

    suspend fun fetchDestination(): DestinasiResponse {
        return api.getDestinasi(tokenBearer)
    }

    suspend fun createDestination(destinasi: List<DestinasiPostRequest>): DestinasiResponse {
        return api.createDestinasi(tokenBearer, destinasi)
    }
}
