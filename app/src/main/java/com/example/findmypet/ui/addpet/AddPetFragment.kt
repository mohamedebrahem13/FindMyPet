package com.example.findmypet.ui.addpet

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
    private lateinit var requestPermissions: ActivityResultLauncher<Array<String>>

    private lateinit var binding:FragmentAddpetBinding

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
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, cityArray)
        val defaultCityIndex = cityArray.indexOf(defaultCity)
        binding.spinner.adapter = adapter
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
                        pet_name = binding.etName.text.toString(),
                        pet_description = binding.etDescription.text.toString(),
                        pet_age = binding.etAge.toString(),
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






    private fun checkAllFields(): Boolean {
        with(binding) {
            // Check if any images are selected
            if (postViewModel.selectedImageUrisFlow.value.isEmpty()) {
                Toast.makeText(context, getString(R.string.select_pet_images), Toast.LENGTH_SHORT).show()
                return false
            }

            // Validate pet name
            val name = etName.text.toString()
            if (name.isEmpty()) {
                editTextTextPersonName2.error = getString(R.string.enter_pet_name)
                return false
            }

            // Validate pet age
            val ageText = etAge.text.toString()
            if (ageText.isEmpty()) {
                editTextNumber.error = getString(R.string.enter_pet_age)
                return false
            } else {
                val age = ageText.toIntOrNull()
                if (age == null || age > 20) {
                    editTextNumber.error = getString(R.string.enter_valid_age)
                    return false
                }
            }

            // Validate selected gender
            if (selectedGender == null) {
                Toast.makeText(context, getString(R.string.select_gender), Toast.LENGTH_SHORT).show()
                return false
            }

            // Validate pet description
            val description = etName.text.toString()

            if (description.isEmpty()) {
                editPetDescription.error = getString(R.string.enter_pet_description)
                return false
            }

        }
        return true
    }



    companion object{
        private const val MAX_IMAGES = 8

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
    // Function to request appropriate permissions based on API level
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





