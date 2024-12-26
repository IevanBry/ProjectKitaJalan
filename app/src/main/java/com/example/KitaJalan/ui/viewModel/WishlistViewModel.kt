import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.KitaJalan.data.model.DestinasiModel
import com.example.KitaJalan.data.repository.DestinasiRepository
import com.example.KitaJalan.data.repository.WishlistRepository
import com.example.KitaJalan.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val wishlistRepository: WishlistRepository,
    private val destinasiRepository: DestinasiRepository
) : ViewModel() {

    private val _filteredDestinations = MutableLiveData<Resource<List<DestinasiModel>>>()
    val filteredDestinations: LiveData<Resource<List<DestinasiModel>>> = _filteredDestinations

    fun fetchWishlistAndMatchDestinations(context: Context) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        _filteredDestinations.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val wishlistIds = wishlistRepository.getWishlist(userId)
                if (wishlistIds.isEmpty()) {
                    _filteredDestinations.postValue(Resource.Empty("No wishlist items found."))
                } else {
                    val destinations = destinasiRepository.getDestinationsByIds(wishlistIds)
                    _filteredDestinations.postValue(Resource.Success(destinations))
                }
            } catch (e: Exception) {
                _filteredDestinations.postValue(Resource.Error("Error fetching wishlist: ${e.message}"))
            }
        }
    }
}