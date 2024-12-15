package com.example.KitaJalan.ui.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.KitaJalan.data.model.UserResponse
import com.example.KitaJalan.data.repository.FirebaseRepository
import com.example.KitaJalan.utils.NetworkUtils
import com.example.KitaJalan.utils.Resource
import kotlinx.coroutines.launch

class FirebaseViewModel(private val repository: FirebaseRepository) : ViewModel() {

    private val _loginState = MutableLiveData<Resource<UserResponse>>()
    val loginState: LiveData<Resource<UserResponse>> = _loginState

    fun login(context: Context, email: String, password: String) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModelScope.launch {
                try {
                    _loginState.value = Resource.Loading()
                    val user = repository.login(email, password)
                    if (user != null) {
                        _loginState.value = Resource.Success(user)
                    } else {
                        _loginState.value =
                            Resource.Error("Login gagal. Periksa kembali kredensial Anda.")
                    }
                } catch (e: Exception) {
                    _loginState.value =
                        Resource.Error(e.message ?: "Terjadi kesalahan saat login")
                }
            }
        } else {
            _loginState.postValue(Resource.Error("No Internet Connection"))
        }
    }
}
