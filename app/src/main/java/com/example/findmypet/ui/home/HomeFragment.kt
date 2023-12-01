package com.example.findmypet.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.findmypet.R
import com.example.findmypet.adapter.TabAdapter
import com.example.findmypet.databinding.FragmentHomeBinding
import com.example.findmypet.databinding.TabCustomViewBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), MenuProvider {


    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)
        requireActivity().addMenuProvider(this,viewLifecycleOwner)

        binding.floatingActionButton.setImageResource(R.drawable.ic_baseline_add_24)
        binding.floatingActionButton.imageTintList = null
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddPet())
        }

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager
        val adapter = TabAdapter(this)
        val tabIcons = arrayOf(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_people_24),
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_person_24),
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_favorite_24)
        )

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabBinding = TabCustomViewBinding.inflate(layoutInflater)
            tabBinding.tabIcon = tabIcons[position]
            tabBinding.tabText = when (position) {
                0 -> "All Posts"
                1 -> "Your Posts"
                2 -> "Favorite"
                else -> ""
            }

            tab.customView = tabBinding.root
        }.attach()


        return binding.root

    }









    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater .inflate(R.menu.user_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.Profile ->
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
            R.id.developer -> Toast.makeText(this.context, "developer Selected", Toast.LENGTH_SHORT)
                .show()

        }
        return true    }
}

