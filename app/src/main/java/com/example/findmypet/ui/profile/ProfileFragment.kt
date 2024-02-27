package com.example.findmypet.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.findmypet.activities.MainActivity
import com.example.findmypet.common.Resource
import com.example.findmypet.common.ToastUtils
import com.example.findmypet.data.model.User
import com.example.findmypet.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var User: User? =null
    private lateinit var binding:FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var parentView: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentProfileBinding.inflate(inflater)

        val args = arguments?.let { ProfileFragmentArgs.fromBundle(it) }
        val userprofile = args?.userprofile

        with(binding){
            if (userprofile != null) {
                // This is a profile view for the user who created the post or user i chat with
                user = userprofile
                btnSignOut.visibility=View.GONE
                editbutton.visibility=View.GONE

                // Handle user data
            } else {
                // This is the profile view for the current user
                btnSignOut.visibility=View.VISIBLE
                editbutton.visibility=View.VISIBLE
                fetchUserProfile()
                initObservers()

            }

        }




        binding.editbutton.setOnClickListener {
            binding.editbutton.setOnClickListener {
                if (User != null) {
                    findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProfileEdit(User!!))
                } else {
                    // Handle the case where User is null (no internet or user data not fetched)
                    Toast.makeText(context, "User data not available. Check your internet connection.", Toast.LENGTH_SHORT).show()
                }
            }


        }

        binding.btnSignOut.setOnClickListener {
            profileViewModel.signOut()
            ToastUtils.showCustomToast(requireContext(), "signOut Success",  parentView,true)
            Intent(requireActivity(), MainActivity::class.java).also {
                    intent -> intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentView = requireActivity().findViewById(android.R.id.content)
    }




    private fun fetchUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                profileViewModel.getCurrentUser()
            }
        }
    }



    private fun initObservers() {
        with(binding) {
            with(profileViewModel) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        currentUser.collect { resource ->
                            when (resource) {
                                is Resource.Success -> {
                                    User = resource.data
                                    user = resource.data
                                    prograss.visibility = View.GONE
                                }
                                is Resource.Error -> {
                                    Log.v("profile", resource.toString())
                                    prograss.visibility = View.GONE
                                }
                                is Resource.Loading -> {
                                    prograss.visibility = View.VISIBLE
                                }
                                else -> {
                                    // Handle other states if necessary
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}