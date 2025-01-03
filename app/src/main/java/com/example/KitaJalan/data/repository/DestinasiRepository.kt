package com.example.KitaJalan.data.repository

import com.example.KitaJalan.data.model.CommentModel
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.data.model.DestinasiPostRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DestinasiRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun fetchDestination(): List<DestinasiModel> {
        return try {
            val snapshot = firestore.collection("destinasi").get().await()
            val destinasiList = snapshot.documents.map { document ->
                val destinasi = document.toObject(DestinasiModel::class.java)!!
                destinasi.copy(id = document.id)
            }

            destinasiList.map { destinasi ->
                val comments = fetchCommentsByDestinasiId(destinasi.id)
                destinasi.copy(
                    averageRating = if (comments.isNotEmpty()) {
                        comments.map { it.rating }.average()
                    } else {
                        0.0
                    },
                    totalComments = comments.size
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun fetchCommentsByDestinasiId(destinasiId: String): List<CommentModel> {
        return try {
            val snapshot = firestore.collection("komentar")
                .whereEqualTo("destinasiId", destinasiId)
                .get().await()
            snapshot.documents.map { document ->
                document.toObject(CommentModel::class.java)!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun createDestination(destinasi: List<DestinasiPostRequest>) {
        try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val adminId = currentUser?.uid ?: throw IllegalStateException("User not logged in")

            val batch = firestore.batch()
            destinasi.forEach { destinasiItem ->
                val docRef = firestore.collection("destinasi").document()
                val destinasiWithAdminId = destinasiItem.copy(
                    id = docRef.id,
                    adminId = adminId
                )
                batch.set(docRef, destinasiWithAdminId)
            }
            batch.commit().await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateDestination(destinasi: DestinasiPostRequest) {
        try {
            val docRef = firestore.collection("destinasi").document(destinasi.id!!)
            docRef.set(destinasi).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteDestination(destinasiId: String) {
        try {
            val docRef = firestore.collection("destinasi").document(destinasiId)
            docRef.delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getWishlist(userId: String): List<String> {
        return try {
            val snapshot = firestore.collection("wishlist").document(userId).get().await()
            snapshot["destinations"] as? List<String> ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDestinationsByIds(ids: List<String>): List<DestinasiModel> {
        return try {
            val snapshot = firestore.collection("destinasi")
                .whereIn(FieldPath.documentId(), ids)
                .get().await()
            snapshot.documents.map { document ->
                val destinasi = document.toObject(DestinasiModel::class.java)!!
                destinasi.copy(id = document.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addToWishlist(userId: String, destinasiId: String) {
        try {
            val wishlistRef = FirebaseFirestore.getInstance().collection("wishlist").document(userId)
            FirebaseFirestore.getInstance().runTransaction { transaction ->
                val snapshot = transaction.get(wishlistRef)
                val currentList = snapshot["destinations"] as? List<String> ?: emptyList()
                if (!currentList.contains(destinasiId)) {
                    val updatedList = currentList.toMutableList().apply { add(destinasiId) }
                    transaction.set(wishlistRef, mapOf("destinations" to updatedList))
                }
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun removeFromWishlist(userId: String, destinasiId: String) {
        try {
            val wishlistRef = FirebaseFirestore.getInstance().collection("wishlist").document(userId)
            FirebaseFirestore.getInstance().runTransaction { transaction ->
                val snapshot = transaction.get(wishlistRef)
                val currentList = snapshot["destinations"] as? List<String> ?: emptyList()
                if (currentList.contains(destinasiId)) {
                    val updatedList = currentList.toMutableList().apply { remove(destinasiId) }
                    transaction.set(wishlistRef, mapOf("destinations" to updatedList))
                }
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }
}