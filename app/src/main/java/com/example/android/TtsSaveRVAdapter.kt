package com.example.android

import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.connection.RetrofitClient
import com.example.android.databinding.ItemRecordlistBinding

// 데이터 모델
class TtsSaveRVAdapter(
    private val onDeleteClick: (voiceId: Int, position: Int) -> Unit
) : RecyclerView.Adapter<TtsSaveRVAdapter.ChatViewHolder>() {

    private val itemList = mutableListOf<RetrofitClient.VoiceItem>()
    fun updateChatList(newList: List<RetrofitClient.VoiceItem>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position in itemList.indices) {
            itemList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TtsSaveRVAdapter.ChatViewHolder {
        val binding =
            ItemRecordlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TtsSaveRVAdapter.ChatViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size


    inner class ChatViewHolder(private val binding: ItemRecordlistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isPlaying = false
        private var mediaPlayer: MediaPlayer? = null

        fun bind(item: RetrofitClient.VoiceItem) = with(binding) {
            itemChattitleTv.text = item.title
            itemDateTv.text = item.createdAt.substringBefore("T").replace("-", ".")

            updateUI()

            character01Layout.setOnClickListener {
                if (isPlaying) {
                    stopPlayback()
                } else {
                    startPlayback(item.voiceUrl)
                }
                isPlaying = !isPlaying
                updateUI()
            }

            imageView20.setOnClickListener {
                val voiceId = item.voiceId
                if (voiceId != null) {
                    onDeleteClick(voiceId, adapterPosition)
                }
            }

        }

        private fun startPlayback(url: String) {
            mediaPlayer = MediaPlayer()
            try {
                mediaPlayer?.apply {
                    setDataSource(url)
                    setOnPreparedListener {
                        start()
                    }
                    setOnCompletionListener {
                        this@ChatViewHolder.isPlaying = false
                        this@ChatViewHolder.updateUI()
                        release()
                        this@ChatViewHolder.mediaPlayer = null
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                Log.e("MediaPlayerError", "Failed to play audio: ${e.message}")
            }
        }

        private fun stopPlayback() {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }

        private fun updateUI() = with(binding) {
            character01Layout.setImageResource(
                if (isPlaying) R.drawable.ic_pause_blue else R.drawable.ic_start
            )
            root.setBackgroundResource(
                if (isPlaying) R.drawable.border_highlight_conan else R.drawable.border_highlight_gray
            )
            imageView20.setImageResource(
                if (isPlaying) R.drawable.trash_blue else R.drawable.trash_gray
            )

            val textColor = if (isPlaying) {
                root.context.getColor(R.color.conan03)
            } else {
                root.context.getColor(R.color.Gray800)
            }
            itemChattitleTv.setTextColor(textColor)
            itemDateTv.setTextColor(textColor)
        }
    }
}