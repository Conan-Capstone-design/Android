package com.example.android

import android.content.Context
import android.content.Intent
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
    private var selectedImageResId: Int = R.drawable.tts_profile

    private var selectedCharacterIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTtsBinding.inflate(inflater, container, false)
        binding.character01Layout.setOnClickListener {
            updateSelection(1,
                binding.character01Layout,
                binding.character01Background,
                binding.conanTitle
            )
        }

        binding.character02Layout.setOnClickListener {
            updateSelection(2,
                binding.character02Layout,
                binding.character02Background,
                binding.character02Title
            )
        }

        binding.character03Layout.setOnClickListener {
            updateSelection(3,
                binding.character03Layout,
                binding.character03Background,
                binding.character03Title
            )
        }

        binding.imageView11.setOnClickListener {
            if (selectedCharacterIndex == 0) {
                // 아무 캐릭터도 선택되지 않으면 화면 전환 안 함
                return@setOnClickListener
            }

            val characterName = binding.character.text.toString()
            val intent = Intent(requireContext(), TtsActivity::class.java)
            intent.putExtra("character_name", characterName)
            intent.putExtra("character_id", selectedCharacterIndex) // 1, 2, 3
            intent.putExtra("image_res_id", selectedImageResId)
            startActivity(intent)
        }

        return binding.root
    }

    private fun updateSelection(
        newIndex: Int,
        newLayout: ImageView,
        newBackground: View,
        newTitle: TextView
    ) {
        if (selectedCharacterIndex == newIndex) {
            // 이미 선택된 걸 다시 클릭하면 → 해제
            resetCheckState(newLayout, newBackground, newTitle)
            selectedCharacterIndex = 0
            binding.imageView11.setImageResource(R.drawable.tts_createtab)
            binding.textView33.visibility = View.VISIBLE
            binding.characterlayout.visibility = View.GONE
            binding.soldoutlayout.setImageResource(R.drawable.tts_smile) // 기본 이미지로 되돌림
            return
        }

        // 기존 선택 해제
        when (selectedCharacterIndex) {
            1 -> resetCheckState(binding.character01Layout, binding.character01Background, binding.conanTitle)
            2 -> resetCheckState(binding.character02Layout, binding.character02Background, binding.character02Title)
            3 -> resetCheckState(binding.character03Layout, binding.character03Background, binding.character03Title)
        }

        // 새로운 선택 적용
        selectedCharacterIndex = newIndex
        newLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.conan03))
        newBackground.visibility = View.GONE
        binding.textView33.visibility = View.GONE
        binding.characterlayout.visibility = View.VISIBLE
        newTitle.setBackgroundResource(R.drawable.roundtab__character_title_blue)
        newTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.conan03))
        binding.imageView11.setImageResource(R.drawable.tts_createtab_blue)

        selectedImageResId = when (newIndex) {
            1 -> R.drawable.a_conan         // 코난
            2 -> R.drawable.shinchangtts02  // 짱구
            3 -> R.drawable.charoro         // 케로로
            else -> R.drawable.tts_profile  // 기본값
        }

        val characterName = when (newIndex) {
            1 -> "코난"
            2 -> "짱구"
            3 -> "케로로"
            else -> ""
        }
        binding.character.text = characterName
        binding.soldoutlayout.setImageResource(selectedImageResId)
    }
    private fun resetCheckState(
        layout: ImageView,
        background: View,
        title: TextView
    ) {
        layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.conan01))
        background.visibility = View.VISIBLE
        title.setBackgroundResource(R.drawable.roundtab__character_title)
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.Gray800))
    }
}