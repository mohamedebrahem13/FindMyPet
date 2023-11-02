package com.example.petme.ui.addpet

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.petme.R
import com.example.petme.common.Resource
import com.example.petme.data.model.Post
import com.example.petme.databinding.FragmentAddpetBinding


class AddPetFragment : Fragment() {

    private var selectedGender: String? = null
    private var selectedCity: String? = null
    private val selectedImageUris: MutableList<Uri> = mutableListOf()
    private val postViewModel:PostViewModel by viewModels()
    private lateinit var imagePicker: ActivityResultLauncher<String>
    private val permissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    private lateinit var binding:FragmentAddpetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker =
            registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
                if (uris.isNotEmpty()) {
                    // Append the selected URIs to the list
                    selectedImageUris.addAll(uris)
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
        gridlayout()
        setSpinner()
        val defaultCity = getString(R.string.default_city)
        val cityArray = resources.getStringArray(R.array.egypt_cities)
        val defaultCityIndex = cityArray.indexOf(defaultCity)
        binding.spinner.setSelection(defaultCityIndex)


        binding.post.setOnClickListener {
            if (checkAllFields()){
                postViewModel.addPostWithImages(Post(pet_name = binding.editTextTextPersonName.toString(), pet_description = binding.PetDescription.toString(), pet_age = binding.editTextNumber.toString(), pet_gender = selectedGender.toString(),postViewModel.uid,null,selectedCity.toString()),selectedImageUris )
                addPetObserver()
            }
        }

        binding.TakePet.setOnClickListener {
            checkPermissionAndOpenGallery()
        }

        return binding.root
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
                    selectedGender = "Girl"
                    Toast.makeText(this.context, "GIRL Selected", Toast.LENGTH_SHORT)
                        .show()
                }
                R.id.boy ->{
                    selectedGender = "Boy"
                    Toast.makeText(this.context, "boy Selected", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }



    private fun setSpinner() {
        with(binding) {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    // Get the selected city when an item is selected
                    selectedCity = parent?.getItemAtPosition(position).toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle the case where nothing is selected (if needed)
                    selectedCity = getString(R.string.default_city)
                    // You can display a message or perform any other required action here
                }
            }
        }
    }


    private fun gridlayout(){

        with(binding){
            if (selectedImageUris.isNotEmpty()) {
                // Selected image URIs are not empty, so show the GridLayout
                imageGrid.visibility = View.VISIBLE
                takePetImage.visibility = View.GONE
                for (uri in selectedImageUris) {
                    val imageView = ImageView(requireContext())
                    imageView.layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    }
                    displayImage(uri,imageView)

                    imageGrid.addView(imageView)
                }
            } else {
                // Selected image URIs are empty, so hide the GridLayout
                imageGrid.visibility = View.GONE
                takePetImage.visibility = View.VISIBLE
            }


        }

    }





    private fun addPetObserver(){
        lifecycleScope.launchWhenResumed {
            postViewModel.addPostResult.collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        // Show loading indicator
                        binding.prograss.visibility = View.VISIBLE

                    }
                    is Resource.Success -> {
                        // Handle successful post addition
                        Log.v("Success","Success addPost ")
                        Toast.makeText(context, "Success addPost", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        val throwable = result.throwable
                        Toast.makeText(context,throwable.toString(), Toast.LENGTH_SHORT).show()
                        // Handle failure (e.g., display an error message)
                    }
                }
            }


        }

    }





    private fun checkAllFields():Boolean {
        with(binding){
            if(selectedImageUris.isEmpty()){
                Toast.makeText(context, "Please select the pet images", Toast.LENGTH_SHORT).show()
                return false
            }
            if (editTextTextPersonName.text.isNullOrEmpty()
                ) {
                editTextTextPersonName.error = "please enter pet name"
                return false
            }
            if (editTextNumber.text.isNullOrEmpty()
            ) {
                editTextNumber.error = "please enter pet age"
                return false
            }
            if (selectedGender == null) {
                // Display an error message to the user (e.g., show a Toast)
                Toast.makeText(context, "Please select a gender", Toast.LENGTH_SHORT).show()
                return false // Don't proceed with creating the post
            }

            if (PetDescription.text.isNullOrEmpty()
            ) {
                PetDescription.error = "please enter pet Description "
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





