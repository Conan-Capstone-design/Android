package com.example.android

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.databinding.ItemRecordlistBinding

// 데이터 모델
data class TtsSaveModel(
    val characterName: String,
    val lastMessage: String,
    var isPlaying: Boolean = false
)

class TtsSaveRVAdapter(
    private val saveList: MutableList<TtsSaveModel>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<TtsSaveRVAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemRecordlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(saveList[position])
    }

    override fun getItemCount(): Int = saveList.size

    inner class ChatViewHolder(private val binding: ItemRecordlistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TtsSaveModel) = with(binding) {
            itemChattitleTv.text = item.characterName
            itemDateTv.text = item.lastMessage

            character01Layout.setImageResource(
                if (item.isPlaying) R.drawable.ic_pause_blue else R.drawable.ic_start
            )
            root.setBackgroundResource(
                if (item.isPlaying) R.drawable.border_highlight_conan else R.drawable.border_highlight_gray
            )
            imageView20.setImageResource(
                if (item.isPlaying) R.drawable.trash_blue else R.drawable.trash_gray
            )

            val textColor = if (item.isPlaying) {
                root.context.getColor(R.color.conan03)
            } else {
                root.context.getColor(R.color.Gray800)
            }
            itemChattitleTv.setTextColor(textColor)
            itemDateTv.setTextColor(textColor)

            character01Layout.setOnClickListener {
                item.isPlaying = !item.isPlaying
                notifyItemChanged(adapterPosition)
            }

            imageView20.setOnClickListener {
                onDeleteClick(adapterPosition)
            }
        }
    }
}
