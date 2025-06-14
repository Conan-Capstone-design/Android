package com.example.android

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.connection.RetrofitClient
import com.example.android.connection.RetrofitObject
import com.example.android.databinding.ActivityTtssaveBinding
import com.example.android.databinding.DialogDeleteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TtsSaveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTtssaveBinding
    private lateinit var adapter: TtsSaveRVAdapter
    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingIndex: Int? = null
    private var clickedItemPosition = -1
    private var clickedVoiceId = -1
    private lateinit var user: SharedPreferences
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTtssaveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = MyApplication.getUser()
        token = user.getString("jwt", "").toString()

        val character = intent.getStringExtra("character_name") ?: "케로로"
        binding.listTitleTv.text = character

        loadVoiceListFromServer()

        binding.listBackBtn.setOnClickListener {
            finish()
        }

        // 어댑터 초기화 시 콜백 전달
        adapter = TtsSaveRVAdapter { voiceId, position ->
            clickedVoiceId = voiceId
            clickedItemPosition = position
            showDeleteDialog()
        }

        binding.listListRv.layoutManager = LinearLayoutManager(this)
        binding.listListRv.adapter = adapter

        // 임시 데이터 추가
        /*ttssaveList.addAll(
            listOf(
                TtsSaveModel(-1, "코난", "2025-01-12"),
                TtsSaveModel(-2, "케로로", "2025-01-13")
            )
        )*/
//        adapter.notifyDataSetChanged()

    }

    private fun loadVoiceListFromServer() {
        val selectedCharacterId = intent.getIntExtra("character_id", 0) // 전달받은 character_id

        val call = RetrofitObject.getRetrofitService.getAllVoices(token)
        call.enqueue(object : Callback<RetrofitClient.ResponseVoiceAll> {
            override fun onResponse(
                call: Call<RetrofitClient.ResponseVoiceAll>,
                response: Response<RetrofitClient.ResponseVoiceAll>
            ) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val allItems = response.body()?.result ?: emptyList()

                    // character_id가 일치하는 항목만 필터링
                    val filteredItems = allItems.filter { it.characterId == selectedCharacterId }

                    Log.d("RetrofitTTS", "Filtered items (character_id=$selectedCharacterId): $filteredItems")

                    adapter.updateChatList(filteredItems)
                } else {
                    Toast.makeText(this@TtsSaveActivity, "음성 목록 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RetrofitClient.ResponseVoiceAll>, t: Throwable) {
                Toast.makeText(this@TtsSaveActivity, "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun characterNameFromId(id: Int): String = when (id) {
        1 -> "코난"
        2 -> "짱구"
        3 -> "케로로"
        else -> "알 수 없음"
    }

    private fun showDeleteDialog() {
        // 배경 어둡게 처리
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete2, null)
//        val dialogView = DialogDeleteBinding.inflate(LayoutInflater.from(this))

        Log.d("dialogview", "dialogview click")
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

        confirmButton.setOnClickListener {
            if (clickedItemPosition != -1 && clickedVoiceId != -1) {
                RetrofitObject.getRetrofitService
                    .deleteVoice(token, clickedVoiceId)
                    .enqueue(object : Callback<RetrofitClient.ResponseVoiceDelete> {
                        override fun onResponse(
                            call: Call<RetrofitClient.ResponseVoiceDelete>,
                            response: Response<RetrofitClient.ResponseVoiceDelete>
                        ) {
                            if (response.isSuccessful && response.body()?.isSuccess == true) {
                                adapter.removeItem(clickedItemPosition)
                                Toast.makeText(
                                    this@TtsSaveActivity,
                                    "삭제되었습니다",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@TtsSaveActivity,
                                    "삭제 실패",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            resetDeleteState()
                        }

                        override fun onFailure(
                            call: Call<RetrofitClient.ResponseVoiceDelete>,
                            t: Throwable
                        ) {
                            Toast.makeText(
                                this@TtsSaveActivity,
                                "네트워크 오류: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            resetDeleteState()
                        }
                    })
            }
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun removeDialogOverlay(dialogContainer: FrameLayout) {
        (dialogContainer.parent as? FrameLayout)?.removeView(dialogContainer)
        binding.root.setBackgroundColor(Color.TRANSPARENT)
        binding.root.alpha = 1.0f
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayer?.release()
//        mediaPlayer = null
//    }

    private fun resetDeleteState() {
        clickedItemPosition = -1
        clickedVoiceId = -1
    }
}
