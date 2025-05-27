package com.example.android

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.connection.RetrofitClient

class CharacterListsAdapter(private val itemList: List<RetrofitClient.commentList>) : RecyclerView.Adapter<CharacterListsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_savelist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.item_chattitle_tv)
        private val textView2: TextView = itemView.findViewById(R.id.item_date_tv)

        fun bind(item: RetrofitClient.commentList) {
            textView.text = item.character
            textView2.text = item.created_at

            // 아이템 클릭 이벤트 설정
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, MypageRecordActivity::class.java)
                intent.putExtra("CHARACTER_NICKNAME", item.character)  // nickname을 Intent에 추가
                context.startActivity(intent)
            }
        }
    }
}