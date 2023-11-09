package com.example.petme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.petme.data.model.Post
import com.example.petme.databinding.UserPostItemBinding

class UserPostsAdapter(
    private val postClickListener: PostListener
) : ListAdapter<Post, UserPostsAdapter.PostViewHolder>(PostDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = UserPostItemBinding.inflate(layoutInflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post, postClickListener)
    }

    class PostViewHolder(private val binding: UserPostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, postClickListener: PostListener) {
            binding.post = post
            binding.executePendingBindings()
            binding.clickListener = postClickListener
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
}
