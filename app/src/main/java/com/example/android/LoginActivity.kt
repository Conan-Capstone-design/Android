package com.example.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.databinding.ActivityLoginBinding
import com.example.android.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private var isChecked: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.textView16.setOnClickListener {
            val intent = Intent(this, AgreefirstActivity::class.java)
            startActivity(intent)
        }
        binding.imageView12.setOnClickListener {
            // 토글 상태 변경
            isChecked = !isChecked
            // 이미지 변경
            if (isChecked) {
                binding.imageView12.setImageResource(R.drawable.btn_checkbox_checked)
            } else {
                binding.imageView12.setImageResource(R.drawable.btn_checkbox)
            }
        }
    }
}