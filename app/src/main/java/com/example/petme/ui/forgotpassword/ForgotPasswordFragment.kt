package com.example.petme.ui.forgotpassword

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.petme.R
import com.example.petme.common.Resource
import com.example.petme.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var binding:FragmentForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding= FragmentForgotPasswordBinding.inflate(inflater)

           binding.send.setOnClickListener {
                if (checking()){
                    viewModel.sendPasswordResetEmail(binding.editTextTextEmailAddress.text.toString())
                    it.findNavController().navigate(R.id.action_forgotPasswordFragment_to_signInFragment2)
                }
            }
        binding.doNotHave.setOnClickListener {
            it.findNavController().navigate(R.id.action_forgotPasswordFragment_to_signUpFragment)
        }
         observer()


        return binding.root

    }




    private fun observer(){
        viewModel.result.observe(viewLifecycleOwner) {
            with(binding){

                when (it) {
                    is Resource.Success -> {
                        statusLoadingWheel.visibility=View.GONE
                        Toast.makeText(context, "Success send please check your emile ", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_forgotPasswordFragment_to_signInFragment2)
                    }

                    is Resource.Error -> {
                        statusLoadingWheel.visibility=View.GONE
                        Toast.makeText(context, "error something wrong ", Toast.LENGTH_SHORT).show()

                    }

                    Resource.Loading ->   statusLoadingWheel.visibility=View.VISIBLE

                    else -> {}
                }

            }
        }

    }

    private fun checking():Boolean{

        with(binding) {
            if (editTextTextEmailAddress.text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(
                    editTextTextEmailAddress.text
                ).matches()
            ) {

                editTextTextEmailAddress.error = "please enter a valid email"
                return false
            }

            return true
        }


    }

    }
