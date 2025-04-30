package com.example.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.databinding.ActivityAgree2Binding

class Agree2Activity : AppCompatActivity() {
    lateinit var binding: ActivityAgree2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgree2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView2.setOnClickListener {
            finish()
        }

    }
}