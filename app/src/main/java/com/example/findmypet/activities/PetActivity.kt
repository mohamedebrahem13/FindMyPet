package com.example.findmypet.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.findmypet.R
import com.example.findmypet.databinding.ActivityPetBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint




@AndroidEntryPoint
class PetActivity : AppCompatActivity() {

    lateinit var binding:ActivityPetBinding
    private lateinit var navController: NavController
    private var interstitialAd: InterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {}


        binding = DataBindingUtil.setContentView(this, R.layout.activity_pet)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController)

        // Load the ad when the activity is created

        loadInterstitialAd()

    }
    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            getString(R.string.ads_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }
    // Function to show the preloaded ad
    fun showAdInFragment() {
        interstitialAd?.show(this)
        // Load a new ad for the next time
        loadInterstitialAd()    }






    override fun onSupportNavigateUp(): Boolean {
        if (!navController.popBackStack()) {
            // Call finish() on your Activity
            finish()
        }
        return navController.navigateUp()||super.onSupportNavigateUp()    }
}