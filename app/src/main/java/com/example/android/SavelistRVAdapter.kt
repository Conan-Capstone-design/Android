package com.example.android

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.connection.RetrofitClient
import com.example.android.databinding.ItemSavelistBinding
class SavelistRVAdapter(private val itemList: List<RetrofitClient.TTSListGet>) : RecyclerView.Adapter<SavelistRVAdapter.ViewHolder>() {
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
        private val imageView: ImageView = itemView.findViewById(R.id.character01)
        fun bind(item: RetrofitClient.TTSListGet) {
            textView.text = item.character
            // 날짜 포맷 변경 (선택사항)
            val date = item.createdAt.substringBefore("T").replace("-", ".")
            textView2.text = date

            // 캐릭터 이름에 따라 이미지 설정
            val imageResId = when (item.character) {
                "코난" -> R.drawable.a_conan
                "짱구" -> R.drawable.shinchangtts02
                "케로로" -> R.drawable.charoro
                else -> R.drawable.default_icon // 기본 이미지 (없으면 새로 하나 만들어도 돼)
            }
            imageView.setImageResource(imageResId)

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