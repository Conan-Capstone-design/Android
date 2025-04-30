package com.example.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android.databinding.FragmentHomeBinding
import com.example.android.databinding.FragmentTtsBinding

class FragmentTTS: Fragment() {
    private lateinit var binding: FragmentTtsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTtsBinding.inflate(inflater, container, false)

        return binding.root
    }
}