package com.example.android

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android.connection.RetrofitClient
import com.example.android.connection.RetrofitObject
import com.example.android.databinding.FragmentChatBinding
import com.example.android.databinding.FragmentHomeBinding
import com.example.android.databinding.FragmentTtsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentChat: Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var user: SharedPreferences
    private lateinit var token: String

    // 선택된 캐릭터 인덱스: 0 = 없음, 1~3은 캐릭터 번호
    private var selectedCharacterIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        // 사용자 SharedPreferences 초기화
        user = MyApplication.getUser()
        token = user.getString("jwt", "").toString()
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

//            val characterName = binding.character.text.toString()
//            val intent = Intent(requireContext(), CharacterChatActivity::class.java)
//            intent.putExtra("character_name", characterName)
//            startActivity(intent)

            startChatRoom()
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
            binding.imageView11.setImageResource(R.drawable.roundtab_tts)
            binding.textView33.visibility = View.VISIBLE
            binding.characterlayout.visibility = View.GONE
            binding.soldoutlayout.setImageResource(R.drawable.tts_conan_default) // 기본 이미지로 되돌림
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
        binding.imageView11.setImageResource(R.drawable.roundtab_tts_blue)

        val drawableRes = when (newIndex) {
            1 -> R.drawable.a_conan         // 코난
            2 -> R.drawable.shinchangtts02  // 짱구
            3 -> R.drawable.charoro         // 케로로
            else -> R.drawable.tts_conan_default
        }

        val characterName = when (newIndex) {
            1 -> "코난"
            2 -> "짱구"
            3 -> "케로로"
            else -> ""
        }
        binding.character.text = characterName
        binding.soldoutlayout.setImageResource(drawableRes)
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

    private fun startChatRoom() {
        val characterName = binding.character.text.toString()  // 선택된 캐릭터 이름
        val call = RetrofitObject.getRetrofitService.startChat(token, characterName)
        call.enqueue(object : Callback<RetrofitClient.ResponseStartChat> {
            override fun onResponse(call: Call<RetrofitClient.ResponseStartChat>, response: Response<RetrofitClient.ResponseStartChat>) {
                if (response.isSuccessful) {
                    val startChatResponse = response.body()
                    Log.d("StartChat", "chatresponse: $startChatResponse")
                    if (startChatResponse != null && startChatResponse.isSuccess) {
                        val messages = startChatResponse.result.messages
                        val chatId = startChatResponse.result.chatId
                        val intent = Intent(activity, CharacterChatActivity::class.java)
                        intent.putExtra("messages", ArrayList(messages)) // messages만 넘김
                        intent.putExtra("character_name", characterName)  // 캐릭터 이름 전달
                        intent.putExtra("chatId", chatId)
                        startActivity(intent)
                    } else {
                        Log.e("StartChat", "Failed to create chat room")
                    }
                } else {
                    Log.e("StartChat", "Response error: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<RetrofitClient.ResponseStartChat>, t: Throwable) {
                Log.e("StartChat", "API call failed: ${t.message}")
            }
        })
    }
}