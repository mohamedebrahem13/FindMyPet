package com.example.findmypet.ui.allposts

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findmypet.adapter.PostListAdapter
import com.example.findmypet.common.Resource
import com.example.findmypet.databinding.AllPostsFragmentBinding
import com.example.findmypet.ui.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllPostsFragment : Fragment() {

    private lateinit var postListAdapter: PostListAdapter
    private lateinit var binding: AllPostsFragmentBinding
    private val allPostsViewModel: AllPostsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AllPostsFragmentBinding.inflate(inflater)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        postListAdapter = PostListAdapter(
            PostListAdapter.PostListener { post ->
                Toast.makeText(this.context, post.pet_name, Toast.LENGTH_SHORT).show()

                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(post,"AllPostsFragment")
                )
            },
            PostListAdapter.ProfileImageClickListener { post ->

                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment(post))
            })
        binding.postListAdapter = postListAdapter

        allPostsViewModel.getPosts()

        fetchHomeData()
        initObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchListener(binding.etSearch)

        observeSearchedPosts()
    }

    private fun setupSearchListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed for your use case
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                allPostsViewModel.searchPostsByPetName(s?.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    // If the search bar is empty, clear the search and show the sorted list
                    allPostsViewModel.searchPostsByPetName(null)
                }
            }
        })
    }





    private fun observeCurrentUser() {
        lifecycleScope.launchWhenResumed {
            allPostsViewModel.currentUser.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        binding.tvNickname.text = resource.data?.nickname?.split(" ")?.get(0)
                        binding.prograss.visibility = View.GONE
                        binding.hi.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {
                        Log.v("HomeFragment", resource.toString())
                        binding.prograss.visibility = View.GONE
                        binding.hi.visibility = View.GONE
                    }
                    Resource.Loading -> {
                        binding.prograss.visibility = View.VISIBLE
                        binding.hi.visibility = View.GONE
                    }
                    else -> {
                        // Handle other states if necessary
                    }
                }
            }
        }
    }

    private fun observeSortedPosts() {
        lifecycleScope.launchWhenResumed {
            allPostsViewModel.sortedPosts.collect { result ->
                when (result) {
                    is Resource.Success -> {
                        binding.prograss.visibility = View.GONE
                        postListAdapter.submitListWithType(result.data, PostListAdapter.ListType.Sorted)
                        binding.tvEmptyMessage.visibility = if (result.data.isEmpty()) View.VISIBLE else View.GONE

                        Log.v("success", result.data.toString())
                    }
                    is Resource.Error -> {
                        binding.prograss.visibility = View.GONE
                        val error = result.throwable
                        Log.v("Home", error.toString())
                    }
                    Resource.Loading -> {
                        binding.prograss.visibility = View.VISIBLE
                    }
                    else -> {
                        // Handle other states if necessary
                    }
                }
            }
        }
    }

    private fun observeSearchedPosts() {
        lifecycleScope.launchWhenResumed {
            allPostsViewModel.searchedPosts.collect { result ->
                when (result) {
                    is Resource.Success -> {
                        binding.prograss.visibility = View.GONE
                        postListAdapter.submitListWithType(result.data, PostListAdapter.ListType.Searched)
                        Log.v("success", result.data.toString())
                    }
                    is Resource.Error -> {
                        binding.prograss.visibility = View.GONE
                        val error = result.throwable
                        Log.v("Search", error.toString())
                    }
                    Resource.Loading -> {
                        binding.prograss.visibility = View.VISIBLE
                    }
                    else -> {
                        // Handle other states if necessary
                    }
                }
            }
        }
    }

    private fun initObservers() {
        observeCurrentUser()
        observeSortedPosts()
        observeSearchedPosts()
    }

    private fun fetchHomeData() {
        lifecycleScope.launchWhenResumed {
            allPostsViewModel.getCurrentUser()
        }
    }
}
