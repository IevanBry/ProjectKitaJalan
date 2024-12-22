package com.example.KitaJalan.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.data.model.WishlistModel
import com.example.KitaJalan.data.model.WishlistPostRequest
import com.example.KitaJalan.data.model.WishlistResponse
import com.example.KitaJalan.data.repository.DestinasiRepository
import com.example.KitaJalan.data.repository.WishlistRepository
import com.example.KitaJalan.utils.NetworkUtils
import com.example.KitaJalan.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val wishlistRepository: WishlistRepository,
    private val destinasiRepository: DestinasiRepository
) : ViewModel() {

    private val _filteredDestinations = MutableLiveData<Resource<List<DestinasiModel>>>()
    val filteredDestinations: LiveData<Resource<List<DestinasiModel>>> = _filteredDestinations

    private fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun fetchWishlistAndMatchDestinations(context: Context) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            _filteredDestinations.value = Resource.Loading()
            viewModelScope.launch {
                try {
                    val userId = getCurrentUserId()
                    if (userId != null) {
                        val wishlistResponse = wishlistRepository.fetchWishlist(userId)
                        val wishlist = wishlistResponse.items
                        Log.d("WishlistLog", "Wishlist Response: $wishlistResponse")
                        Log.d(
                            "WishlistLog",  "Wishlist Items: ${wishlist.joinToString { it.idDestinasi }}"
                        )

                        if (wishlist.isEmpty()) {
                            _filteredDestinations.postValue(Resource.Empty("No Wishlist Found"))
                        } else {
                            val destinasiResponse = destinasiRepository.fetchDestination()
                            val destinations = destinasiResponse.items

                            val matchedDestinations = filterDestinationsByWishlist(destinations, wishlist)

                            if (matchedDestinations.isEmpty()) {
                                _filteredDestinations.postValue(Resource.Empty("No Destinations Found in Wishlist"))
                            } else {
                                _filteredDestinations.postValue(Resource.Success(matchedDestinations))
                            }
                        }
                    } else {
                        _filteredDestinations.postValue(Resource.Error("User not logged in"))
                    }
                } catch (e: Exception) {
                    Log.e("WishlistLog", "Error: ${e.message}")
                    _filteredDestinations.postValue(Resource.Error("Unknown Error: ${e.message}"))
                }
            }
        } else {
            _filteredDestinations.postValue(Resource.Error("No Internet Connection"))
        }
    }
    private fun filterDestinationsByWishlist(
        destinations: List<DestinasiModel>,
        wishlist: List<WishlistModel>
    ): List<DestinasiModel> {
        val currentUserId = getCurrentUserId()

        if (currentUserId == null) {
            Log.e("MatchLog", "Error: Current user is not logged in")
            return emptyList()
        }

        val matchedDestinations = destinations.filter { destination ->
            val isMatched = wishlist.any { wishlistItem ->
                val idDestinasi = wishlistItem.idDestinasi
                val userId = wishlistItem.userId
                Log.d(
                    "MatchDebug",
                    "Comparing Wishlist ID: $idDestinasi with Destinasi UUID: ${destination._uuid} and Wishlist UserID: $userId with Current UserID: $currentUserId"
                )
                idDestinasi == destination._uuid && userId == currentUserId
            }

            if (isMatched) {
                Log.d(
                    "MatchLog",
                    "Matched Destinasi: ${destination.namaDestinasi} with Wishlist ID: ${destination._uuid}"
                )
            } else {
                Log.d(
                    "MatchLog",
                    "Unmatched Destinasi: ${destination.namaDestinasi} with Wishlist ID: ${destination._uuid}"
                )
            }
            isMatched
        }

        Log.d("MatchLog", "Total Matched Destinations: ${matchedDestinations.size}")
        return matchedDestinations
    }

    fun addToWishlist(context: Context, wishlistRequests: List<WishlistPostRequest>, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    val response = wishlistRepository.addWishlist(wishlistRequests)
                    Log.d("WishlistLog", "Wishlist added successfully: ${response.items}")
                    onSuccess()
                } catch (e: Exception) {
                    Log.e("WishlistLog", "Error adding wishlist: ${e.message}")
                    onError(e.message ?: "Unknown error")
                }
            }
        } else {
            onError("No Internet Connection")
        }
    }

    fun isFavorited(uuid: String, onResult: (Boolean) -> Unit, onError: (String) -> Unit) {
        val userId = getCurrentUserId()
        if (userId.isNullOrEmpty()) {
            onError("User not logged in")
            return
        }

        viewModelScope.launch {
            try {
                val wishlistResponse = wishlistRepository.fetchWishlist(userId)
                val wishlist = wishlistResponse.items

                val isFavorited = wishlist.any { it.idDestinasi == uuid }
                Log.d("WishlistLog", "Is Favorited Check: $uuid -> $isFavorited")
                onResult(isFavorited)
            } catch (e: Exception) {
                Log.e("WishlistLog", "Error checking favorite status: ${e.message}")
                onError(e.message ?: "Unknown error")
            }
        }
    }
}