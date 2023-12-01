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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findmypet.adapter.PostListAdapter
import com.example.findmypet.common.Resource
import com.example.findmypet.databinding.AllPostsFragmentBinding
import com.example.findmypet.ui.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
            },PostListAdapter.FaveImageClickListener{
                allPostsViewModel.addFav(postId = it.postId.toString())
                addFaveObserver() },PostListAdapter.RemoveFaveImageClickListener{
                allPostsViewModel.removeFav(postId = it.postId.toString())
                removeFaveObserver()
            })
        binding.postListAdapter = postListAdapter

        getCurrentUser()
        initObservers()
        refresh()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchListener(binding.etSearch)
    }

    private fun setupSearchListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvEmptySearched.visibility =View.GONE
                // Not needed for your use case
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                allPostsViewModel.searchPosts(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    // If the search bar is empty, reset the search and show the original list of posts
                    allPostsViewModel.resetSearch()
                }
            }
        })
    }



    private fun refresh(){
        binding.swipeRefreshLayout.setOnRefreshListener {
            allPostsViewModel.fetchPosts()
            binding.swipeRefreshLayout.isRefreshing = false // To stop the refreshing animation
        }
    }

    private fun removeFaveObserver(){

        lifecycleScope.launchWhenStarted{
            allPostsViewModel.removeFaveSharedFlow.collect{ result ->
                // Update UI based on the result state
                when (result) {
                    is Resource.Loading -> {
                        binding.prograss.visibility=View.VISIBLE

                    }
                    is Resource.Success -> {
                        binding.prograss.visibility=View.GONE
                        Toast.makeText(requireContext(),"Success remove the post from favorite ", Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Error -> {
                        binding.prograss.visibility=View.GONE
                        val error = result.throwable.message
                        Toast.makeText(requireContext(), error ?: "Unknown error", Toast.LENGTH_SHORT).show()


                    }
                }
            }

        }
    }



    private fun addFaveObserver(){

        // Observe addfave post loading state
        lifecycleScope.launchWhenStarted{
            allPostsViewModel.addFaveSharedFlow.collect { result ->
                // Update UI based on the result state
                when (result) {
                    is Resource.Loading -> {
                        binding.prograss.visibility=View.VISIBLE

                    }
                    is Resource.Success -> {
                        binding.prograss.visibility=View.GONE
                        Toast.makeText(requireContext(),"Success add the post to favorite ", Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Error -> {
                        binding.prograss.visibility=View.GONE
                        val error = result.throwable.message
                        Toast.makeText(requireContext(), error ?: "Unknown error", Toast.LENGTH_SHORT).show()


                    }
                }
            }

        }

    }


    private fun observeCurrentUser() {
        lifecycleScope.launchWhenResumed {
            allPostsViewModel.currentUser.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        binding.tvNickname.text = resource.data.nickname.split(" ")[0]
                        binding.prograss.visibility = View.GONE
                        binding.hi.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {
                        Log.v("currentuser", resource.toString())
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

    private fun observePostsLiveData() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                allPostsViewModel.postsStateFlow.collect { result ->
                    when (result) {

                        is Resource.Success -> {
                            binding.prograss.visibility = View.GONE
                            postListAdapter.submitList(result.data)
                            binding.tvEmptySorted.visibility =
                                if (result.data.isEmpty()) View.VISIBLE else View.GONE
                            binding.tvEmptySearched.visibility = View.GONE

                            Log.v("success", result.data.toString())
                            Toast.makeText(
                                requireContext(),
                                "Success get the posts ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is Resource.Error -> {
                            handleResourceError(result)
                            Toast.makeText(
                                requireContext(),
                                "error get the posts ",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        Resource.Loading -> {
                            binding.prograss.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }




    private fun initObservers() {
        observeCurrentUser()
        observePostsLiveData()
    }

    private fun getCurrentUser() {
        lifecycleScope.launchWhenResumed {
            allPostsViewModel.getCurrentUser()
        }
    }

    private fun handleResourceError(resource: Resource.Error) {
        binding.prograss.visibility = View.GONE
        val error = resource.throwable
        Log.v("allPosts", error.toString())
        Toast.makeText(requireContext(), "Error getting the posts", Toast.LENGTH_SHORT).show()
    }

}