package com.example.KitaJalan.ui.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.KitaJalan.data.model.EventModel
import com.example.KitaJalan.data.repository.EventRepository
import com.example.KitaJalan.utils.NetworkUtils
import com.example.KitaJalan.utils.Resource
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _data = MutableLiveData<Resource<List<EventModel>>>()
    val data: LiveData<Resource<List<EventModel>>> = _data

    private val _createStatus = MutableLiveData<Resource<Unit>>()
    val createStatus: LiveData<Resource<Unit>> = _createStatus

    private val _deleteStatus = MutableLiveData<Resource<Unit>>()
    val deleteStatus: LiveData<Resource<Unit>> = _deleteStatus

    private var cachedData: List<EventModel> = emptyList()

    fun getEvents(context: Context, forceRefresh: Boolean = false) {
        if (_data.value == null || forceRefresh) {
            _data.value = Resource.Loading()
            if (NetworkUtils.isNetworkAvailable(context)) {
                viewModelScope.launch {
                    try {
                        val response = repository.fetchEvents()
                        if (response.isEmpty()) {
                            _data.postValue(Resource.Empty("No Events Found"))
                        } else {
                            cachedData = response
                            _data.postValue(Resource.Success(response))
                        }
                    } catch (e: Exception) {
                        _data.postValue(Resource.Error("Unknown Error: ${e.message}"))
                    }
                }
            } else {
                _data.postValue(Resource.Error("No Internet Connection"))
            }
        }
    }

    fun addEvent(context: Context, events: List<EventModel>) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    _createStatus.value = Resource.Loading()
                    repository.createEvent(events)
                    _createStatus.postValue(Resource.Success(Unit))
                    getEvents(context, forceRefresh = true)
                } catch (e: Exception) {
                    _createStatus.postValue(Resource.Error("Unknown error: ${e.message}"))
                }
            }
        } else {
            _createStatus.postValue(Resource.Error("No Internet Connection"))
        }
    }

    fun updateEvent(context: Context, event: EventModel) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    _createStatus.value = Resource.Loading()
                    repository.updateEvent(event)
                    _createStatus.postValue(Resource.Success(Unit))
                    getEvents(context, forceRefresh = true)
                } catch (e: Exception) {
                    _createStatus.postValue(Resource.Error("Error: ${e.message}"))
                }
            }
        } else {
            _createStatus.postValue(Resource.Error("No Internet Connection"))
        }
    }

    fun deleteEvent(context: Context, eventId: String) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    _deleteStatus.value = Resource.Loading()
                    repository.deleteEvent(eventId)
                    _deleteStatus.postValue(Resource.Success(Unit))
                    getEvents(context, forceRefresh = true)
                } catch (e: Exception) {
                    _deleteStatus.postValue(Resource.Error("Error: ${e.message}"))
                }
            }
        } else {
            _deleteStatus.postValue(Resource.Error("No Internet Connection"))
        }
    }
}