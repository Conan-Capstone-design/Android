package com.example.android

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.connection.RetrofitClient
import com.example.android.connection.RetrofitObject
import com.example.android.databinding.ItemChatlistBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatlistRVAdapter : RecyclerView.Adapter<ChatlistRVAdapter.ChatViewHolder>() {
    private lateinit var user: SharedPreferences
    private lateinit var token: String
    private val chatList = mutableListOf<RetrofitClient.ChatList>()

    fun updateChatList(newChatList: List<RetrofitClient.ChatList>) {
        chatList.clear()
        chatList.addAll(newChatList)
        notifyDataSetChanged()
    }

    fun addItem(item: RetrofitClient.ChatList) {
        chatList.add(item)
        notifyItemInserted(chatList.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding =
            ItemChatlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int = chatList.size

    inner class ChatViewHolder(private val binding: ItemChatlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatRoom: RetrofitClient.ChatList) {
            user = MyApplication.getUser()
            token = user.getString("jwt", "").toString()

            binding.itemChattitleTv.text = chatRoom.character
            val rawDate = chatRoom.updated_at
            val formattedDate = rawDate.substringBefore("T").replace("-", ".")
            binding.itemDateTv.text = formattedDate


            // 캐릭터 이름에 따라 이미지 리소스 설정
            val imageResId = when (chatRoom.character) {
                "코난" -> R.drawable.a_conan
                "짱구" -> R.drawable.shinchangtts02
                "케로로" -> R.drawable.charoro
                else -> R.drawable.default_icon // 기본 이미지 (없으면 새로 만들어도 됨)
            }
            binding.character01.setImageResource(imageResId)

            binding.root.setOnClickListener {
//                val intent = Intent(binding.root.context, CharacterChatActivity::class.java).apply {
//                    putExtra("character_name", chatRoom.character)
//                }
//                binding.root.context.startActivity(intent)
                startChatRoom(token, chatRoom.character)
            }
        }

        private fun startChatRoom(token: String, characterName: String) {
            val call = RetrofitObject.getRetrofitService.startChat(token, characterName)
            call.enqueue(object : Callback<RetrofitClient.ResponseStartChat> {
                override fun onResponse(
                    call: Call<RetrofitClient.ResponseStartChat>,
                    response: Response<RetrofitClient.ResponseStartChat>
                ) {
                    if (response.isSuccessful) {
                        val startChatResponse = response.body()
                        Log.d("StartChat", "chatresponse: $startChatResponse")
                        if (startChatResponse != null && startChatResponse.isSuccess) {
                            val messages = startChatResponse.result.messages
                            val chatId = startChatResponse.result.chatId
                            val intent = Intent(binding.root.context, CharacterChatActivity::class.java)
                            intent.putExtra("messages", ArrayList(messages))
                            intent.putExtra("character_name", characterName)
                            intent.putExtra("chatId", chatId)
                            binding.root.context.startActivity(intent)
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
}