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
import androidx.recyclerview.widget.RecyclerView
import com.example.findmypet.R
import com.example.findmypet.adapter.PostListAdapter
import com.example.findmypet.common.Resource
import com.example.findmypet.common.ToastUtils
import com.example.findmypet.databinding.AllPostsFragmentBinding
import com.example.findmypet.ui.home.HomeFragmentDirections
import com.google.android.gms.ads.AdRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllPostsFragment : Fragment() {


    private lateinit var postListAdapter: PostListAdapter
    private lateinit var binding: AllPostsFragmentBinding
    private val allPostsViewModel: AllPostsViewModel by viewModels()
    private lateinit var parentView: ViewGroup


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AllPostsFragmentBinding.inflate(inflater)
        val recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        setupAdView()

        postListAdapter = PostListAdapter(
            PostListAdapter.PostListener { post ->
                Toast.makeText(this.context, post.pet_name, Toast.LENGTH_SHORT).show()

                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(post,"AllPostsFragment")
                )
            },
            PostListAdapter.ProfileImageClickListener { post ->

                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment(post.user))
            },PostListAdapter.FaveImageClickListener{
                allPostsViewModel.addFav(postId = it.postId.toString())
                addFaveObserver() },PostListAdapter.RemoveFaveImageClickListener{
                allPostsViewModel.removeFav(postId = it.postId.toString())
                removeFaveObserver()
            })
        binding.postListAdapter = postListAdapter

        initObservers()
        refresh()
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!allPostsViewModel.isSearching){
                    Log.v("is_Searching",allPostsViewModel.isSearching.toString())
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // End of list reached, load more posts
                        allPostsViewModel.fetchPosts()
                    }
                }

            }
        })


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentView = requireActivity().findViewById(android.R.id.content)
        setupSearchListener(binding.editTextSearch)

    }

    private fun setupSearchListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                // Not needed for your use case
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                allPostsViewModel.isSearching=true
                allPostsViewModel.searchPosts(s?.toString()?.replaceFirstChar { it.uppercase() } ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) {
                    // If the search bar is empty, reset the search and show the original list of posts
                    allPostsViewModel.resetSearch()
                }
            }
        })
    }

    private fun setupAdView() {
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }


    private fun refresh(){
        binding.swipeRefreshLayout.setOnRefreshListener {
            if(!allPostsViewModel.isSearching){
                allPostsViewModel.refreshPosts()
                binding.swipeRefreshLayout.isRefreshing = false // To stop the refreshing animation
            }else{
                binding.swipeRefreshLayout.isRefreshing = false // To stop the refreshing animation

            }
        }
    }

    private fun removeFaveObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                allPostsViewModel.removeFaveSharedFlow.collect { result ->
                    // Update UI based on the result state
                    when (result) {
                        is Resource.Loading -> {
                            binding.prograss.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.prograss.visibility = View.GONE
                            ToastUtils.showCustomToast(requireContext(),
                                getString(R.string.success_remove_post_favorite),  parentView,true)
                        }
                        is Resource.Error -> {
                            handleResourceError(result)
                        }
                    }
                }
            }
        }
    }



    private fun addFaveObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                allPostsViewModel.addFaveSharedFlow.collect { result ->
                    // Update UI based on the result state
                    when (result) {
                        is Resource.Loading -> {
                            binding.prograss.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.prograss.visibility = View.GONE
                            ToastUtils.showCustomToast(requireContext(), getString(R.string.success_add_post_favorite), parentView, true)
                        }
                        is Resource.Error -> {
                            handleResourceError(result)
                        }
                    }
                }
            }
        }
    }




    private fun observePostsData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                allPostsViewModel.postsStateFlow.collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            binding.prograss.visibility = View.GONE
                            Log.v("success",result.data.toString())
                            if (result.data.isNotEmpty()) {
                                binding.tvEmptySorted.visibility = View.GONE
                                postListAdapter.submitList(result.data)
                                Log.v("success GET THE POSTS", result.data.toString())
                                ToastUtils.showCustomToast(requireContext(), getString(R.string.success_get_posts), parentView, true)

                            }else{
                                postListAdapter.submitList(result.data)
                                binding.tvEmptySorted.visibility = View.VISIBLE

                            }
                        }
                        is Resource.Error -> {
                            handleResourceError(result)
                            Log.v("erroron",result.throwable.toString())


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
        observePostsData()
    }


    private fun handleResourceError(resource: Resource.Error) {
        binding.prograss.visibility = View.GONE
        val error = resource.throwable
        Log.v("error", error.toString())
        // Show the custom toast using ToastUtils
        ToastUtils.showCustomToast(requireContext(), error.message.toString(),  parentView,false)
    }

}