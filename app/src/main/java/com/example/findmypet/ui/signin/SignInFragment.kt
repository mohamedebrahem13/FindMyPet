package com.example.findmypet.ui.signin

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.findmypet.R
import com.example.findmypet.activities.PetActivity
import com.example.findmypet.common.Resource
import com.example.findmypet.common.ToastUtils
import com.example.findmypet.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var binding :FragmentSignInBinding
    private val viewModel: SignInViewModel by viewModels()
    private lateinit var parentView: ViewGroup


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentSignInBinding.inflate(inflater)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()


        viewModelObserver()
        binding.loginbtn.setOnClickListener{
           if (checking()){
               viewModel.signInWithEmailAndPassword(
                   binding.etEmail.text.toString(),
                   binding.etPassword.text.toString()
               )
               initObservers()

           }
        }
        binding.signup.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }
        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToForgotPasswordFragment())
        }


        return binding.root
    }


private fun viewModelObserver() {

    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.checkCurrentUser.collectLatest { isUserSignedIn ->
                if (isUserSignedIn == true) {
                    Log.v("currentUser", getString(R.string.user_is_signed_in))
                    ToastUtils.showCustomToast(requireContext(), getString(R.string.user_is_signed_in),  parentView,true)

                    Intent(requireActivity(), PetActivity::class.java).also { intent ->
                        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }
        }
    }


}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentView = requireActivity().findViewById(android.R.id.content)

    }

    private fun initObservers() {

       lifecycleScope.launch {

           viewModel.result.collectLatest {
        when (it) {
            is Resource.Success -> {
                binding.prograss.visibility = View.GONE
                ToastUtils.showCustomToast(requireContext(),
                    getString(R.string.signing_success),  parentView,true)

                Intent(requireActivity(), PetActivity::class.java).also {
                    intent -> intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            is Resource.Error -> {
                binding.prograss.visibility = View.GONE
                ToastUtils.showCustomToast(requireContext(),"Signing error: ${it.throwable.message.toString()}",  parentView,false)

            }
            Resource.Loading -> binding.prograss.visibility = View.VISIBLE


            else -> {

            }

        }
    }


}

    }



    private fun checking(): Boolean {
        with(binding) {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextTextEmailAddress.error = getString(R.string.please_enter_a_valid_email)
                return false
            } else {
                editTextTextEmailAddress.error = null
            }

            if (password.isEmpty() || password.length < 6) {
                editTextTextPassword.error = getString(R.string.please_enter_a_valid_password)
                return false
            } else {
                editTextTextPassword.error = null
            }

            return true
        }
    }


}