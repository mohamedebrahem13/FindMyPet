package com.example.findmypet.ui.postdetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.findmypet.adapter.ImagePagerAdapter
import com.example.findmypet.databinding.FragmentZoomBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ZoomFragment : Fragment() {
    private lateinit var binding:FragmentZoomBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding  = FragmentZoomBinding.inflate(inflater, container, false)

        // Hide system bars using WindowInsetsControllerCompat

        val imageUrl = ZoomFragmentArgs.fromBundle(requireArguments()).imageurl
        val imageUrls = ZoomFragmentArgs.fromBundle(requireArguments()).imageurls
        val imageUrlsList = imageUrls.toList()
        Log.v("imageUrlsList",imageUrlsList.toString())
        Log.v("imageUrl",imageUrl)

        val initialPosition = imageUrlsList.indexOf(imageUrl)

        val adapter = ImagePagerAdapter(imageUrlsList)
        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(initialPosition, false)
        val tabLayout: TabLayout = binding.tabLayout

        TabLayoutMediator(tabLayout, binding.viewPager) { tab, position ->
            // Customize tab labels if needed
            tab.text = "Image ${position + 1}"
        }.attach()


        return binding.root
    }
}
