package com.example.KitaJalan.ui.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.data.repository.DestinasiRepository
import com.example.KitaJalan.utils.NetworkUtils
import com.example.KitaJalan.utils.Resource
import kotlinx.coroutines.launch

class DestinasiViewModel(private val repository: DestinasiRepository) : ViewModel() {

    private val _data = MutableLiveData<Resource<List<DestinasiModel>>>()
    val data: LiveData<Resource<List<DestinasiModel>>> = _data

    private val _createStatus = MutableLiveData<Resource<Unit>>()
    val createStatus: LiveData<Resource<Unit>> = _createStatus

    private val _deleteStatus = MutableLiveData<Resource<Unit>>()
    val deleteStatus: LiveData<Resource<Unit>> = _deleteStatus

    private val _wishlistStatus = MutableLiveData<Resource<Unit>>()
    val wishlistStatus: LiveData<Resource<Unit>> = _wishlistStatus

    private val _wishlistData = MutableLiveData<Resource<List<String>>>()
    val wishlistData: LiveData<Resource<List<String>>> = _wishlistData

    private var cachedData: List<DestinasiModel> = emptyList()

    fun getDestinasi(context: Context, forceRefresh: Boolean = false) {
        if (_data.value == null || forceRefresh) {
            _data.value = Resource.Loading()
            if (NetworkUtils.isNetworkAvailable(context)) {
                viewModelScope.launch {
                    try {
                        val response = repository.fetchDestination()
                        if (response.isEmpty()) {
                            _data.postValue(Resource.Empty("No Data Found"))
                        } else {
                            cachedData = response
                            _data.postValue(Resource.Success(response))
                        }
                    } catch (e: Exception) {
                        _data.postValue(Resource.Error("Unknown Error : ${e.message}"))
                    }
                }
            } else {
                _data.postValue(Resource.Error("No Internet Connection"))
            }
        }
    }

    fun addDestinasi(context: Context, destinasi: List<DestinasiModel>) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    _createStatus.value = Resource.Loading()
                    repository.createDestination(destinasi)
                    _createStatus.postValue(Resource.Success(Unit))
                    getDestinasi(context, forceRefresh = true)
                } catch (e: Exception) {
                    _createStatus.postValue(Resource.Error("Unknown error: ${e.message}"))
                }
            }
        } else {
            _createStatus.postValue(Resource.Error("No Internet Connection"))
        }
    }

    fun updateDestinasi(context: Context, destinasi: DestinasiModel) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    _createStatus.value = Resource.Loading()
                    repository.updateDestination(destinasi)
                    _createStatus.postValue(Resource.Success(Unit))
                    getDestinasi(context, forceRefresh = true)
                } catch (e: Exception) {
                    _createStatus.postValue(Resource.Error("Error: ${e.message}"))
                }
            }
        } else {
            _createStatus.postValue(Resource.Error("No Internet Connection"))
        }
    }

    fun deleteDestinasi(context: Context, destinasiId: String) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    _deleteStatus.value = Resource.Loading()
                    repository.deleteDestination(destinasiId)
                    _deleteStatus.postValue(Resource.Success(Unit))
                    getDestinasi(context, forceRefresh = true)
                } catch (e: Exception) {
                    _deleteStatus.postValue(Resource.Error("Error: ${e.message}"))
                }
            }
        } else {
            _deleteStatus.postValue(Resource.Error("No Internet Connection"))
        }
    }

    fun getWishlist(context: Context, userId: String) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModelScope.launch {
                _wishlistData.value = Resource.Loading()
                try {
                    val wishlist = repository.getWishlist(userId)
                    _wishlistData.postValue(Resource.Success(wishlist))
                } catch (e: Exception) {
                    _wishlistData.postValue(Resource.Error("Error: ${e.message}"))
                }
            }
        } else {
            _wishlistData.postValue(Resource.Error("No Internet Connection"))
        }
    }

    fun addToWishlist(context: Context, userId: String, destinasiId: String) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            _wishlistStatus.value = Resource.Loading()
            viewModelScope.launch {
                try {
                    repository.addToWishlist(userId, destinasiId)
                    getWishlist(context, userId)
                    _wishlistStatus.postValue(Resource.Success(Unit))
                } catch (e: Exception) {
                    _wishlistStatus.postValue(Resource.Error("Failed to add to wishlist: ${e.message}"))
                }
            }
        } else {
            _wishlistStatus.postValue(Resource.Error("No Internet Connection"))
        }
    }

    fun removeFromWishlist(context: Context, userId: String, destinasiId: String) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            _wishlistStatus.value = Resource.Loading()
            viewModelScope.launch {
                try {
                    repository.removeFromWishlist(userId, destinasiId)
                    getWishlist(context, userId)
                    _wishlistStatus.postValue(Resource.Success(null))
                } catch (e: Exception) {
                    _wishlistStatus.postValue(Resource.Error("Failed to remove from wishlist: ${e.message}"))
                }
            }
        } else {
            _wishlistStatus.postValue(Resource.Error("No Internet Connection"))
        }
    }
}
