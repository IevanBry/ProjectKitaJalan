package com.example.KitaJalan.data.repository

import com.example.KitaJalan.data.firebase.FirebaseAuthService
import com.example.KitaJalan.data.model.UserResponse

class FirebaseRepository(private val authService: FirebaseAuthService) {
    suspend fun login(email: String, password: String): UserResponse? {
        val authResult = authService.login(email, password)
        if (authResult != null) {
            return UserResponse(
                id = null,
                name = authResult.displayName ?: "Unknown",
                username = authResult.email?.substringBefore('@') ?: "Unknown",
                email = authResult.email ?: "Unknown"
            )
        }
        return null
    }

    suspend fun register(email: String, password: String): Boolean {
        return authService.register(email, password)
    }

}