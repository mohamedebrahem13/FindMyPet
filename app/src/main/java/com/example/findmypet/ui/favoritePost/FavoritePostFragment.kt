package com.example.findmypet.ui.favoritePost

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findmypet.R
import com.example.findmypet.adapter.FavoritePostListAdapter
import com.example.findmypet.common.Resource
import com.example.findmypet.common.ToastUtils
import com.example.findmypet.data.model.Post
import com.example.findmypet.databinding.FragmentFavoritePostBinding
import com.example.findmypet.ui.home.HomeFragmentDirections
import com.google.android.gms.ads.AdRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.UnknownHostException

@AndroidEntryPoint
class FavoritePostFragment : Fragment() {

    private val viewModel: FavoritePostsViewModel by viewModels(ownerProducer = { this })
    private lateinit var binding:FragmentFavoritePostBinding
    private lateinit var favoritePostAdapter: FavoritePostListAdapter
    private lateinit var parentView: ViewGroup


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentFavoritePostBinding.inflate(inflater)



        // Initialize RecyclerView and Adapter
        favoritePostAdapter = FavoritePostListAdapter(
            postClickListener = FavoritePostListAdapter.PostListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(
                        it, getString(R.string.favoritefragment)
                    )
                )

            },
            profileImageClickListener = FavoritePostListAdapter.ProfileImageClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToProfileFragment(
                        it.user
                    )
                )

            },
            removeFaveImageClickListener = FavoritePostListAdapter.RemoveFaveImageClickListener {
                viewModel.removeFav(it.postId.toString())
                removeFaveObserver()

            }
        )
        setupAdView()
        setupRecyclerView()
        refresh()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentView = requireActivity().findViewById(android.R.id.content)
        observeFavoritePosts()
    }

    private fun observeFavoritePosts() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.favoritePosts.collect { postsResource ->
                    when (postsResource) {
                        is Resource.Success -> handleSuccess(postsResource.data)
                        is Resource.Error -> handleError(postsResource.throwable)
                        is Resource.Loading -> handleLoading()
                        // Add handling for other states if needed
                    }
                }
            }
        }
    }


    private fun handleError(throwable: Throwable) {
        binding.prograss.visibility = View.GONE
        val errorMessage = when (throwable) {
            is UnknownHostException -> getString(R.string.network_unavailable_please_check_your_internet_connection)
            else -> throwable.message ?: getString(R.string.unknown_error)
        }
        Log.e("fave_posts_error", "Error: $errorMessage")
        showToast(errorMessage,false)
    }

    private fun handleLoading() {
        binding.prograss.visibility = View.VISIBLE
    }

    private fun showToast(message: String,isSuccess: Boolean) {
        ToastUtils.showCustomToast(requireContext(), message,  parentView,isSuccess)

    }
    private fun handleSuccess(favoritePosts: List<Post>) {
        if (favoritePosts.isEmpty()) {

            showToast(getString(R.string.You_do_not_have_favorite_posts_or_need_to_refresh),false)
            binding.prograss.visibility = View.GONE
            favoritePostAdapter.submitList(favoritePosts)
        } else {
            if (favoritePostAdapter.currentList != favoritePosts) {
                favoritePostAdapter.submitList(favoritePosts)

                showToast(getString(R.string.success_get_the_favorite_posts ),true)
                binding.prograss.visibility = View.GONE
            } else {
                binding.prograss.visibility = View.GONE

                showToast(getString(R.string.No_new_favorite_posts ),false)
            }
        }
    }




    private fun refresh(){
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchFavoritePosts()
            binding.swipeRefreshLayout.isRefreshing = false // To stop the refreshing animation

        }
    }

    private fun setupAdView() {
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }


    private fun removeFaveObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.removeFaveSharedFlow.collect { result ->
                    // Update UI based on the result state
                    when (result) {
                        is Resource.Loading -> {
                            binding.prograss.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.prograss.visibility = View.GONE
                            Toast.makeText(requireContext(), getString(R.string.success_remove_post_favorite ), Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Error -> {
                            binding.prograss.visibility = View.GONE
                            val error = result.throwable.message
                            Toast.makeText(requireContext(), error ?: "Unknown error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }





    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoritePostAdapter
        }
    }

}