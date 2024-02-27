package com.example.findmypet.ui.signup

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.findmypet.common.Resource
import com.example.findmypet.common.ToastUtils
import com.example.findmypet.data.model.User
import com.example.findmypet.databinding.FragmentSignUp2Binding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private lateinit var binding:FragmentSignUp2Binding
    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var parentView: ViewGroup


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= FragmentSignUp2Binding.inflate(inflater)

        initObservers()

        binding.button.setOnClickListener {
          if (checkAllFields()){
              viewModel.signUpWithEmailAndPassword(
                  User( email = binding.editTextTextEmailAddress3.text.toString(),
                      nickname = binding.editTextTextPersonName2.text.toString(),
                      phone = binding.editTextPhone2.text.toString()),
               binding.editTextTextPassword4.text.toString(),
         )

            }
        }


        binding.Signing.setOnClickListener {
        findNavController().navigateUp()

        }

        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentView = requireActivity().findViewById(android.R.id.content)

    }


    private fun initObservers() {

        with(viewModel) {

            result.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                       binding.prograss.visibility =View.GONE
                        findNavController().navigateUp()
                        Log.v("Success","Success")
                        ToastUtils.showCustomToast(requireContext(),"SignUP Success",  parentView,true)
                    }
                    is Resource.Error -> {
                        binding.prograss.visibility =View.GONE
                        ToastUtils.showCustomToast(requireContext(),"SignUP error ${it.throwable.message.toString()}",  parentView,true)
                        Log.v("error","SignUP error")

                    }
                    is Resource.Loading-> {
                        binding.prograss.visibility = View.VISIBLE


                    }
                    else -> {}
                }
            }
        }
    }



    private fun checkAllFields():Boolean {
        with(binding){
            if (editTextTextEmailAddress3.text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(
                    editTextTextEmailAddress3.text
                ).matches()) {
                editTextTextEmailAddress3.error = "please enter a valid email"
                return false
            }

            if (editTextTextPersonName2.text.isNullOrEmpty()) {
                editTextTextPersonName2.error = "please enter a valid name"
                return false
            }

            if (editTextTextPassword4.text.isNullOrEmpty()|| editTextTextPassword4.length() < 6) {
               editTextTextPassword4.error = "Password must be minimum 6 characters"
                return false
            }

            if (editTextTextPassword5.text.isNullOrEmpty() ) {
                editTextTextPassword5.error = "Password is required"
                return false
            } else if (editTextTextPassword5.length() < 6) {
                editTextTextPassword5.error = "Password must be minimum 6 characters"
                return false
            } else if (editTextTextPassword5.text.toString()!= editTextTextPassword4.text.toString()) {
                editTextTextPassword5.error = "Password must be the same"
                return false
            }
            if (editTextPhone2.text.isNullOrEmpty() || editTextPhone2.length() < 11 || !editTextPhone2.text.startsWith("01")) {
                editTextPhone2.error = "Phone number must start with 01 and be 11 digits long"
                return false
            }


            // after all validation return true.
            return true
        }


    }




}