package com.example.KitaJalan.data.repository

import com.example.KitaJalan.data.model.EventModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EventRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun fetchEvents(): List<EventModel> {
        return try {
            val snapshot = firestore.collection("events").get().await()
            snapshot.documents.map { document ->
                val event = document.toObject(EventModel::class.java)!!
                event.copy(id = document.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun createEvent(events: List<EventModel>) {
        try {
            val batch = firestore.batch()
            events.forEach { event ->
                val docRef = firestore.collection("events").document()
                val eventWithId = event.copy(id = docRef.id)
                batch.set(docRef, eventWithId)
            }
            batch.commit().await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun updateEvent(event: EventModel) {
        try {
            val docRef = firestore.collection("events").document(event.id!!)
            docRef.set(event).await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun deleteEvent(eventId: String) {
        try {
            val docRef = firestore.collection("events").document(eventId)
            docRef.delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}