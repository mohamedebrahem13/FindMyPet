package com.example.petme.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.petme.R
import com.example.petme.common.Resource
import com.example.petme.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding:FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding= FragmentProfileBinding.inflate(inflater)

        initObservers()
        binding.btnSignOut.setOnClickListener {
            profileViewModel.signOut()

        }


        return binding.root
    }



    private fun initObservers() {
        with(binding) {
            with(profileViewModel) {
                lifecycleScope.launchWhenResumed {
                    currentUser.collectLatest { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                user = resource.data
                                statusLoadingWheel.visibility = View.GONE
                            }
                            is Resource.Error -> {
                                Log.v("profile", resource.toString())
                                statusLoadingWheel.visibility = View.GONE
                            }
                            Resource.Loading -> statusLoadingWheel.visibility = View.VISIBLE
                            else -> {
                                // Handle other states if necessary
                            }
                        }
                    }
                }

                lifecycleScope.launch{
                    checkCurrentUser.collect { isUserSignedIn ->
                        if (isUserSignedIn == true) {
                            Log.v("user", "User is here")
                        } else {
                            if (findNavController().currentDestination?.id == R.id.profileFragment) {
                                // The user is not signed in; pop the back stack to remove ProfileFragment
                                findNavController().popBackStack()
                            }
                        }
                    }
                }


            }
        }
    }

}