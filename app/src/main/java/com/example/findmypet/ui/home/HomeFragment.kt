package com.example.findmypet.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.findmypet.R
import com.example.findmypet.adapter.TabAdapter
import com.example.findmypet.common.Resource
import com.example.findmypet.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel:HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)

        binding.floatingActionButton.imageTintList = null
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddPet())
        }

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager
        val tabIcons = arrayOf(
            R.drawable.ic_baseline_people_24,
            R.drawable.ic_baseline_person_24,
            R.drawable.ic_baseline_favorite_24
        )
        val tabTitles = arrayOf("All Posts", "Your Posts", "Favorite")

        val tabAdapter = TabAdapter(this,tabIcons,tabTitles)


        viewPager.adapter = tabAdapter
        homeViewModel.onViewOpened()
        getCurrentUser()
        initObservers()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customTabView = layoutInflater.inflate(R.layout.tab_custom_view, null)
            val tabIcon = customTabView.findViewById<ImageView>(R.id.tab_icon)
            val tabText = customTabView.findViewById<TextView>(R.id.tab_text)

            tabIcon.setImageResource(tabAdapter.getTabIcon(position))
            tabText.text = tabAdapter.getTabTitle(position)

            tab.customView = customTabView
        }.attach()

        // Set click listeners for profile image and messages image
        binding.profileImage.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
        }

        binding.messagesImage.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToConversationListFragment())
        }


        return binding.root
    }

    private fun initObservers() {
        observeCurrentUser()
    }

    private fun getCurrentUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                homeViewModel.getCurrentUser()
            }
        }
    }

    private fun observeCurrentUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                homeViewModel.currentUser.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            binding.user=resource.data
                            binding.hi.visibility = View.VISIBLE
                        }
                        is Resource.Error -> {
                            Log.v("current user", resource.toString())
                            binding.hi.visibility = View.GONE
                        }
                        is Resource.Loading -> {
                            binding.hi.visibility = View.GONE
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
