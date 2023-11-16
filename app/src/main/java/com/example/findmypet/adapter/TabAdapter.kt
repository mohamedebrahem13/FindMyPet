package com.example.findmypet.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.findmypet.ui.allposts.AllPostsFragment
import com.example.findmypet.ui.favoritePost.FavoritePostFragment
import com.example.findmypet.ui.postsById.PostsByIdFragment

class TabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AllPostsFragment()
            1 -> PostsByIdFragment()
            2 -> FavoritePostFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
