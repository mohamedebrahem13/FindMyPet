package com.example.findmypet.ui.profile.edit

import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.findmypet.common.Resource
import com.example.findmypet.common.UpdateUserProfileResponse
import com.example.findmypet.data.model.User
import com.example.findmypet.databinding.FragmentProfileEditBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileEdit : Fragment() {
    private lateinit var binding: FragmentProfileEditBinding
    private  var imageUri: Uri? =null
    private lateinit var imagePicker: ActivityResultLauncher<String>
    private val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    private val profileEditViewModel: ProfileEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the image picker with the GetContent contract
        imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                binding.imageView.setImageURI(uri)
                imageUri =  uri
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileEditBinding.inflate(inflater)

        binding.imageView.setOnClickListener {
            checkPermissionAndOpenGallery()

        }
        binding.update.setOnClickListener {
                   if (checkAllFields()){
                       profileEditViewModel.updateProfile(imageUri, User ( email = binding.editTextTextEmailAddress2.text.toString(),
                           nickname = binding.editTextTextPersonName3.text.toString(),
                           phoneNumber = binding.editTextPhone.text.toString()))
                       updateProfileObserver()
                       initObserver()

                   }

        }

        with(binding){
            user =ProfileEditArgs.fromBundle(requireArguments()).userProfile
        }


        return binding.root
    }

    private fun updateProfileObserver(){
        lifecycleScope.launchWhenStarted {
            profileEditViewModel.updateProfileResult.collect { result ->
                when (result) {
                    is UpdateUserProfileResponse.Loading -> {
                        binding.prograss.visibility = View.VISIBLE
                        // Handle loading state
                    }
                    is UpdateUserProfileResponse.Success -> {
                        // Handle success state
                        binding.prograss.visibility = View.GONE
                        Toast.makeText(context, "success update profile ", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()

                    }
                    is UpdateUserProfileResponse.Error -> {
                        binding.prograss.visibility = View.GONE
                        // Handle error state
                        val error = result.throwable
                        Toast.makeText(context,error.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }







    private fun initObserver() {
        // Observe the progress and update UI accordingly
        lifecycleScope.launchWhenResumed {
            profileEditViewModel.progress.collectLatest { progress ->
                when (progress) {
                    is Resource.Loading -> {
                        // Show progress bar
                        binding.prograss.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        // Hide progress bar and show success message
                        binding.prograss.visibility = View.GONE
                        Toast.makeText(context, progress.data, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        // Hide progress bar and show error message
                        binding.prograss.visibility = View.GONE
                        Toast.makeText(context, progress.throwable.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                    }
                }
            }
        }
    }




    private fun checkAllFields():Boolean {
        with(binding){
            if (editTextTextEmailAddress2.text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(
                    editTextTextEmailAddress2.text
                ).matches()) {
                editTextTextEmailAddress2.error = "please enter a valid email"
                return false
            }

            if (editTextTextPersonName3.text.isNullOrEmpty()) {
                editTextTextPersonName3.error = "please enter a valid name"
                return false
            }

            if (editTextPhone.text.isNullOrEmpty() || editTextPhone.length()<11) {
                editTextPhone.error = "phone must be 11 number"
                return false
            }
        }
        return true
    }









    private fun checkPermissionAndOpenGallery() {
        val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
        if (PermissionChecker.checkSelfPermission(requireContext(), permission) == PermissionChecker.PERMISSION_GRANTED) {
            openGallery()
        } else {
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {
        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun openGallery() {
        // Use the modern approach for all Android versions
        imagePicker.launch("image/*") // You can specify the MIME type here if needed
    }
}
