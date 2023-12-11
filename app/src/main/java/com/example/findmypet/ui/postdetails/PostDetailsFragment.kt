package com.example.findmypet.ui.postdetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findmypet.adapter.ImageAdapter
import com.example.findmypet.databinding.FragmentPostDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailsFragment : Fragment() {


    private lateinit var binding: FragmentPostDetailsBinding
    private lateinit var imageAdapter: ImageAdapter

    // Inside PostDetailsFragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPostDetailsBinding.inflate(inflater)

        with(binding) {
            val post = PostDetailsFragmentArgs.fromBundle(requireArguments()).post
            postData= post
            val sourceFragment = PostDetailsFragmentArgs.fromBundle(requireArguments()).sourceFragment

            // Set up the RecyclerView and its LinearLayoutManager
            imageUrlsRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            // Initialize and set up the ImageAdapter with the post's imageUrls
            imageAdapter = ImageAdapter(post.imageUrls)
            imageUrlsRecyclerView.adapter = imageAdapter

            // Handle views based on the source fragment
            when (sourceFragment) {
                "PostsByIdFragment" -> {
                    // Code specific to PostsByIdFragment
                    imageButton.visibility = View.GONE // or View.INVISIBLE
                    chat.visibility = View.GONE
                }

                "AllPostsFragment", "FavoriteFragment" -> {
                    // Code specific to AllPostsFragment
                    imageButton.visibility = View.VISIBLE


                    // Set OnClickListener for dialer
                    imageButton.setOnClickListener {
                        // Open dialer
                        val dialIntent = Intent(Intent.ACTION_DIAL)
                        dialIntent.data = Uri.parse("tel:${post.user?.phone}")
                        startActivity(dialIntent)
                    }

                    chat.setOnClickListener {
                        post.user?.let { user ->
                            findNavController().navigate(
                                PostDetailsFragmentDirections.actionDetailsFragmentToChatFragment(user)
                            )
                        }
                    }

                }
                // Add more cases as needed for other source fragments
                else -> {
                    // Default case
                    imageButton.visibility = View.VISIBLE
                }
            }
        }

        return binding.root
    }



}