package com.example.petme.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.petme.common.Resource
import com.example.petme.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

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
             findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToSignInFragment2())        }


        return binding.root
    }



    private fun initObservers() {
        with(binding) {
            with(profileViewModel) {

                checkCurrentUser.observe(viewLifecycleOwner) {
                    if (it!!){
                        Log.v("user","User is here")

                    }
                }


                currentUser.observe(viewLifecycleOwner) {
                    when (it) {
                        is Resource.Success -> {
                            user = it.data
                           statusLoadingWheel.visibility =View.GONE

                        }
                        is Resource.Error -> {
                            Log.v("profile",it.toString())
                          statusLoadingWheel.visibility =View.GONE

                        }
                        Resource.Loading ->  statusLoadingWheel.visibility =View.VISIBLE

                        else -> {


                        }
                    }
                }
            }
        }
    }
}