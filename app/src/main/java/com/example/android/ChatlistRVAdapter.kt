package com.example.android

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.connection.RetrofitClient
import com.example.android.databinding.ItemChatlistBinding

class ChatlistRVAdapter : RecyclerView.Adapter<ChatlistRVAdapter.ChatViewHolder>() {

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
            binding.itemChattitleTv.text = chatRoom.character
            binding.itemDateTv.text = chatRoom.updated_at

            // 캐릭터 이름에 따라 이미지 리소스 설정
            val imageResId = when (chatRoom.character) {
                "코난" -> R.drawable.a_conan
                "짱구" -> R.drawable.shinchangtts02
                "케로로" -> R.drawable.charoro
                else -> R.drawable.default_icon // 기본 이미지 (없으면 새로 만들어도 됨)
            }
            binding.character01.setImageResource(imageResId)

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, CharacterChatActivity::class.java).apply {
                    putExtra("character_name", chatRoom.character)
                }
                binding.root.context.startActivity(intent)
            }
        }
    }
}