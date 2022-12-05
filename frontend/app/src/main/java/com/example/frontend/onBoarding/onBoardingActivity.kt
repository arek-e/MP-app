package com.example.frontend.onBoarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.frontend.R

import com.example.frontend.databinding.ActivityOnBoardingBinding
import com.example.frontend.utils.Animatoo
import com.google.android.material.tabs.TabLayoutMediator


class onBoardingActivity : AppCompatActivity() {

    private val binding by lazy { ActivityOnBoardingBinding.inflate(layoutInflater) }

    private lateinit var mViewPager: ViewPager2
    private lateinit var textSkip: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mViewPager = binding.viewPager
        mViewPager.adapter = onBoardingAdapter(this,this)

        TabLayoutMediator(binding.pageIndicator, mViewPager) { _, _ -> }.attach()
        textSkip = findViewById(R.id.text_skip)
        textSkip.setOnClickListener {
            finish()
            val intent =
                Intent(applicationContext, finishActivity::class.java)
            startActivity(intent)
            Animatoo.animateSlideLeft(this)


    }
        val btnNextStep: Button = findViewById(R.id.btnNext)

        btnNextStep.setOnClickListener {
            if (getItem() > mViewPager.childCount) {
                //TODO: Do an expliit intent to binhunters front -->
                finish()
                val intent =
                    Intent(applicationContext, finishActivity::class.java)
                startActivity(intent)
                Animatoo.animateSlideLeft(this)
            } else {
                mViewPager.setCurrentItem(getItem() + 1, true)
            }
        }

    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }

}