package com.example.findmypet.ui.postsById

import android.app.AlertDialog
import android.os.Bundle
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
import com.example.findmypet.adapter.UserPostsAdapter
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.databinding.FragmentPostsByIdBinding
import com.example.findmypet.ui.home.HomeFragmentDirections
import com.google.android.gms.ads.AdRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.net.UnknownHostException

@AndroidEntryPoint
class PostsByIdFragment : Fragment() {
    private lateinit var userPostsAdapter: UserPostsAdapter
    private val viewModel: PostsByUserViewModel by viewModels()
    private lateinit var binding :FragmentPostsByIdBinding





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding=FragmentPostsByIdBinding.inflate(inflater)

        userPostsAdapter = UserPostsAdapter(UserPostsAdapter.PostListener(
            clickListener = { post ->
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(
                        post, "PostsByIdFragment"
                    )
                )
            },
            deleteClickListener = { post ->
                // Handle delete click
                showDeleteConfirmationDialog(post)
            }// Add this line

        ))
        setupAdView()
        setupRecyclerView()
        refresh()




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Other initialization code
        observeUserPosts()
    }

    private fun setupAdView() {
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }



    private fun showDeleteConfirmationDialog(post: Post) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Delete Post")
        alertDialogBuilder.setMessage("Are you sure you want to delete this post?")

        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // User clicked Yes, perform the delete action
            viewModel.observeDeletePostAction(post.postId.toString())
            deletePostObserver()
        }

        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            // User clicked No, do nothing or handle as needed
            dialog.dismiss() // Dismiss the dialog
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }



    private fun observeUserPosts() {

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.userPostsStateFlow.collect { postsResource ->
                        when (postsResource) {
                            is Resource.Success -> {
                                val userPosts = postsResource.data
                                binding.prograss.visibility = View.GONE

                                userPostsAdapter.submitList(userPosts)
                                    Toast.makeText(
                                        requireContext(),
                                        "Success get your posts",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    binding.prograss.visibility = View.GONE

                            }
                            is Resource.Error -> {
                                binding.prograss.visibility = View.GONE
                                val errorMessage = when (val error = postsResource.throwable) {
                                    is UnknownHostException -> "Network unavailable. Please check your internet connection."
                                    else -> error.message ?: "Unknown error"
                                }
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            is Resource.Loading -> {
                                binding.prograss.visibility = View.VISIBLE
                            }
                            // Handle other states if needed
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }


    }

    private fun deletePostObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.deletePostSharedFlow.collect { result ->
                    // Update UI based on the result state
                    when (result) {
                        is Resource.Loading -> {
                            binding.prograss.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.prograss.visibility = View.GONE
                            Toast.makeText(requireContext(), "Success delete the post", Toast.LENGTH_SHORT).show()
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

    private fun refresh(){
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchPostsForCurrentUser()
            binding.swipeRefreshLayout.isRefreshing = false // To stop the refreshing animation

        }
    }


    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userPostsAdapter
        }
    }

}