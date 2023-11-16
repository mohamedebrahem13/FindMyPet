package com.example.findmypet.ui.postsById

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findmypet.adapter.UserPostsAdapter
import com.example.findmypet.common.Resource
import com.example.findmypet.databinding.FragmentPostsByIdBinding
import com.example.findmypet.ui.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

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
                viewModel.deletePost(post.postId.toString())
                deletePostObserver()
            }
        ))
        setupRecyclerView()

        viewModel.getPostsForCurrentUser()






        lifecycleScope.launchWhenStarted {
            viewModel.postsStateFlow.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.prograss.visibility=View.VISIBLE
                        // Show loading progress
                    }
                    is Resource.Success -> {
                        val posts = resource.data
                        userPostsAdapter.submitList(posts)
                        binding.prograss.visibility=View.GONE
                        // Update UI with the posts
                    }
                    is Resource.Error -> {
                        binding.prograss.visibility=View.GONE

                        val error = resource.throwable.message
                        Toast.makeText(requireContext(), error?.toString() ?: "Unknown error", Toast.LENGTH_SHORT).show()

                        // Handle the error
                    }
                }
            }
        }


        return binding.root
    }




    fun deletePostObserver(){

        // Observe delete post loading state
        lifecycleScope.launchWhenStarted{
            viewModel.deletePostStateFlow.collect { result ->
                // Update UI based on the result state
                when (result) {
                    is Resource.Loading -> {
                        binding.prograss.visibility=View.VISIBLE

                    }
                    is Resource.Success -> {
                        binding.prograss.visibility=View.GONE
                        Toast.makeText(requireContext(),"Success delete the post ", Toast.LENGTH_SHORT).show()



                    }
                    is Resource.Error -> {
                        binding.prograss.visibility=View.GONE
                        val error = result.throwable.message
                        Toast.makeText(requireContext(), error?.toString() ?: "Unknown error", Toast.LENGTH_SHORT).show()


                    }
                }
            }

        }

    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userPostsAdapter
        }
    }

}