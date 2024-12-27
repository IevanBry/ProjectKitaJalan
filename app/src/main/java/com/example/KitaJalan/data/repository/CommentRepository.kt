package com.example.KitaJalan.data.repository

import com.example.KitaJalan.data.model.CommentModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class CommentRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun fetchKomentarByDestinasiId(destinasiId: String): List<CommentModel> {
        return try {
            val snapshot = firestore.collection("komentar")
                .whereEqualTo("destinasiId", destinasiId)
                .get()
                .await()

            snapshot.documents.mapNotNull { document ->
                document.toObject(CommentModel::class.java)?.copy(
                    destinasiId = document.id
                )
            }.sortedByDescending { comment ->
                SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).parse(comment.tanggal)?.time
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addKomentar(komentar: CommentModel): String {
        return try {
            val docRef = firestore.collection("komentar").add(komentar).await()
            docRef.id
        } catch (e: Exception) {
            throw e
        }
    }
}