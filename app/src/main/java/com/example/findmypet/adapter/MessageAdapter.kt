package com.example.findmypet.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.findmypet.R
import com.example.findmypet.data.model.Message
import com.example.findmypet.databinding.ItemMessageBinding

class MessageAdapter(private val currentUser: String) : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMessageBinding.inflate(inflater, parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.message = message
            binding.currentUser = currentUser

            // Set background color based on sender's ID
            val layoutParams = binding.textMessage.layoutParams as LinearLayout.LayoutParams
            val textMessageDateLayoutParams = binding.textMessageDate.layoutParams as LinearLayout.LayoutParams


            if (message.senderId == currentUser) {
                binding.textMessage.setBackgroundResource(R.drawable.bg_message_sender)
                layoutParams.gravity = Gravity.END // Align text to the end (right)
                textMessageDateLayoutParams.gravity = Gravity.END
            } else {
                binding.textMessage.setBackgroundResource(R.drawable.bg_message_receiver)
                layoutParams.gravity = Gravity.START // Align text to the end (right)
                textMessageDateLayoutParams.gravity = Gravity.START

            }

            // Set text color based on sender's ID
            binding.textMessage.setTextColor(
                if (message.senderId == currentUser) {
                    ContextCompat.getColor(binding.root.context, android.R.color.white)
                } else {
                    ContextCompat.getColor(binding.root.context, android.R.color.black)
                }
            )

            binding.executePendingBindings()
        }
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.messageId == newItem.messageId
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}