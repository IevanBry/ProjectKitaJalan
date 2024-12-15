package com.example.KitaJalan.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.KitaJalan.data.model.DestinasiPostRequest
import com.example.KitaJalan.data.model.DestinasiResponse
import com.example.KitaJalan.data.repository.DestinasiRepository
import com.example.KitaJalan.utils.NetworkUtils
import com.example.KitaJalan.utils.Resource
import kotlinx.coroutines.launch

class DestinasiViewModel(private val repository: DestinasiRepository) : ViewModel() {

    private val _data = MutableLiveData<Resource<DestinasiResponse>>()
    val data: LiveData<Resource<DestinasiResponse>> = _data

    private val _createStatus = MutableLiveData<Resource<Unit>>()
    val createStatus: LiveData<Resource<Unit>> = _createStatus

    fun getDestinasi(context: Context, forceRefresh: Boolean = false) {
        if (data.value == null || forceRefresh) {
            _data.value = Resource.Loading()
            if (NetworkUtils.isNetworkAvailable(context)) {
                viewModelScope.launch {
                    try {
                        val response = repository.fetchDestination()
                        if (response.items.isEmpty()) {
                            _data.postValue(Resource.Empty("No Data Found"))
                        } else {
                            _data.postValue(Resource.Success(response))
                        }
                    } catch (e: Exception) {
                        _data.postValue(Resource.Error("Unknown Error : ${e.message}"))
                    }
                }
            } else {
                _data.postValue(Resource.Error("No Internet Connection 2"))
            }
        }
    }

    fun addDestinasi(context: Context, destinasi: List<DestinasiPostRequest>) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    _createStatus.value = Resource.Loading()

                    val response = repository.createDestination(destinasi)
                    _createStatus.postValue(Resource.Success(Unit))

                    getDestinasi(context, forceRefresh = true)

                }catch (e: Exception) {
                    _data.postValue(Resource.Error("Unknown error: ${e.message}"))
                }
            }
        } else {
            _createStatus.postValue(Resource.Error("No Internet Connection"))
        }
    }
}
