package com.example.petme.ui.signin

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.petme.activities.PetActivity
import com.example.petme.common.Resource
import com.example.petme.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private lateinit var binding :FragmentSignInBinding
    private val viewModel: SignInViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentSignInBinding.inflate(inflater)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()


        viewModelObserver()
        binding.loginbtn.setOnClickListener{
           if (checking()){
               viewModel.signInWithEmailAndPassword(
                   binding.editTextTextEmailAddress.text.toString(),
                   binding.editTextTextPassword.text.toString()
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

    lifecycleScope.launchWhenResumed {
        viewModel.checkCurrentUser.collectLatest { isUserSignedIn ->
            if (isUserSignedIn == true) {
                Log.v("currentUser", "user is signed in")
                Toast.makeText(context, "User is signed in", Toast.LENGTH_SHORT).show()
                Intent(requireActivity(), PetActivity::class.java).also {
                        intent -> intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }                          }
        }
    }

}




    private fun initObservers() {

       lifecycleScope.launch {

           viewModel.result.collectLatest {
        when (it) {
            is Resource.Success -> {
                binding.prograss.visibility = View.GONE
                Toast.makeText(context, "Signing Success", Toast.LENGTH_SHORT).show()
                Intent(requireActivity(), PetActivity::class.java).also {
                    intent -> intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            is Resource.Error -> {
                binding.prograss.visibility = View.GONE
                Toast.makeText(context, "Signing error", Toast.LENGTH_SHORT).show()

            }
            Resource.Loading -> binding.prograss.visibility = View.VISIBLE


            else -> {

            }

        }
    }


}

    }



    private fun checking():Boolean  {
        //check for email and password if null make toast and tell user else navigate to welcome fragment
        with(binding){
            if (editTextTextEmailAddress.text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(
                    editTextTextEmailAddress.text
                ).matches()
            ) {

                editTextTextEmailAddress.error = "please enter a valid email"
                return false
            }
            if (editTextTextPassword.text.isNullOrEmpty() || editTextTextPassword.text.length < 6){
                editTextTextPassword.error = "please enter a valid password"
                return false
            }
            return true

        }

    }


}