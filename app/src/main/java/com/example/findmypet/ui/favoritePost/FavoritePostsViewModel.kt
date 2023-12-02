package com.example.findmypet.ui.favoritePost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.GetFavoritePostsUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.RemovePostFromFavoriteUseCase
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class FavoritePostsViewModel @Inject constructor(
    private val getFavoritePostsUseCase: GetFavoritePostsUseCase
    ,private val removePostFromFavoriteUseCase: RemovePostFromFavoriteUseCase
) : ViewModel() {

    private val _removeFaveSharedFlow = MutableSharedFlow<Resource<Unit>>()
    val removeFaveSharedFlow: SharedFlow<Resource<Unit>> = _removeFaveSharedFlow.asSharedFlow()

    private val _favoritePosts = MutableStateFlow<Resource<List<Post>>>(Resource.Loading)
    val favoritePosts: StateFlow<Resource<List<Post>>> = _favoritePosts


    init {
        fetchFavoritePosts()
    }



    fun fetchFavoritePosts() {
        viewModelScope.launch {
            try {
                val result = getFavoritePostsUseCase.execute()
                    .collect { resource ->
                        _favoritePosts.emit(resource) // Emit the collected value into MutableStateFlow
                    }
            } catch (e: UnknownHostException) {
                _favoritePosts.emit(Resource.Error(Exception("Network unavailable. Please check your internet connection.")))
            } catch (e: Exception) {
                _favoritePosts.emit(Resource.Error(e))
            }
        }
    }



    fun removeFav(postId: String) {
        viewModelScope.launch {
            try {
                _removeFaveSharedFlow.emit(Resource.Loading)
                val result = removePostFromFavoriteUseCase.execute(postId)
                _removeFaveSharedFlow.emit(result)
            } catch (e: Exception) {
                if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                    _removeFaveSharedFlow.emit(Resource.Error(e))
                } else {
                    _removeFaveSharedFlow.emit(Resource.Error(e))
                }
            }
        }
    }
}