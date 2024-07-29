package com.example.findmypet.ui.addpet

import android.annotation.SuppressLint
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findmypet.R
import com.example.findmypet.activities.PetActivity
import com.example.findmypet.adapter.ImageAdapter
import com.example.findmypet.common.Resource
import com.example.findmypet.common.ToastUtils
import com.example.findmypet.data.model.Post
import com.example.findmypet.data.model.User
import com.example.findmypet.databinding.FragmentAddpetBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddPetFragment : Fragment(), ImageAdapter.OnImageClickListener {


    private var currentUser: User? =null
    private var selectedGender: String? = null
    private var selectedCity: String? = null
    private var imageAdapter:ImageAdapter?=null
    private val postViewModel:PostViewModel by viewModels()
    private lateinit var parentView: ViewGroup

    private lateinit var imagePicker: ActivityResultLauncher<String>
    private val mediaPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    private val legacyStoragePermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(requireContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    private lateinit var binding:FragmentAddpetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePicker = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                if (postViewModel.selectedImageUrisFlow.value.size + uris.size > MAX_IMAGES) {
                    Toast.makeText(requireContext(), R.string.max_8_images_selected, Toast.LENGTH_SHORT).show()
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
        // Call the method to show the preloaded ad from the activity
        (activity as? PetActivity)?.showAdInFragment()
        fetchUserProfile()
        initObservers()

        radioCheck()
        setSpinner()
        val defaultCity = getString(R.string.default_city)
        val cityArray = resources.getStringArray(R.array.egypt_cities)
        val defaultCityIndex = cityArray.indexOf(defaultCity)
        binding.spinner.setSelection(defaultCityIndex)

        observeSelectedImageUris()
        observePostCount()

        binding.TakePet.setOnClickListener {
            checkPermissionAndOpenGallery()
        }
        binding.buttonBack.setOnClickListener{
            findNavController().navigateUp()
        }

        return binding.root
    }






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentView = requireActivity().findViewById(android.R.id.content)

    }



    private fun fetchUserProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                postViewModel.getCurrentUser()
            }
        }

    }



    private fun radioCheck(){
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.girl -> {
                    selectedGender = "Girl"
                    Toast.makeText(requireContext(), getString(R.string.girl_selected), Toast.LENGTH_SHORT).show()

                }
                R.id.boy ->{
                    selectedGender = "Boy"
                    Toast.makeText(requireContext(), getString(R.string.boy_selected), Toast.LENGTH_SHORT).show()
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
    private fun handlePostCount(postCount: Int) {
        if (postCount >= 10) {
            ToastUtils.showCustomToast(requireContext(), getString(R.string.max_post_reached_message), parentView, false,Toast.LENGTH_LONG)

        } else {
            // Proceed with adding the post if the user's post count is less than 10
            setAddPostClickListener()
        }
    }

    private fun setAddPostClickListener() {
        binding.post.setOnClickListener {
            if (checkAllFields()) {
                postViewModel.addPostWithImages(
                    Post(
                        pet_name = binding.editTextTextPersonName.text.toString(),
                        pet_description = binding.PetDescription.text.toString(),
                        pet_age = binding.editTextNumber.text.toString(),
                        pet_gender = selectedGender.toString(),
                        null,
                        selectedCity.toString(),
                        null,
                        user = currentUser
                    ),
                    postViewModel.selectedImageUrisFlow.value
                )
                addPetObserver()
            }
        }
    }

    private fun observePostCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                postViewModel.postCount.collect { postCount ->
                    handlePostCount(postCount)
                }
            }
        }
    }

    private fun addPetObserver(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                postViewModel.addPostResult.collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            // Show loading indicator
                            binding.prograss.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            // Handle successful post addition
                            ToastUtils.showCustomToast(requireContext(), getString(R.string.success_add_post), parentView, true)

                            binding.prograss.visibility = View.GONE
                            findNavController().navigateUp()
                        }
                        is Resource.Error -> {
                            val throwable = result.throwable.message
                            ToastUtils.showCustomToast(requireContext(), getString(R.string.failed_add_pet_check_internet, throwable), parentView, false)

                            binding.prograss.visibility = View.GONE

                            // Handle failure (e.g., display an error message)
                        }

                        else -> {}
                    }
                }
            }
        }


    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                postViewModel.currentUser.collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            currentUser = resource.data
                            Log.v("current user", resource.toString())
                            binding.prograss.visibility = View.GONE
                        }
                        is Resource.Error -> {
                            Log.v("current user", resource.toString())
                            binding.prograss.visibility = View.GONE
                        }
                        is Resource.Loading -> {
                            binding.prograss.visibility = View.VISIBLE
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
            if(postViewModel.selectedImageUrisFlow.value.isEmpty()){
                Toast.makeText(context, getString(R.string.select_pet_images), Toast.LENGTH_SHORT).show()
                return false
            }
            if (editTextTextPersonName.text.isNullOrEmpty()
            ) {
                editTextTextPersonName.error = getString(R.string.enter_pet_name)
                return false
            }
            if (!editTextNumber.text.isNullOrEmpty()) {
                val age = editTextNumber.text.toString().toInt()
                if (age > 20) {
                    editTextNumber.error = getString(R.string.enter_valid_age)
                    return false
                }
            } else {
                editTextNumber.error = getString(R.string.enter_pet_age)
                return false
            }
            if (selectedGender == null) {
                // Display an error message to the user (e.g., show a Toast)
                Toast.makeText(context, getString(R.string.select_gender), Toast.LENGTH_SHORT).show()
                return false // Don't proceed with creating the post
            }

            if (PetDescription.text.isNullOrEmpty()
            ) {
                PetDescription.error = getString(R.string.enter_pet_description)
                return false
            }

        }
        return true
    }



    companion object{
        private const val MAX_IMAGES = 8

    }
    @SuppressLint("NewApi")
    private fun checkPermissionAndOpenGallery() {
        val permission =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                android.Manifest.permission.READ_MEDIA_IMAGES
            } else {
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            }

        if (PermissionChecker.checkSelfPermission(requireContext(), permission) == PermissionChecker.PERMISSION_GRANTED) {
            openGallery()
        } else {
            requestAppropriatePermission()
        }
    }

    // Function to request appropriate permissions based on API level
    @SuppressLint("NewApi")
    private fun requestAppropriatePermission() {
        val permission =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                android.Manifest.permission.READ_MEDIA_IMAGES
            } else {
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            mediaPermissionLauncher.launch(permission)
        } else {
            legacyStoragePermissionLauncher.launch(permission)
        }
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





