package com.example.android

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.connection.RetrofitClient
import com.example.android.databinding.ActivityChatlistBinding
import com.example.android.databinding.ActivityMypagesavelistBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.android.connection.RetrofitObject


class MypageSaveActivity: AppCompatActivity() {
    private lateinit var user: SharedPreferences
    private lateinit var token: String
    private lateinit var binding: ActivityMypagesavelistBinding
    private lateinit var chatListAdapter: SavelistRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypagesavelistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 사용자 SharedPreferences 초기화
        user = MyApplication.getUser()
        token = user.getString("jwt", "").toString()
        binding.listBackBtn.setOnClickListener {
            finish()
        }

        //리사이클러뷰 어댑터
//        // Initialize RecyclerView and adapter
//        chatListAdapter = SavelistRVAdapter()
//        binding.listListRv.layoutManager = LinearLayoutManager(this)
//        binding.listListRv.adapter = chatListAdapter

//        val conanItem = SavelistModel(
//            "짱구",
//            "2025.04.30",
//            R.drawable.shinchangtts02
//        )
//        chatListAdapter.addItem(conanItem)
        loadData()
    }

    private fun loadData() {
        val call = RetrofitObject.getRetrofitService.MypageTTSlistGet(token)
        call.enqueue(object : Callback<RetrofitClient.ResponseTTSListGet> {
            override fun onResponse(call: Call<RetrofitClient.ResponseTTSListGet>, response: Response<RetrofitClient.ResponseTTSListGet>) {
                if (response.isSuccessful) {
                    Log.d("Retrofit5", response.toString())
                    // response.body()를 통해 Responsegetmypage 객체에 접근
                    val profileData = response.body()?.result
                    // 식물 데이터가 없으면 'textViewnot'을 보이도록 처리
                    if (profileData.isNullOrEmpty()) {
                        binding.textView70.visibility = View.VISIBLE
                        binding.listListRv.visibility = View.GONE
                    } else {
                        binding.textView70.visibility = View.GONE
                        binding.listListRv.visibility = View.VISIBLE
                        setupRecyclerView(profileData)
                    }
                } else {
                    Toast.makeText(
                        this@MypageSaveActivity,
                        response.body()?.message ?: "Unknown error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onFailure(call: Call<RetrofitClient.ResponseTTSListGet>, t: Throwable) {
                val errorMessage = "Call Failed: ${t.message}"
                Log.d("Retrofit5", errorMessage)
            }
        })
    }

    private fun setupRecyclerView(TTSData: List<RetrofitClient.TTSListGet>) {
        // RecyclerView의 레이아웃 매니저 설정
        binding.listListRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        // RecyclerView의 어댑터 설정
        chatListAdapter = SavelistRVAdapter(TTSData)
        binding.listListRv.adapter = chatListAdapter
    }
}