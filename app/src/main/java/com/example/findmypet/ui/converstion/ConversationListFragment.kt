package com.example.findmypet.ui.converstion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findmypet.adapter.ConversationListAdapter
import com.example.findmypet.data.model.DisplayConversation
import com.example.findmypet.data.model.User
import com.example.findmypet.databinding.FragmentConversationListBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ConversationListFragment : Fragment() {
    private lateinit var conversationAdapter: ConversationListAdapter
    private lateinit var binding: FragmentConversationListBinding
    private val viewModel: ConversationListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConversationListBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeConversations()
        return binding.root
    }


    private fun setupRecyclerView() {
        conversationAdapter = ConversationListAdapter(ConversationListAdapter.ConversationClickListener { conversation ->
            Log.v("chat", conversation.channelId)
            Snackbar.make(requireView(), conversation.channelId, Snackbar.LENGTH_SHORT).show()
            findNavController().navigate(
                ConversationListFragmentDirections.actionConversationListFragmentToChatFragment2(
                    User(id = conversation.secondUserId,
                        nickname = conversation.secondUserName,
                        Profile_image = conversation.secondUserImage, email = conversation.secondUserEmile, phone = conversation.secondUserPhone)
                )
            )
        })
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = conversationAdapter
        }
    }

    private fun observeConversations() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.conversations.collect { conversations ->
                    displayConversations(conversations)
                }
            }
        }
    }

    private fun displayConversations(conversations: List<DisplayConversation>) {
        conversationAdapter.submitList(conversations)
    }
}