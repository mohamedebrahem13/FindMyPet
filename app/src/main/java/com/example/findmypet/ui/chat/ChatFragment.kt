package com.example.findmypet.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findmypet.adapter.MessageAdapter
import com.example.findmypet.common.MessageResource
import com.example.findmypet.data.model.Message
import com.example.findmypet.data.model.User
import com.example.findmypet.databinding.FragmentChatBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var user:User
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        val args = arguments?.let { ChatFragmentArgs.fromBundle(it) }
         user = args?.User!!
        refreshMessagesForUser(userId = user.id.toString())

        // Inflate the custom toolbar layout

        // Set user data to the custom toolbar's binding
        binding.user = user

        // Obtain reference to the custom toolbar view
       // Find the CircleImageView in the custom toolbar layout
        binding.profileImage.setOnClickListener {
            Toast.makeText(requireContext(), user.nickname, Toast.LENGTH_SHORT).show()
               findNavController().navigate(
                    ChatFragmentDirections.actionChatFragmentToProfileFragment(user)
                )
        }
        binding.buttonBack.setOnClickListener{
            findNavController().navigateUp()
        }


        binding.lifecycleOwner = viewLifecycleOwner
        setupRecyclerView()
        observeMessages()
        chatObserver()
        setupSendButton()

        return binding.root
    }


    private fun refreshMessagesForUser(userId: String) {
        viewModel.refreshMessages(userId)
    }



    private fun chat() {
        if (checking()) {
            val messageText = binding.editTextText2.text.toString()
            viewModel.sendMessageAndInitiate(user.id.toString(), messageText)
        } else {
            // Show a toast message when EditText is empty
            Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show()
        }
    }


    private fun checking():Boolean {
        with(binding) {
            return !editTextText2.text.isNullOrEmpty()
        }
    }

    private fun setupSendButton() {
        binding.send.setOnClickListener {
            chat()
            clearEditText()
        }
    }



    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(viewModel.uid)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
        }
    }

    private fun chatObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.messageFlow.collect { resource ->
                    // Handle the resource here, it will contain the MessageResource state
                    when (resource) {
                        is MessageResource.Success -> {
                            showToast(resource.message)


                        }
                        is MessageResource.Error -> {
                            showToast(resource.throwable.message.toString())
                        }

                    }
                }
            }
        }
    }

    private fun displayMessages(messages: List<Message>) {
        messageAdapter.submitList(messages)
        binding.recyclerView.scrollToPosition(messages.size - 1) // Scrolls to the last item
    }


    private fun observeMessages() {
        lifecycleScope.launch {
            viewModel.messagesState.collect { messages ->
                // Update UI to display the messages
                displayMessages(messages)
            }
        }
    }


    private fun clearEditText() {
        // Assuming editText is your EditText view
        binding.editTextText2.text.clear()
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}