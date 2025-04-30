package com.example.android

import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.android.databinding.ActivitySecessionBinding

class SecessionActivity: AppCompatActivity() {
    lateinit var binding: ActivitySecessionBinding
    private var isChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView10.setOnClickListener {
            finish()
        }
        // 이미지 뷰 클릭 이벤트 리스너 설정
        binding.imageView25.setOnClickListener {
            // 토글 상태 변경
            isChecked = !isChecked

            // 이미지 변경
            if (isChecked) {
                binding.imageView25.setImageResource(R.drawable.btn_checkbox_checked)
                binding.button3.setBackgroundResource(R.drawable.blue_rectangle)
                binding.button3.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                binding.imageView25.setImageResource(R.drawable.btn_checkbox)
                binding.button3.setBackgroundResource(R.drawable.gray_rectangle)
                binding.button3.setTextColor(ContextCompat.getColor(this, R.color.Gray900))
            }
        }
        binding.button3.setOnClickListener {
            val intent =
                Intent(this@SecessionActivity, LoginActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(this@SecessionActivity)
            stackBuilder.addNextIntentWithParentStack(intent)
            stackBuilder.startActivities()
        }
    }
}