package com.example.petme.ui.postdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petme.databinding.FragmentPostDetailsBinding
import com.example.petme.ui.addpet.ImageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailsFragment : Fragment() {


    private lateinit var binding: FragmentPostDetailsBinding
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPostDetailsBinding.inflate(inflater)

        with(binding){
        val post= PostDetailsFragmentArgs.fromBundle(requireArguments()).post
            postData= post
            // Set up the RecyclerView and its LinearLayoutManager
            imageUrlsRecyclerView.layoutManager =   LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            // Initialize and set up the ImageAdapter with the post's imageUrls
            imageAdapter = ImageAdapter(post.imageUrls)
            imageUrlsRecyclerView.adapter = imageAdapter
        }

        return binding.root

    }


}