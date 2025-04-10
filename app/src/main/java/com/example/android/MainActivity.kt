package com.example.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBottomNavigation()
        // 초기 화면을 홈 프래그먼트로 설정
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(binding.AmainFrame.id, FragmentHome())
                .commitAllowingStateLoss()
        }
    }
    private fun initBottomNavigation() {

        bottomNavigationView = binding.AmainBnv

        bottomNavigationView.selectedItemId = R.id.bnv_home

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.bnv_tts -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.AmainFrame.id, FragmentTTS()) // Replace with your fragment
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.bnv_home -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.AmainFrame.id, FragmentHome())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.bnv_chat -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.AmainFrame.id, FragmentChat())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.bnv_mypage -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(binding.AmainFrame.id, FragmentMypage())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }
}