package com.example.frontend.onBoarding

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.frontend.R

class onBoardingAdapter(
    fragmentActivity : FragmentActivity,
    private val context: Context
    ) :
    FragmentStateAdapter(fragmentActivity) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> OnboardingFragment.newInstance(
                    context.resources.getString(R.string.title_1),
                    context.resources.getString(R.string.description_1),
                    R.raw.environment_effect
                )
                1 -> OnboardingFragment.newInstance(
                    context.resources.getString(R.string.title_2),
                    context.resources.getString(R.string.description_2),
                    R.raw.trashbins_recycle
                )
                else -> OnboardingFragment.newInstance(
                    context.resources.getString(R.string.title_3),
                    context.resources.getString(R.string.description_3),
                    R.raw.pink_trash
                )
            }
        }
        override fun getItemCount(): Int {
            return 3
        }
    }

