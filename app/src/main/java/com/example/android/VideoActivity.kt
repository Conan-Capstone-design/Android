package com.example.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.android.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        setSupportActionBar(binding.VideoToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 텍스트 직접 넣었으니까 툴바 기본 타이틀은 숨김

        // 뒤로가기 버튼 클릭 처리
        binding.ibArrowBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        supportFragmentManager.commit {
            replace(binding.VideoFrame.id, FragmentVideo())
        }
    }
}