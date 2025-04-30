package com.example.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.databinding.ActivityAgree2Binding
import com.example.android.databinding.ActivityServiceagreeBinding

class AgreeServiceActivity : AppCompatActivity() {
    lateinit var binding: ActivityServiceagreeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceagreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView2.setOnClickListener {
            finish()
        }

    }
}