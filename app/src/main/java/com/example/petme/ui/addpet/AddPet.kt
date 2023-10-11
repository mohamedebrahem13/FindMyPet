package com.example.petme.ui.addpet

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.petme.R
import com.example.petme.databinding.FragmentAddpetBinding


class AddPet : Fragment(),MenuProvider {


    private lateinit var binding:FragmentAddpetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentAddpetBinding.inflate(inflater)


        requireActivity().addMenuProvider(this,viewLifecycleOwner)

        binding.button3.setOnClickListener {
            findNavController().navigate(AddPetDirections.actionAddpetToProfileFragment())
        }
        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater .inflate(R.menu.user_menu, menu)    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.signout -> Toast.makeText(this.context, "About Selected", Toast.LENGTH_SHORT)
                .show()
            R.id.developer -> Toast.makeText(this.context, "Settings Selected", Toast.LENGTH_SHORT)
                .show()
            R.id.Profile -> Toast.makeText(this.context, "Exit Selected", Toast.LENGTH_SHORT).show()

        }
                  return true
    }

}
