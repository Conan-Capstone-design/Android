package com.example.android

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.databinding.ItemChatlistBinding

data class ChatlistModel(
    val characterName: String,
    val lastMessage: String,
    val imageResId: Int
)
class ChatlistRVAdapter : RecyclerView.Adapter<ChatlistRVAdapter.ChatViewHolder>() {

    private val chatList = mutableListOf<ChatlistModel>()

    fun updateChatList(newChatList: List<ChatlistModel>) {
        chatList.clear()
        chatList.addAll(newChatList)
        notifyDataSetChanged()
    }

    fun addItem(item: ChatlistModel) {
        chatList.add(item)
        notifyItemInserted(chatList.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int = chatList.size

    inner class ChatViewHolder(private val binding: ItemChatlistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatRoom: ChatlistModel) {
            binding.itemChattitleTv.text = chatRoom.characterName
            binding.itemDateTv.text = chatRoom.lastMessage
            binding.character01.setImageResource(chatRoom.imageResId)

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, CharacterChatActivity::class.java).apply {
                    putExtra("character_name", chatRoom.characterName)
                }
                binding.root.context.startActivity(intent)
            }
        }
    }
}