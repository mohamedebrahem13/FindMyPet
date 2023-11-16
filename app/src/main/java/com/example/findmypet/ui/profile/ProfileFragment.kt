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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.findmypet.activities.MainActivity
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.User
import com.example.findmypet.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var User: User? =null
    private lateinit var binding:FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentProfileBinding.inflate(inflater)

        val args = arguments?.let { ProfileFragmentArgs.fromBundle(it) }
        val post = args?.clickedpost

        with(binding){
            if (post != null) {
                // This is a profile view for the user who created the post

                val clickedUser = post.user
                user = clickedUser
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
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProfileEdit(
                    User!!
                ))

        }

        binding.btnSignOut.setOnClickListener {
            profileViewModel.signOut()
            Toast.makeText(context, "signOut Success", Toast.LENGTH_SHORT).show()
            Intent(requireActivity(), MainActivity::class.java).also {
                    intent -> intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }



        return binding.root
    }






    private fun fetchUserProfile() {
        lifecycleScope.launchWhenResumed {
            profileViewModel.getCurrentUser() // Trigger a data refresh when the fragment is resumed
        }    }


    private fun initObservers() {
        with(binding) {
            with(profileViewModel) {
                lifecycleScope.launchWhenResumed {
                    currentUser.collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                user = resource.data
                                User =resource.data
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