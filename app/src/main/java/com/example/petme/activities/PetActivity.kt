package com.example.petme.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.petme.R
import com.example.petme.databinding.ActivityPetBinding
import dagger.hilt.android.AndroidEntryPoint




@AndroidEntryPoint
class PetActivity : AppCompatActivity() {
    lateinit var binding:ActivityPetBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_pet)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController)
    }





    override fun onSupportNavigateUp(): Boolean {
        if (!navController.popBackStack()) {
            // Call finish() on your Activity
            finish()
        }
        return navController.navigateUp()||super.onSupportNavigateUp()    }
}