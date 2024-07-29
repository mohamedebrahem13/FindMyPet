package com.example.findmypet.ui.allposts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.AddPostToFavoriteUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.GetPostsUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.RemovePostFromFavoriteUseCase
import com.example.findmypet.domain.usecase.firebaseUseCase.posts.SearchPostsByNameUseCase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllPostsViewModel @Inject constructor(private val getPostsUseCase: GetPostsUseCase,
                                            private val searchPostsByNameUseCase: SearchPostsByNameUseCase,
                                            private val addPostToFavoriteUseCase:AddPostToFavoriteUseCase,
                                            private val removePostFromFavoriteUseCase: RemovePostFromFavoriteUseCase): ViewModel(){

     var isSearching = false // Flag to indicate whether the user is currently searching

    private val _postsStateFlow = MutableStateFlow<Resource<List<Post>>>(Resource.Loading)
    val postsStateFlow: StateFlow<Resource<List<Post>>> = _postsStateFlow



    private val _addFaveSharedFlow = MutableSharedFlow<Resource<Unit>>()
    val addFaveSharedFlow: SharedFlow<Resource<Unit>> = _addFaveSharedFlow.asSharedFlow()

    private val _removeFaveSharedFlow = MutableSharedFlow<Resource<Unit>>()
    val removeFaveSharedFlow: SharedFlow<Resource<Unit>> = _removeFaveSharedFlow.asSharedFlow()


    private var lastVisibleDocument: DocumentSnapshot? = null

    private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
    private val allPosts: StateFlow<List<Post>> = _allPosts
    init {
        fetchPosts()
    }
    private val delay: Long = 1000
    private var searchJob: Job? = null

    fun fetchPosts() {
        viewModelScope.launch {
            try {
                _postsStateFlow.value = Resource.Loading // Emit loading state

                val result = getPostsUseCase.execute(lastVisibleDocument)

                result.collect { (resource, newLastVisible) ->
                    when (resource) {
                        is Resource.Success -> {
                            if (resource.data.isNotEmpty()) {
                                val currentPosts = _allPosts.value.toMutableList()
                                currentPosts.addAll(resource.data)
                                _allPosts.value = currentPosts
                                lastVisibleDocument = newLastVisible
                                _postsStateFlow.value = Resource.Success(allPosts.value)
                            }
                        }
                        is Resource.Error -> {
                            _postsStateFlow.value = resource // Emit error state
                        }
                        is Resource.Loading -> {
                            _postsStateFlow.value = resource                        }

                    }
                }
            } catch (e: Throwable) {
                _postsStateFlow.value = Resource.Error(e) // Emit error state
            }
        }
    }


    // Method to perform search
    fun searchPosts(query: String) {
        viewModelScope.launch {
            cancelPreviousSearch()
            if (query.isBlank()) {
                resetSearch()
            } else {
                initiateSearch(query)
            }
        }
    }

    private fun cancelPreviousSearch() {
        searchJob?.cancel()
    }
    private fun initiateSearch(query: String) {
        searchJob = viewModelScope.launch {
            delay(delay)
            updateSearchState(true)
            executeSearch(query)
        }
    }
    private suspend fun executeSearch(query: String) {
        searchPostsByNameUseCase(query).collect { networkSearchedPosts ->
            handleSearchResult(networkSearchedPosts)
        }
    }
    private fun handleSearchResult(networkSearchedPosts: Resource<List<Post>>) {
        when (networkSearchedPosts) {
            is Resource.Success -> _postsStateFlow.value = networkSearchedPosts
            is Resource.Error -> _postsStateFlow.value = networkSearchedPosts
            is Resource.Loading -> _postsStateFlow.value = networkSearchedPosts
        }
    }
    private fun updateSearchState(isSearching: Boolean) {
        this.isSearching = isSearching
    }

    // Method to revert to the original list
     fun resetSearch() {
        updateSearchState(false)
        _postsStateFlow.value = Resource.Success(_allPosts.value)
    }



    fun refreshPosts(){
        lastVisibleDocument=null
        _allPosts.value= emptyList()
        fetchPosts()
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

    fun addFav(postId:String) {
        viewModelScope.launch {
            try {
                _addFaveSharedFlow.emit(Resource.Loading)
                val result = addPostToFavoriteUseCase.execute(postId)
                _addFaveSharedFlow.emit(result)
            } catch (e: Exception) {
                if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                    _addFaveSharedFlow.emit(Resource.Error(e))
                } else {
                    _addFaveSharedFlow.emit(Resource.Error(e))
                }
            }

        }
    }


}
