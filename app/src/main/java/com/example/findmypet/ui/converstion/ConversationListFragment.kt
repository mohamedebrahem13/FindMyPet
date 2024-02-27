package com.example.findmypet.ui.converstion

import android.os.Bundle
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
        conversationAdapter = ConversationListAdapter(ConversationListAdapter.ConversationClickListener { displayConversation ->
            findNavController().navigate(
                ConversationListFragmentDirections.actionConversationListFragmentToChatFragment2(
                    User(id = displayConversation.secondUserId,
                        nickname = displayConversation.secondUserName.trim(),
                        imagePath = displayConversation.secondUserImage,
                        email = displayConversation.secondUserEmile,
                        phone = displayConversation.secondUserPhone)
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.conversations.collect { conversations ->
                    displayConversations(conversations)
                }
            }
        }
    }

    private fun displayConversations(conversations: List<DisplayConversation>) {
        if (conversations.isNotEmpty()){
            binding.textView17.visibility=View.GONE
            binding.imageView4.visibility=View.GONE
            conversationAdapter.submitList(conversations)

        }
    }
}