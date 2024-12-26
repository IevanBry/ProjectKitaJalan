package com.example.KitaJalan.data.repository

import com.example.KitaJalan.data.model.DestinasiModel
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DestinasiRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun fetchDestination(): List<DestinasiModel> {
        return try {
            val snapshot = firestore.collection("destinasi").get().await()
            snapshot.documents.map { document ->
                val destinasi = document.toObject(DestinasiModel::class.java)!!
                destinasi.copy(id = document.id)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun createDestination(destinasi: List<DestinasiModel>) {
        try {
            val batch = firestore.batch()
            destinasi.forEach { destinasiItem ->
                val docRef = firestore.collection("destinasi").document()
                batch.set(docRef, destinasiItem)
            }
            batch.commit().await()
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
}