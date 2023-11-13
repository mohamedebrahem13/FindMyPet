package com.example.petme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.petme.data.model.Post
import com.example.petme.databinding.PostItemBinding

class PostListAdapter(
    private val postClickListener: PostListener,
    private val profileImageClickListener: ProfileImageClickListener
) : ListAdapter<Post, PostListAdapter.PostViewHolder>(PostDiffUtil()) {

    enum class ListType {
        Sorted,
        Searched
    }

    private var currentListType: ListType = ListType.Sorted

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PostItemBinding.inflate(layoutInflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post, postClickListener, profileImageClickListener)
    }

    fun submitListWithType(list: List<Post>?, listType: ListType) {
        currentListType = listType
        submitList(list)
    }

    inner class PostViewHolder(private val binding: PostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, postClickListener: PostListener, profileImageClickListener: ProfileImageClickListener) {
            binding.post = post
            binding.executePendingBindings()
            binding.clickListener = postClickListener
            binding.profileImageClickListener = profileImageClickListener

            // Customize the item view based on the list type
            when (currentListType) {
                ListType.Sorted -> {
                    // Customize UI for the sorted list if needed
                }
                ListType.Searched -> {
                    // Customize UI for the searched list if needed
                }
            }

            updateEmptyMessageVisibility()
        }


        private fun updateEmptyMessageVisibility() {
            if (currentListType == ListType.Searched && currentList.isEmpty()) {
                binding.tvEmptyMessage.visibility = View.VISIBLE
                binding.tvEmptyMessage.text = "No results found"
            } else {
                binding.tvEmptyMessage.visibility = View.GONE
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
}
