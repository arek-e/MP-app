package com.example.frontend.onBoarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.example.frontend.databinding.ActivityFinishBinding

class finishActivity : AppCompatActivity() {
    private val binding  by lazy { ActivityFinishBinding.inflate(layoutInflater) }
    private lateinit var btnStart: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val view = binding.root
        setContentView(view)
        btnStart = binding.layoutStart
        btnStart.setOnClickListener {
            finish()
        }
    }
}