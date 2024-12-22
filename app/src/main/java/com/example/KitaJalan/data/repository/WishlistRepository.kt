package com.example.KitaJalan.data.repository

import com.example.KitaJalan.data.model.WishlistPostRequest
import com.example.KitaJalan.data.model.WishlistResponse
import com.example.KitaJalan.data.network.ApiService

class WishlistRepository(private val api: ApiService) {
    private val tokenBearer = "Bearer PcsPAEEisvYRT1l85x9n_02CzP_cNb9zNY-hpOtXmJ_1kxJqAw"

    suspend fun fetchWishlist(userId: String): WishlistResponse {
        return api.getWishlist(tokenBearer, userId)
    }

    suspend fun addWishlist(wishlistRequests: List<WishlistPostRequest>): WishlistResponse {
        return api.addWishlist(tokenBearer, wishlistRequests)
    }
}
