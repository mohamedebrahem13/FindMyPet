package com.example.findmypet.ui.postdetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findmypet.R
import com.example.findmypet.adapter.DetailsImageAdapter
import com.example.findmypet.databinding.FragmentPostDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailsFragment : Fragment() {

    private lateinit var currentUid: String
    private lateinit var binding: FragmentPostDetailsBinding
    private lateinit var detailsImageAdapter: DetailsImageAdapter
    private val viewModel: PostDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDetailsBinding.inflate(inflater)
        currentUid = viewModel.uid

        with(binding) {
            val post = PostDetailsFragmentArgs.fromBundle(requireArguments()).post
            postData = post
            val sourceFragment = PostDetailsFragmentArgs.fromBundle(requireArguments()).sourceFragment

            imageUrlsRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            detailsImageAdapter = DetailsImageAdapter(post.imageUrls, object : DetailsImageAdapter.OnDetailsImageClickListener {
                override fun onDetailsImageClick(position: Int) {
                    val imageUrl = post.imageUrls?.get(position)
                    val imageUrlsArray = post.imageUrls?.toTypedArray()

                    val action = imageUrl?.let {
                        PostDetailsFragmentDirections.actionDetailsFragmentToZoomFragment(
                            imageUrlsArray ?: emptyArray(), imageUrl
                        )
                    }

                    if (action != null) {
                        findNavController().navigate(action)
                    }
                }
            })

            imageUrlsRecyclerView.adapter = detailsImageAdapter

            when (sourceFragment) {
                getString(R.string.postsbyidfragment)-> {
                    imageButton.visibility = View.GONE
                    chat.visibility = View.GONE
                }

                getString(R.string.allpostsfragment), getString(R.string.favoritefragment) -> {
                    if (post.user?.id == currentUid) {
                        imageButton.visibility = View.GONE
                        chat.visibility = View.GONE
                    } else {
                        imageButton.visibility = View.VISIBLE
                        chat.visibility = View.VISIBLE

                        imageButton.setOnClickListener {
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
                }
            }
        }
        binding.buttonBack.setOnClickListener{
            findNavController().navigateUp()
        }

        return binding.root
    }
}
