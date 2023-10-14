package com.example.petme.ui.addpet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.petme.databinding.FragmentAddpetBinding


class AddPet : Fragment() {


    private lateinit var binding:FragmentAddpetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentAddpetBinding.inflate(inflater)

        binding.button3.setOnClickListener {
//            view?.findNavController()?.navigate(AddPetDirections.actionAddpetToProfileFragment())
        }
        return binding.root
    }



}
