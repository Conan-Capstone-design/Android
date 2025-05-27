package com.example.android

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.connection.RetrofitClient
import com.example.android.connection.RetrofitObject
import com.example.android.databinding.ActivityAgree2Binding
import com.example.android.databinding.ActivityAgree3Binding
import com.example.android.databinding.ActivityChatBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CharacterChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding
    private lateinit var user: SharedPreferences
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = MyApplication.getUser()
        token = user.getString("jwt", "").toString()

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
        // 채팅 RecyclerView 설정
        val messageAdapter = MessageAdapter()
        binding.chatRv.layoutManager = LinearLayoutManager(this)
        binding.chatRv.adapter = messageAdapter

        // 서버에서 전달받은 이전 메시지들 표시
        val messages = intent.getSerializableExtra("messages") as? ArrayList<RetrofitClient.Message>
        messages?.forEach { msg ->
            if (msg.role == "user") {
                messageAdapter.addItem(MessageModel.SenderMessage(msg.content))
            } else {
                messageAdapter.addItem(MessageModel.ReceiverMessage(msg.content))
            }
        }

        // 메시지 전송
//        binding.btnChatSend.setOnClickListener {
//            val message = binding.chatInputEt.text.toString()
//            if (message.isNotBlank()) {
//                messageAdapter.addItem(MessageModel.SenderMessage(message))
//                binding.chatRv.scrollToPosition(messageAdapter.itemCount - 1)
//                binding.chatInputEt.text.clear()
//            }
//        }
        //전송
        binding.btnChatSend.setOnClickListener {
            val message = binding.chatInputEt.text.toString()
            val chatId = intent.getIntExtra("chat_id", -1)

            if (message.isNotBlank()) {
                messageAdapter.apply {
                    addItem(MessageModel.SenderMessage(message))
                }
                // 메시지 전송 후 입력 필드 초기화
                binding.chatInputEt.text.clear()

                val chatId = intent.getIntExtra("chat_id", -1)

                if (token != null) {
                    sendMessageToAPI(token, chatId, message, messageAdapter)
                } else {
                    Log.e("ChatActivity", "JWT token is missing")
                }
            }
        }
    }
    private fun sendMessageToAPI(token: String, chatId: Int, message: String, messageAdapter: MessageAdapter) {
        val request = RetrofitClient.RequestMessage(chatId, message)
        val call = RetrofitObject.getRetrofitService.sendMessage(token, request)
        Log.d("nowChat", "request: $request")
        call.enqueue(object : Callback<RetrofitClient.ResponseMessage> {
            override fun onResponse(
                call: Call<RetrofitClient.ResponseMessage>,
                response: Response<RetrofitClient.ResponseMessage>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.isSuccess) {
                        // assistant 응답 메시지 받아서 추가
                        val reply = body.result.content
                        Log.d("nowChat", "reply: $reply")
                        messageAdapter.addItem(MessageModel.ReceiverMessage(reply))
                        binding.chatRv.scrollToPosition(messageAdapter.itemCount - 1)
                    } else {
                        Log.e("nowChat", "응답은 왔지만 실패: ${body?.message}")
                    }
                } else {
                    Log.e("nowChat", "HTTP 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RetrofitClient.ResponseMessage>, t: Throwable) {
                Log.e("nowChat", "API 호출 실패: ${t.message}")
            }
        })
    }
}