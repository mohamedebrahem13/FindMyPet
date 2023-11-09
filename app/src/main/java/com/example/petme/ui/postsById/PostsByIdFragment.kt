package com.example.petme.ui.postsById

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petme.adapter.UserPostsAdapter
import com.example.petme.common.Resource
import com.example.petme.databinding.FragmentPostsByIdBinding
import com.example.petme.ui.home.HomeFragmentDirections
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

        userPostsAdapter =UserPostsAdapter(UserPostsAdapter.PostListener{
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailsFragment(
                    it))

        })
        setupRecyclerView()

        viewModel.getPostsForCurrentUser()

        lifecycleScope.launchWhenStarted {
            viewModel.postsStateFlow.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Show loading progress
                    }
                    is Resource.Success -> {
                        val posts = resource.data
                        userPostsAdapter.submitList(posts)
                        // Update UI with the posts
                    }
                    is Resource.Error -> {
                        val error = resource.throwable.message
                        // Handle the error
                    }
                }
            }
        }


        return binding.root
    }



    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userPostsAdapter
        }
    }

}