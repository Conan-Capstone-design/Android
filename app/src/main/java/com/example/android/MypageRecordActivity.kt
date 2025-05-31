package com.example.android

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.android.connection.RetrofitClient
import com.example.android.connection.RetrofitObject
import com.example.android.databinding.ActivityMypagesaveCharacterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MypageRecordActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMypagesaveCharacterBinding
    private lateinit var chatListAdapter: RecordlistRVAdapter
    private lateinit var user: SharedPreferences
    private lateinit var token: String
    private var clickedItemPosition = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypagesaveCharacterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 사용자 SharedPreferences 초기화
        user = MyApplication.getUser()
        token = user.getString("jwt", "").toString()

        val characterName = intent.getStringExtra("CHARACTER_NICKNAME")
        binding.listTitleTv.text = characterName // 예시로 TextView에 표시

        binding.listBackBtn.setOnClickListener {
            finish()
        }

        loadTTS()

        //리사이클러뷰 어댑터
        // 어댑터 초기화 시 콜백 전달
        chatListAdapter = RecordlistRVAdapter { position ->
            clickedItemPosition = position
            showCustomDialog()
        }

        binding.listListRv.layoutManager = LinearLayoutManager(this)
        binding.listListRv.adapter = chatListAdapter
        binding.listListRv.itemAnimator = null

//        val conanItem = RecordlistModel("짱구", "2025.04.30")
//        chatListAdapter.addItem(conanItem)
    }

    private fun loadTTS() {
        val characterName = intent.getStringExtra("CHARACTER_NICKNAME")!!
        val call = RetrofitObject.getRetrofitService.mypageTTSList(token, characterName)
        call.enqueue(object : Callback<RetrofitClient.ResponseMypageTTS> {
            override fun onResponse(call: Call<RetrofitClient.ResponseMypageTTS>, response: Response<RetrofitClient.ResponseMypageTTS>) {
                if (response.isSuccessful) {
                    Log.d("RetrofitTTS", response.toString())
                    // response.body()를 통해 Responsegetmypage 객체에 접근
                    val profileData = response.body()?.result
                    Log.d("RetrofitTTS", profileData.toString())
                    profileData?.let { ttsList ->
                        chatListAdapter.updateChatList(ttsList)
                    }
                } else {
                    Toast.makeText(
                        this@MypageRecordActivity,
                        response.body()?.message ?: "Unknown error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onFailure(call: Call<RetrofitClient.ResponseMypageTTS>, t: Throwable) {
                val errorMessage = "Call Failed: ${t.message}"
                Log.d("RetrofitTTS", errorMessage)
            }
        })
    }

    private fun showCustomDialog() {
        // 다이얼로그 레이아웃을 inflate
        Log.d("MyApp", "showCustomDialog() called")
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete2, null)

        // AlertDialog.Builder를 사용하여 다이얼로그 생성
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val alertDialog: AlertDialog = builder.create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Window 속성을 사용하여 크기 조절
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT // 원하는 폭으로 설정
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT // 원하는 높이로 설정
        alertDialog.window?.attributes = layoutParams

        // 다이얼로그 내부의 ImageButton 참조
        val cancelButton = dialogView.findViewById<ImageButton>(R.id.btnCancel)
        val confirmButton = dialogView.findViewById<ImageButton>(R.id.btnDelete)

        // 취소 버튼 클릭 리스너 설정
        cancelButton.setOnClickListener {
            // 취소 버튼을 눌렀을 때 수행할 동작
            alertDialog.dismiss() // 다이얼로그 닫기
            // 추가적인 작업 수행 가능
        }

        // 확인 버튼 클릭 리스너 설정
        confirmButton.setOnClickListener {
            if (clickedItemPosition != -1) {
                // 아이템 삭제 로직
                // 예시: chatListAdapter에서 해당 아이템 삭제
                chatListAdapter.removeItem(clickedItemPosition)
                Log.d("MyApp", "삭제한 위치: $clickedItemPosition")
                clickedItemPosition = -1
            }
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

}