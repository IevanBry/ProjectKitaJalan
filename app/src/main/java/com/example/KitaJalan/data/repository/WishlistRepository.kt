package com.example.KitaJalan.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WishlistRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getWishlist(userId: String): List<String> {
        return try {
            val snapshot = firestore.collection("wishlist").document(userId).get().await()
            snapshot["destinations"] as? List<String> ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

