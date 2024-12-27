package com.example.KitaJalan.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.KitaJalan.data.model.CommentModel
import com.example.KitaJalan.data.repository.CommentRepository
import com.example.KitaJalan.utils.Resource
import kotlinx.coroutines.launch

class CommentViewModel(private val repository: CommentRepository) : ViewModel() {

    private val _comments = MutableLiveData<Resource<List<CommentModel>>>()
    val comments: LiveData<Resource<List<CommentModel>>> = _comments

    private val _addCommentStatus = MutableLiveData<Resource<Unit>>()
    val addCommentStatus: LiveData<Resource<Unit>> = _addCommentStatus

    fun fetchComments(destinasiId: String) {
        _comments.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val comments = repository.fetchKomentarByDestinasiId(destinasiId)
                if (comments.isEmpty()) {
                    _comments.postValue(Resource.Empty("No Comments Found"))
                } else {
                    _comments.postValue(Resource.Success(comments))
                }
            } catch (e: Exception) {
                _comments.postValue(Resource.Error("Error: ${e.message}"))
            }
        }
    }

    // Add a new comment
    fun addComment(komentar: CommentModel) {
        _addCommentStatus.value = Resource.Loading()
        viewModelScope.launch {
            try {
                repository.addKomentar(komentar)
                _addCommentStatus.postValue(Resource.Success(Unit))
            } catch (e: Exception) {
                _addCommentStatus.postValue(Resource.Error("Error: ${e.message}"))
            }
        }
    }

    fun calculateAverageRating(destinasiId: String, callback: (Double) -> Unit) {
        viewModelScope.launch {
            try {
                val comments = repository.fetchKomentarByDestinasiId(destinasiId)
                val averageRating = if (comments.isNotEmpty()) {
                    comments.map { it.rating }.average()
                } else {
                    0.0
                }
                callback(averageRating)
            } catch (e: Exception) {
                callback(0.0)
            }
        }
    }

}