package com.example.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.databinding.ActivityAgreefirstBinding
import com.example.android.databinding.ActivityLoginBinding

class AgreefirstActivity : AppCompatActivity() {
    lateinit var binding: ActivityAgreefirstBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgreefirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView10.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
        binding.btnRc1.setOnClickListener {
            val intent = Intent(this, AgreeServiceActivity::class.java)
            startActivity(intent)
        }
        binding.btnRc2.setOnClickListener {
            val intent = Intent(this, Agree2Activity::class.java)
            startActivity(intent)
        }
        binding.btnRc3.setOnClickListener {
            val intent = Intent(this, Agree3Activity::class.java)
            startActivity(intent)
        }
        binding.imageView2.setOnClickListener {
            finish()
        }

    }
}