package com.example.frontend.onBoarding

import android.content.Intent
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
        //val intent = Intent(this,MainActivity::class.java)
        btnStart = binding.layoutStart
        btnStart.setOnClickListener {
            btnStart.setOnClickListener { startActivity(intent)}

        }
    }
}


