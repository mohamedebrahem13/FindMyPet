package com.example.findmypet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.findmypet.data.model.Post
import com.example.findmypet.databinding.PostItemBinding

class PostListAdapter(
    private val postClickListener: PostListener,
    private val profileImageClickListener: ProfileImageClickListener
    ,private val faveImageClickListener:FaveImageClickListener,private val removeFaveImageClickListener:RemoveFaveImageClickListener
) : ListAdapter<Post, PostListAdapter.PostViewHolder>(PostDiffUtil()) {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PostItemBinding.inflate(layoutInflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post, postClickListener, profileImageClickListener,faveImageClickListener,removeFaveImageClickListener)
    }



    inner class PostViewHolder(private val binding: PostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, postClickListener: PostListener, profileImageClickListener: ProfileImageClickListener,FaveImageClickListener:FaveImageClickListener,removeFaveImageClickListener: RemoveFaveImageClickListener) {
            binding.post = post
            binding.executePendingBindings()
            binding.faveImageClickListener=FaveImageClickListener
            binding.clickListener = postClickListener
            binding.profileImageClickListener = profileImageClickListener
            binding.removeFaveImageClickListener=removeFaveImageClickListener
            binding.faveFill.setOnClickListener {
                if (binding.faveFill.visibility == View.VISIBLE) {
                    binding.faveFill.visibility = View.GONE
                    binding.imageView3.visibility = View.VISIBLE
                } else {
                    binding.faveFill.visibility = View.VISIBLE
                    binding.imageView3.visibility = View.GONE
                }

                // Notify the listener about the click event
                removeFaveImageClickListener.onClick(post)

            }






            binding.imageView3.setOnClickListener {
                // Toggle the visibility of ImageViews when clicked
                if (binding.imageView3.visibility == View.VISIBLE) {
                    binding.imageView3.visibility = View.GONE
                    binding.faveFill.visibility = View.VISIBLE
                } else {
                    binding.imageView3.visibility = View.VISIBLE
                    binding.faveFill.visibility = View.GONE
                }

                // Notify the listener about the click event
                faveImageClickListener.onClick(post)
            }

        }




    }

    class PostDiffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }

    class PostListener(val clickListener: (post: Post) -> Unit) {
        fun onClick(post: Post) = clickListener(post)
    }

    class ProfileImageClickListener(val clickListener: (post: Post) -> Unit) {
        fun onClick(post: Post) = clickListener(post)
    }

    class FaveImageClickListener(val clickListener: (post: Post) -> Unit) {
        fun onClick(post: Post) = clickListener(post)
    }
    class RemoveFaveImageClickListener(val clickListener: (post: Post) -> Unit) {
        fun onClick(post: Post) = clickListener(post)
    }
}
