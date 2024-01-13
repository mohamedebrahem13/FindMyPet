package com.example.findmypet.common

import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.findmypet.R
import com.google.android.material.navigation.NavigationView

object DrawerSetupHelper {

    private lateinit var toggle: ActionBarDrawerToggle

    fun setup(
        activity: AppCompatActivity,
        drawerLayout: DrawerLayout,
        navigationView: NavigationView,
        onMenuItemClicked: (MenuItem) -> Unit
    ) {
        toggle = ActionBarDrawerToggle(
            activity,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeButtonEnabled(true)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            onMenuItemClicked(menuItem)
            drawerLayout.closeDrawers()
            true
        }
    }

    fun handleToggleClick(item: MenuItem, drawerLayout: DrawerLayout): Boolean {
        return if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            true
        } else {
            false
        }
    }
}
