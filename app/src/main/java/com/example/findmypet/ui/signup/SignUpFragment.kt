package com.example.findmypet.ui.signup

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.findmypet.common.Resource
import com.example.findmypet.common.ToastUtils
import com.example.findmypet.data.model.User
import com.example.findmypet.databinding.FragmentSignUp2Binding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


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
                  User( email = binding.etEmail.text.toString(),
                      nickname = binding.etName.text.toString(),
                      phone = binding.etPhone.text.toString()),
               binding.etPassword.text.toString(),
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            binding.prograss.visibility = View.GONE
                            findNavController().navigateUp()
                            Log.v("Success", "Sign-up successful")
                            ToastUtils.showCustomToast(requireContext(), "SignUP Success", parentView, true)
                        }
                        is Resource.Error -> {
                            binding.prograss.visibility = View.GONE
                            ToastUtils.showCustomToast(requireContext(), "SignUP error: ${resource.throwable.message}", parentView, true)
                            Log.v("Error", "Sign-up error")
                        }
                        is Resource.Loading -> {
                            binding.prograss.visibility = View.VISIBLE
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun checkAllFields(): Boolean {
        with(binding) {
            // Check email
            val email = etEmail.text.toString()
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextTextEmailAddress3.error = "Please enter a valid email"
                return false
            }

            // Check name
            val name = etName.text.toString()
            if (name.isEmpty()) {
                editTextTextPersonName2.error = "Please enter a valid name"
                return false
            }

            // Check password
            val password = etPassword.text.toString()
            if (password.isEmpty() || password.length < 6) {
                editTextTextPassword4.error = "Password must be at least 6 characters long"
                return false
            }

            // Check password confirmation
            val passwordVerify = etPasswordVerify.text.toString()
            if (passwordVerify.isEmpty()) {
                editTextTextPassword5.error = "Password confirmation is required"
                return false
            } else if (passwordVerify.length < 6) {
                editTextTextPassword5.error = "Password must be at least 6 characters long"
                return false
            } else if (passwordVerify != password) {
                editTextTextPassword5.error = "Passwords must match"
                return false
            }

            // Check phone number
            val phone = etPhone.text.toString()
            if (phone.isEmpty() || phone.length < 11 || !phone.startsWith("01")) {
                editTextPhone2.error = "Phone number must start with 01 and be 11 digits long"
                return false
            }

            // All validations passed
            return true
        }
    }



}