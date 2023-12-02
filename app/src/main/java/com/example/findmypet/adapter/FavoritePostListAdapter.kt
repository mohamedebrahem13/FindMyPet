package com.example.findmypet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.findmypet.data.model.Post
import com.example.findmypet.databinding.FavoritePostItemBinding

class FavoritePostListAdapter(
    private val postClickListener: PostListener,
    private val profileImageClickListener: ProfileImageClickListener,
    private val removeFaveImageClickListener: RemoveFaveImageClickListener
) : ListAdapter<Post, FavoritePostListAdapter.PostViewHolder>(PostDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = FavoritePostItemBinding.inflate(layoutInflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post, postClickListener, profileImageClickListener, removeFaveImageClickListener)
    }

    inner class PostViewHolder(private val binding: FavoritePostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            post: Post,
            postClickListener: PostListener,
            profileImageClickListener: ProfileImageClickListener,
            removeFaveImageClickListener: RemoveFaveImageClickListener
        ) {
            binding.post = post
            binding.executePendingBindings()
            binding.clickListener = postClickListener
            binding.profileImageClickListener = profileImageClickListener
            binding.removeFaveImageClickListener = removeFaveImageClickListener

            binding.removeFave.setOnClickListener {
                // Handle the click event for removing a favorite post
                removeFaveImageClickListener.onClick(post)
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
    class RemoveFaveImageClickListener(val clickListener: (post: Post) -> Unit) {
        fun onClick(post: Post) = clickListener(post)
    }
}