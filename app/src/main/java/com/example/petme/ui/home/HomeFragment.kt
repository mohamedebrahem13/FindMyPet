package com.example.petme.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petme.R
import com.example.petme.common.Resource
import com.example.petme.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), MenuProvider {

    private lateinit var postListAdapter: PostListAdapter

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)
        requireActivity().addMenuProvider(this,viewLifecycleOwner)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        // Initialize the PostListAdapter
        postListAdapter = PostListAdapter(PostListAdapter.PostListener { post ->
            Toast.makeText(this.context, post.pet_name,Toast.LENGTH_SHORT).show()

            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailsFragment(
                    post))
        },
            PostListAdapter.ProfileImageClickListener { post ->

                findNavController().navigate( HomeFragmentDirections.actionHomeFragmentToProfileFragment(post))
                // Handle profile image click here
            })
        binding.postListAdapter = postListAdapter



        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddPet())
        }
        getPosts()
        fetchHomeData()
        initObservers()

        return binding.root

    }



    private fun getPosts(){
        // Observe the sorted posts StateFlow
        with(binding){

            lifecycleScope.launchWhenResumed {
                homeViewModel.sortedPosts.collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            prograss.visibility = View.VISIBLE

                            // Handle loading state
                            // You can show a loading indicator or perform other actions
                        }
                        is Resource.Success -> {
                            // Handle success state
                            prograss.visibility = View.GONE
                            postListAdapter?.submitList(result.data)
                            Log.v("sucsses", result.data.toString())


                        }
                        is Resource.Error -> {
                            // Handle error state
                            prograss.visibility = View.GONE
                            val error = result.throwable
                            Log.v("Home", error.toString())

                            // Show an error message or perform other error handling
                        }
                    }
                }

            }


        }

    }




    private fun fetchHomeData() {
        lifecycleScope.launchWhenResumed {
            homeViewModel.getCurrentUser() // Trigger a data refresh when the fragment is resumed
        }    }



    private fun initObservers() {
        with(binding) {
            with(homeViewModel) {
                lifecycleScope.launchWhenResumed {
                    currentUser.collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                tvNickname.text = resource.data.nickname.split(" ")[0]
                                prograss.visibility = View.GONE
                                hi.visibility=View.VISIBLE
                            }
                            is Resource.Error -> {
                                Log.v("HomeFragment", resource.toString())
                                prograss.visibility = View.GONE
                                hi.visibility=View.GONE


                            }
                            Resource.Loading ->{
                                prograss.visibility =
                                View.VISIBLE
                                hi.visibility=View.GONE

                            }

                            else -> {

                                // Handle other states if necessary
                            }
                        }
                    }
                }
            }


        }


    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater .inflate(R.menu.user_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.Profile ->
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
            R.id.developer -> Toast.makeText(this.context, "Settings Selected", Toast.LENGTH_SHORT)
                .show()

        }
        return true    }
}

