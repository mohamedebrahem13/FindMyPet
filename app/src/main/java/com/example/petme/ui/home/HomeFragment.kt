package com.example.petme.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.petme.R
import com.example.petme.adapter.TabAdapter
import com.example.petme.databinding.FragmentHomeBinding
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

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddPet())
        }

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager
        val adapter = TabAdapter(this)

        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "All Posts"
                1 -> "Your Posts"
                2 -> "Favorite Posts"
                else -> ""
            }
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
            R.id.developer -> Toast.makeText(this.context, "Settings Selected", Toast.LENGTH_SHORT)
                .show()

        }
        return true    }
}

