package com.example.KitaJalan.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.KitaJalan.R
import com.example.KitaJalan.databinding.FragmentDetailBinding
import com.squareup.picasso.Picasso
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailFragment : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        val args = arguments
        Picasso.get()
            .load(args?.getString("foto"))
            .error(R.drawable.bali)
            .into(binding.detailImg)

        setupTabsAndViewPager()

        return binding.root
    }

    private fun setupTabsAndViewPager() {
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = object : androidx.viewpager2.adapter.FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment {
                val bundle = arguments
                return when (position) {
                    0 -> FragmentDetailView().apply { arguments = bundle }
                    1 -> MapFragment().apply { arguments = bundle }
                    2 -> CommentFragment().apply { arguments = bundle }
                    else -> throw IllegalStateException("Unexpected position: $position")
                }
            }
        }

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Detail"
                1 -> tab.text = "Map"
                2 -> tab.text = "Komentar"
            }
        }.attach()
    }
}