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

        val startFragmentId = intent.getIntExtra("start_fragment", R.id.bnv_home)
        bottomNavigationView.selectedItemId = startFragmentId

        if (savedInstanceState == null) {
            val fragment = when (startFragmentId) {
                R.id.bnv_chat -> FragmentChat()
                R.id.bnv_tts -> FragmentTTS()
                R.id.bnv_mypage -> FragmentMypage()
                else -> FragmentHome()
            }
            supportFragmentManager
                .beginTransaction()
                .replace(binding.AmainFrame.id, fragment)
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
    fun switchToFragmentTTS() {
        binding.AmainBnv.selectedItemId = R.id.bnv_tts
    }
}