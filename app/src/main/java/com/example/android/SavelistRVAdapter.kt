package com.example.android

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.databinding.ItemSavelistBinding

data class SavelistModel(
    val characterName: String,
    val lastMessage: String,
    val imageResId: Int
)
class SavelistRVAdapter : RecyclerView.Adapter<SavelistRVAdapter.ChatViewHolder>() {

    private val saveList = mutableListOf<SavelistModel>()

    fun updateChatList(newChatList: List<SavelistModel>) {
        saveList.clear()
        saveList.addAll(newChatList)
        notifyDataSetChanged()
    }

    fun addItem(item: SavelistModel) {
        saveList.add(item)
        notifyItemInserted(saveList.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemSavelistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(saveList[position])
    }

    override fun getItemCount(): Int = saveList.size

    inner class ChatViewHolder(private val binding: ItemSavelistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatRoom: SavelistModel) {
            binding.itemChattitleTv.text = chatRoom.characterName
            binding.itemDateTv.text = chatRoom.lastMessage
            binding.character01.setImageResource(chatRoom.imageResId)

            binding.imageView21.setOnClickListener {
                val intent = Intent(binding.root.context, MypageRecordActivity::class.java).apply {
                    putExtra("character_name", chatRoom.characterName)
                }
                binding.root.context.startActivity(intent)
            }
        }
    }
}