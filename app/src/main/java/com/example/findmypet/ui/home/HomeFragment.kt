package com.example.findmypet.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.findmypet.R
import com.example.findmypet.adapter.TabAdapter
import com.example.findmypet.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), MenuProvider {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel:HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        requireActivity().addMenuProvider(this, viewLifecycleOwner)

        binding.floatingActionButton.setImageResource(R.drawable.ic_baseline_add_24)
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

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customTabView = layoutInflater.inflate(R.layout.tab_custom_view, null)
            val tabIcon = customTabView.findViewById<ImageView>(R.id.tab_icon)
            val tabText = customTabView.findViewById<TextView>(R.id.tab_text)

            tabIcon.setImageResource(tabAdapter.getTabIcon(position))
            tabText.text = tabAdapter.getTabTitle(position)

            tab.customView = customTabView
        }.attach()


        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.user_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.Profile -> findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
            R.id.Conversation -> findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToConversationListFragment())
        }
        return true
    }
}
