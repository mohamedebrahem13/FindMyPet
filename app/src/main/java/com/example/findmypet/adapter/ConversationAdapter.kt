package com.example.findmypet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.findmypet.data.model.DisplayConversation
import com.example.findmypet.databinding.ItemConversationBinding
class ConversationListAdapter(
    private val conversationClickListener: ConversationClickListener
) : ListAdapter<DisplayConversation, ConversationListAdapter.ConversationViewHolder>(ConversationDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemConversationBinding.inflate(layoutInflater, parent, false)
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val displayConversation = getItem(position)
        holder.bind(displayConversation, conversationClickListener)
    }

    inner class ConversationViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(displayConversation: DisplayConversation, conversationClickListener: ConversationClickListener) {
            binding.displayConversation = displayConversation
            binding.executePendingBindings()
            binding.clickListener = conversationClickListener
        }
    }

    class ConversationDiffUtil : DiffUtil.ItemCallback<DisplayConversation>() {
        override fun areItemsTheSame(oldItem: DisplayConversation, newItem: DisplayConversation): Boolean {
            return oldItem.channelId == newItem.channelId
        }

        override fun areContentsTheSame(oldItem: DisplayConversation, newItem: DisplayConversation): Boolean {
            return oldItem == newItem
        }
    }

    class ConversationClickListener(val clickListener: (displayConversation: DisplayConversation) -> Unit) {
        fun onClick(displayConversation: DisplayConversation) = clickListener(displayConversation)
    }
}