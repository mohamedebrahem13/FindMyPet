package com.example.findmypet.ui.forgotpassword

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.findmypet.R
import com.example.findmypet.common.Resource
import com.example.findmypet.common.ToastUtils
import com.example.findmypet.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var binding:FragmentForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private lateinit var parentView: ViewGroup


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentForgotPasswordBinding.inflate(inflater)
        observer()
        binding.send.setOnClickListener {
                if (checking()){
                    viewModel.sendPasswordResetEmail(binding.etEmail.text.toString())
                }
            }
        binding.alreadyHaveAccount.setOnClickListener {
            it.findNavController().navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToSignInFragment())
        }


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentView = requireActivity().findViewById(android.R.id.content)

    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.result.collect { resource ->
                    with(binding) {
                        when (resource) {
                            is Resource.Success -> {
                                prograss.visibility = View.GONE
                                ToastUtils.showCustomToast(
                                    requireContext(),
                                    getString(R.string.success_check_email),
                                    parentView,
                                    true
                                )
                                findNavController().navigate(
                                    ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToSignInFragment()
                                )
                            }
                            is Resource.Error -> {
                                prograss.visibility = View.GONE
                                ToastUtils.showCustomToast(
                                    requireContext(),
                                    getString(R.string.error_something_wrong),
                                    parentView,
                                    false
                                )
                            }
                            is Resource.Loading -> {
                                prograss.visibility = View.VISIBLE
                            }
                            else -> {
                                // Handle other cases if necessary
                            }
                        }
                    }
                }
            }
        }
    }
    private fun checking(): Boolean {
        with(binding) {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextTextEmailAddress.error = getString(R.string.please_enter_valid_email)
                return false
            }

            return true
        }
    }


    }
