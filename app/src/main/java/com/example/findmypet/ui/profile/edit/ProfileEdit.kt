package com.example.findmypet.ui.profile.edit

import android.Manifest
import android.net.Uri
import android.os.Build
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.findmypet.R
import com.example.findmypet.common.Resource
import com.example.findmypet.common.UpdateUserProfileResponse
import com.example.findmypet.data.model.User
import com.example.findmypet.databinding.FragmentProfileEditBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileEdit : Fragment() {
    private lateinit var binding: FragmentProfileEditBinding
    private  var imageUri: Uri? =null
    private lateinit var imagePicker: ActivityResultLauncher<String>
    private lateinit var requestPermissions: ActivityResultLauncher<Array<String>>


    private val profileEditViewModel: ProfileEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Handle the permissions result here
            permissions.forEach { (_, isGranted) ->
                if (isGranted) {
                    openGallery()
                } else {
                    Toast.makeText(requireContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }
        }
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
        binding.buttonBack.setOnClickListener{
            findNavController().navigateUp()
        }
        binding.update.setOnClickListener {
                   if (checkAllFields()){
                       profileEditViewModel.updateProfile(imageUri, User ( email = binding.editTextTextEmailAddress2.text.toString(),
                           nickname = binding.editTextTextPersonName3.text.toString(),
                           phone = binding.editTextPhone.text.toString()))
                       updateProfileObserver()
                       initObserver()

                   }

        }

        with(binding){
            user =ProfileEditArgs.fromBundle(requireArguments()).userProfile
        }


        return binding.root
    }

    private fun updateProfileObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileEditViewModel.updateProfileResult.collect { result ->
                    when (result) {
                        is UpdateUserProfileResponse.Loading -> {
                            binding.prograss.visibility = View.VISIBLE
                            // Handle loading state
                        }
                        is UpdateUserProfileResponse.Success -> {
                            // Handle success state
                            binding.prograss.visibility = View.GONE
                            Toast.makeText(context, getString(R.string.success_update_profile), Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                        is UpdateUserProfileResponse.Error -> {
                            binding.prograss.visibility = View.GONE
                            // Handle error state
                            val error = result.throwable
                            Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }
        }
    }






    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
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
                            // Handle other states if necessary
                        }
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
                editTextTextEmailAddress2.error = getString(R.string.please_enter_valid_email)
                return false
            }

            if (editTextTextPersonName3.text.isNullOrEmpty()) {
                editTextTextPersonName3.error = getString(R.string.please_enter_valid_name)
                return false
            }

            if (editTextPhone.text.isNullOrEmpty() || editTextPhone.length()<11) {
                editTextPhone.error = getString(R.string.phone_must_be_11_number)
                return false
            }
        }
        return true
    }








    private fun checkPermissionAndOpenGallery() {
        val permission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                // For API level 34 and above, you need to check and request both permissions
                Manifest.permission.READ_MEDIA_IMAGES
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED

            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                Manifest.permission.READ_MEDIA_IMAGES
            }
            else -> {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        }

        if (PermissionChecker.checkSelfPermission(requireContext(), permission) == PermissionChecker.PERMISSION_GRANTED) {
            openGallery()
        } else {
            requestPermissionsBasedOnVersion()
        }
    }


    private fun requestPermissionsBasedOnVersion() {
        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }
            else -> {
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }

        requestPermissions.launch(permissions)
    }

    private fun openGallery() {
        // Use the modern approach for all Android versions
        imagePicker.launch("image/*") // You can specify the MIME type here if needed
    }
}
