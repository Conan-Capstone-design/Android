package com.example.android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.databinding.ActivityMypagesaveCharacterBinding


class MypageRecordActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMypagesaveCharacterBinding
    private lateinit var chatListAdapter: RecordlistRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypagesaveCharacterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listBackBtn.setOnClickListener {
            finish()
        }

        //리사이클러뷰 어댑터
        // Initialize RecyclerView and adapter
        chatListAdapter = RecordlistRVAdapter()
        binding.listListRv.layoutManager = LinearLayoutManager(this)
        binding.listListRv.adapter = chatListAdapter

        binding.listListRv.itemAnimator = null

        val conanItem = RecordlistModel(
            "짱구",
            "2025.04.30"
        )
        chatListAdapter.addItem(conanItem)
    }
}