package com.example.findmypet.ui.profile
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.findmypet.R
import com.example.findmypet.activities.MainActivity
import com.example.findmypet.common.Resource
import com.example.findmypet.common.ToastUtils
import com.example.findmypet.data.model.User
import com.example.findmypet.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var currentUser: User? =null
    private lateinit var binding:FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var parentView: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=FragmentProfileBinding.inflate(inflater)

        val args = arguments?.let { ProfileFragmentArgs.fromBundle(it) }
        val userprofile = args?.userprofile

        with(binding){
            if (userprofile != null) {
                // This is a profile view for the user who created the post or user i chat with
                user = userprofile
                menuIcon.visibility=View.GONE
                // Handle user data
            } else {
                // This is the profile view for the current user
                menuIcon.visibility=View.VISIBLE
                fetchUserProfile()
                initObservers()

            }

        }

        binding.buttonBack.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.menuIcon.setOnClickListener { view ->
            val wrapper: Context = ContextThemeWrapper(this.requireContext(), R.style.find_PopupMenu)

            val popupMenu = PopupMenu(wrapper, view)
            popupMenu.inflate(R.menu.profile_menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit_profile -> {
                        if (currentUser != null) {
                            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToProfileEdit(currentUser!!))
                        } else {
                            // Handle the case where User is null (no internet or user data not fetched)
                            Toast.makeText(this.requireContext(),
                                getString(R.string.user_data_not_available_check_your_internet_connection), Toast.LENGTH_SHORT).show()
                        }
                        true
                    }
                    R.id.delete_account -> {
                        // Handle Delete Account action
                        showDeleteConfirmationDialog()
                        true
                    }
                    R.id.sign_out -> {
                        profileViewModel.signOut()
                        ToastUtils.showCustomToast(this.requireContext(),
                            getString(R.string.signout_success),  parentView, true)
                        Intent(this.requireContext(), MainActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()

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
                profileViewModel.getCurrentUser()
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(getString(R.string.delete_account))
        alertDialogBuilder.setMessage(getString(R.string.are_you_sure_you_want_to_delete_your_account))

        alertDialogBuilder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            // User clicked Yes, perform the delete action
            deleteAccount()
        }

        alertDialogBuilder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            // User clicked No, dismiss the dialog
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteAccount() {
        profileViewModel.deleteUserAccount()
        observeDeleteAccountStatus()
    }

    private fun initObservers() {
        with(binding) {
            with(profileViewModel) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        currentUser.collect { resource ->
                            when (resource) {
                                is Resource.Success -> {
                                    this@ProfileFragment.currentUser = resource.data
                                    user = resource.data
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
    }
    private fun observeDeleteAccountStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                profileViewModel.deleteAccountStatus.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            binding.prograss.visibility = View.GONE
                            Toast.makeText(requireContext(),
                                getString(R.string.account_deleted_successfully), Toast.LENGTH_SHORT).show()
                            profileViewModel.signOut()
                            ToastUtils.showCustomToast(requireContext(),
                                getString(R.string.signout_success),  parentView, true)
                            Intent(requireContext(), MainActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                        is Resource.Error -> {
                            binding.prograss.visibility = View.GONE
                            Toast.makeText(requireContext(),
                                getString(R.string.error_deleting_account), Toast.LENGTH_SHORT).show()
                            Log.e("ProfileFragment", "Error deleting account: ${resource.throwable.message}")
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
}