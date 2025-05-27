package com.example.android

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.connection.RetrofitClient
import com.example.android.connection.RetrofitObject
import com.example.android.databinding.ActivityChatlistBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatlistActivity: AppCompatActivity() {
    private lateinit var user: SharedPreferences
    private lateinit var token: String
    private lateinit var binding: ActivityChatlistBinding
    private lateinit var chatListAdapter: ChatlistRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = MyApplication.getUser()
        token = user.getString("jwt", "").toString()

        binding.listBackBtn.setOnClickListener {
            finish()
        }

        //리사이클러뷰 어댑터
        // Initialize RecyclerView and adapter
        chatListAdapter = ChatlistRVAdapter()
        binding.listListRv.layoutManager = LinearLayoutManager(this)
        binding.listListRv.adapter = chatListAdapter

//        val conanItem = ChatlistModel(
//            "짱구",
//            "2025.04.30",
//            R.drawable.shinchangtts02
//        )
//        chatListAdapter.addItem(conanItem)

        loadChatList()
    }

    private fun loadChatList() {
        val call = RetrofitObject.getRetrofitService.chatList(token)
        call.enqueue(object : Callback<RetrofitClient.ResponseCharacterChat> {
            override fun onResponse(
                call: Call<RetrofitClient.ResponseCharacterChat>,
                response: Response<RetrofitClient.ResponseCharacterChat>
            ) {
                if (response.isSuccessful) {
                    val chatListResponse = response.body()
                    if (chatListResponse != null && chatListResponse.isSuccess) {
                        chatListAdapter.updateChatList(chatListResponse.result)
                    } else {
                        Log.e(
                            "ChatlistActivity",
                            "Failed to load chat list: ${chatListResponse?.message}"
                        )
                    }
                } else {
                    Log.e("ChatlistActivity", "Failed to get a valid response")
                }
            }

            override fun onFailure(call: Call<RetrofitClient.ResponseCharacterChat>, t: Throwable) {
                Log.e("ChatlistActivity", "API call failed: ${t.message}")
            }
        })
    }
}