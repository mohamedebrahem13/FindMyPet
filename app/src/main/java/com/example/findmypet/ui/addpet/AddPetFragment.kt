package com.example.findmypet.ui.addpet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findmypet.R
import com.example.findmypet.adapter.ImageAdapter
import com.example.findmypet.common.Resource
import com.example.findmypet.data.model.Post
import com.example.findmypet.data.model.User
import com.example.findmypet.databinding.FragmentAddpetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddPetFragment : Fragment(), ImageAdapter.OnImageClickListener {
    private var currentUser: User? =null
    private var selectedGender: String? = null
    private var selectedCity: String? = null
    private var imageAdapter:ImageAdapter?=null
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
        imagePicker = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                if (postViewModel.selectedImageUrisFlow.value.size + uris.size > MAX_IMAGES) {
                    Toast.makeText(requireContext(), "You can select a maximum of 8 images", Toast.LENGTH_SHORT).show()
                } else {
                    // add selecting new images
                    postViewModel.updateSelectedImageUris(uris)
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
        fetchUserProfile()
        initObservers()

        radioCheck()
        setSpinner()
        val defaultCity = getString(R.string.default_city)
        val cityArray = resources.getStringArray(R.array.egypt_cities)
        val defaultCityIndex = cityArray.indexOf(defaultCity)
        binding.spinner.setSelection(defaultCityIndex)

        observeSelectedImageUris()


        binding.post.setOnClickListener {
            if (checkAllFields()){
                postViewModel.addPostWithImages(Post(
                    pet_name = binding.editTextTextPersonName.text.toString(), pet_description = binding.PetDescription.text.toString(), pet_age = binding.editTextNumber.text.toString(), pet_gender = selectedGender.toString(),null,selectedCity.toString(),null,
                    user = currentUser ), postViewModel.selectedImageUrisFlow.value )
                addPetObserver()
            }
        }

        binding.TakePet.setOnClickListener {
            checkPermissionAndOpenGallery()
        }

        return binding.root
    }









    private fun fetchUserProfile() {
        lifecycleScope.launchWhenResumed {
            postViewModel.getCurrentUser() // Trigger a data refresh when the fragment is resumed
        }    }



    private fun radioCheck(){
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
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


    private fun observeSelectedImageUris() {
        viewLifecycleOwner.lifecycleScope.launch {
            postViewModel.selectedImageUrisFlow.collect { uris ->
                if (uris.isNotEmpty()) {
                    // Hide "Take Pet" button and "Take Pet Image"
                    binding.TakePet.visibility = View.GONE
                    binding.takePetImage.visibility = View.GONE

                    // Show the RecyclerView
                    binding.recyclerView.visibility = View.VISIBLE
                    setupRecyclerView()
                } else {
                    // Show "Take Pet" button and "Take Pet Image"
                    binding.TakePet.visibility = View.VISIBLE
                    binding.takePetImage.visibility = View.VISIBLE

                    // Hide the RecyclerView
                    binding.recyclerView.visibility = View.GONE
                }
            }
        }
    }



    private fun setupRecyclerView() {
         imageAdapter = ImageAdapter(postViewModel.selectedImageUrisFlow.value.map { it.toString() },this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
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
                        binding.prograss.visibility = View.GONE
                        findNavController().navigateUp()

                    }
                    is Resource.Error -> {
                        val throwable = result.throwable
                        Toast.makeText(context,
                            "failed add the pet check your internet$throwable", Toast.LENGTH_SHORT).show()
                        binding.prograss.visibility = View.GONE

                        // Handle failure (e.g., display an error message)
                    }
                }
            }


        }

    }

    private fun initObservers() {
        with(binding) {
            with(postViewModel) {
                lifecycleScope.launchWhenResumed {
                    currentUser.collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                this@AddPetFragment.currentUser = resource.data
                                prograss.visibility = View.GONE
                            }
                            is Resource.Error -> {
                                Log.v("profile", resource.toString())
                                prograss.visibility = View.GONE
                            }
                            is Resource.Loading -> {
                                prograss.visibility = View.VISIBLE
                            }
                            else -> {
                                // Handle other states if necessary
                            }
                        }
                    }
                }
            }
        }
    }





    private fun checkAllFields():Boolean {
        with(binding){
            if(postViewModel.selectedImageUrisFlow.value.isEmpty()){
                Toast.makeText(context, "Please select the pet images", Toast.LENGTH_SHORT).show()
                return false
            }
            if (editTextTextPersonName.text.isNullOrEmpty()
            ) {
                editTextTextPersonName.error = "please enter pet name"
                return false
            }
            if (!editTextNumber.text.isNullOrEmpty()) {
                val age = editTextNumber.text.toString().toInt()
                if (age > 20) {
                    editTextNumber.error = "Please enter a valid age (20 or less)"
                    return false
                }
            } else {
                editTextNumber.error = "Please enter pet age"
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



    companion object{
        private const val MAX_IMAGES = 8

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
        if (postViewModel.selectedImageUrisFlow.value.size < MAX_IMAGES) {
            // Use the modern approach for all Android versions
            imagePicker.launch("image/*") // You can specify the MIME type here if needed
        }
    }

    override fun onImageClick(position: Int) {
        postViewModel.removeImageUriAtPosition(position)
        imageAdapter?.notifyItemRemoved(position)
    }


}





