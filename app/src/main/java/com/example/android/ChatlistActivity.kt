package com.example.android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.databinding.ActivityChatlistBinding


class ChatlistActivity: AppCompatActivity() {

    private lateinit var binding: ActivityChatlistBinding
    private lateinit var chatListAdapter: ChatlistRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listBackBtn.setOnClickListener {
            finish()
        }

        //리사이클러뷰 어댑터
        // Initialize RecyclerView and adapter
        chatListAdapter = ChatlistRVAdapter()
        binding.listListRv.layoutManager = LinearLayoutManager(this)
        binding.listListRv.adapter = chatListAdapter

        val conanItem = ChatlistModel(
            "짱구",
            "2025.04.30",
            R.drawable.shinchangtts02
        )
        chatListAdapter.addItem(conanItem)
    }
}