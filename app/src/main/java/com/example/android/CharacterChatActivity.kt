package com.example.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.databinding.ActivityAgree2Binding
import com.example.android.databinding.ActivityAgree3Binding
import com.example.android.databinding.ActivityChatBinding

class CharacterChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val characterName = intent.getStringExtra("character_name")
        binding.chatCharacter.text = characterName // 예시로 TextView에 표시

        binding.chatBackBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("start_fragment", R.id.bnv_chat)
            startActivity(intent)
        }

        binding.chatChatlistBtn.setOnClickListener {
            val intent = Intent(this, ChatlistActivity::class.java)
            startActivity(intent)
        }
        val messageAdapter = MessageAdapter()
        binding.chatRv.layoutManager = LinearLayoutManager(this)
        binding.chatRv.adapter = messageAdapter

        val firstm = intent.getStringExtra("firstm") ?: "오늘은 무슨 사건이 벌어질까?"
        messageAdapter.apply {
            addItem(MessageModel.ReceiverMessage(firstm))
        }

        //전송
        binding.btnChatSend.setOnClickListener {
            val message = binding.chatInputEt.text.toString()

            if (message.isNotBlank()) {
                messageAdapter.apply {
                    addItem(MessageModel.SenderMessage(message))
                    binding.chatRv.scrollToPosition(itemCount - 1)
                }
                binding.chatInputEt.text.clear()
            }
        }
    }
}
