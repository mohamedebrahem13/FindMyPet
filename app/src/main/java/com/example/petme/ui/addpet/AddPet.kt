package com.example.petme.ui.addpet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.petme.R
import com.example.petme.databinding.FragmentAddpetBinding


class AddPet : Fragment() {

    private lateinit var imagePicker: ActivityResultLauncher<String>
    private fun isAtLeastAndroid11() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    private lateinit var binding:FragmentAddpetBinding





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isAtLeastAndroid11()) {
            // Initialize the image picker with the GetContent contract
            imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                if (uri != null) {
                    displayImage(uri,binding.TakePet)
                    // Handle the selected image URI
                    // You can use the 'uri' to access the selected image
                }

            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentAddpetBinding.inflate(inflater)

        radioCheck()
        binding.TakePet.setOnClickListener {
            pickImageFromGallery()
        }

        return binding.root
    }



    // Call this function to open the image picker
    private fun pickImageFromGallery() {
        if (isAtLeastAndroid11()) {
            // For Android 11 and higher, use the modern approach
            imagePicker.launch("image/*") // You can specify the MIME type here if needed
        } else {
            // For lower Android versions, use the traditional startActivityForResult
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_IMAGE_PICK)
            } else {
                // Request storage permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION
                )            }
        }
    }
    // Handle the result when using startActivityForResult
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedImage = data?.data
            if (selectedImage != null) {
                displayImage(selectedImage,binding.TakePet)
            }
            // Handle the selected image URI as needed
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
        private const val REQUEST_STORAGE_PERMISSION = 1002
    }

    // Load and display the image using Glide
    private fun displayImage(imageUri: Uri, imageView: ImageView) {
        Glide.with(this)
            .load(imageUri)
            .into(imageView)
    }

    private fun radioCheck(){
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.girl -> {
                    Toast.makeText(this.context, "GIRL Selected", Toast.LENGTH_SHORT)
                        .show()
                }
                R.id.boy ->{
                    Toast.makeText(this.context, "boy Selected", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}





