package com.example.android

import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.databinding.FragmentHomeBinding
import com.example.android.databinding.FragmentMypageBinding

class FragmentMypage: Fragment() {
    private lateinit var binding: FragmentMypageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        binding.imageViesecession.setOnClickListener {
            val intent = Intent(requireContext(), SecessionActivity::class.java)
            startActivity(intent)
        }

        binding.imageView13.setOnClickListener {
            val intent = Intent(requireContext(), MypageProfileActivity::class.java)
            startActivity(intent)
        }

        binding.imageVielogout.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(requireContext())
            stackBuilder.addNextIntentWithParentStack(intent)
            stackBuilder.startActivities()
        }
        return binding.root
    }
}