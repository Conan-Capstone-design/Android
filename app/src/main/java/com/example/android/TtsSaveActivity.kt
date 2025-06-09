package com.example.android

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
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
    private val ttssaveList = mutableListOf<TtsSaveModel>()
    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingIndex: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTtssaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listBackBtn.setOnClickListener {
            finish()
        }

        adapter = TtsSaveRVAdapter(
            ttssaveList,
            onDeleteClick = { position -> showDeleteDialog(position) },
            onPlayClick = { position ->

                val item = ttssaveList[position]
                val token = MyApplication.getUser().getString("jwt", null) ?: return@TtsSaveRVAdapter

                // 이미 재생 중인 항목을 다시 누르면 정지
                if (item.isPlaying) {
                    mediaPlayer?.pause()
                    item.isPlaying = false
                    adapter.notifyItemChanged(position)
                    currentlyPlayingIndex = null
                    return@TtsSaveRVAdapter
                }

                // 이전 재생 중인 항목 정지
                currentlyPlayingIndex?.let { idx ->
                    ttssaveList[idx].isPlaying = false
                    adapter.notifyItemChanged(idx)
                }
                mediaPlayer?.release()
                mediaPlayer = null

                // 음성 재생 요청
                val call = RetrofitObject.getRetrofitService.playVoice(token, item.voiceId)
                call.enqueue(object : Callback<RetrofitClient.ResponseVoicePlay> {
                    override fun onResponse(
                        call: Call<RetrofitClient.ResponseVoicePlay>,
                        response: Response<RetrofitClient.ResponseVoicePlay>
                    ) {
                        if (response.isSuccessful && response.body()?.isSuccess == true) {
                            val url = response.body()?.result?.voiceUrl ?: return
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(url)
                                setOnPreparedListener {
                                    start()
                                    item.isPlaying = true
                                    adapter.notifyItemChanged(position)
                                    currentlyPlayingIndex = position
                                }
                                setOnCompletionListener {
                                    item.isPlaying = false
                                    adapter.notifyItemChanged(position)
                                    currentlyPlayingIndex = null
                                }
                                prepareAsync()
                            }
                        } else {
                            Toast.makeText(this@TtsSaveActivity, "재생 실패", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RetrofitClient.ResponseVoicePlay>, t: Throwable) {
                        Toast.makeText(this@TtsSaveActivity, "오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        )

        binding.listListRv.layoutManager = LinearLayoutManager(this)
        binding.listListRv.adapter = adapter

        loadVoiceListFromServer()

        // 임시 데이터 추가
        /*ttssaveList.addAll(
            listOf(
                TtsSaveModel(-1, "코난", "2025-01-12"),
                TtsSaveModel(-2, "케로로", "2025-01-13")
            )
        )*/
        adapter.notifyDataSetChanged()

    }

    private fun loadVoiceListFromServer() {
        val token = MyApplication.getUser().getString("jwt", null) ?: return
        val call = RetrofitObject.getRetrofitService.getAllVoices(token)
        call.enqueue(object : Callback<RetrofitClient.ResponseVoiceAll> {
            override fun onResponse(
                call: Call<RetrofitClient.ResponseVoiceAll>,
                response: Response<RetrofitClient.ResponseVoiceAll>
            ) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    val items = response.body()!!.result
                    ttssaveList.clear()

                    ttssaveList.addAll(
                        items.map {
                            TtsSaveModel(
                                voiceId = it.voiceId,
                                characterName = characterNameFromId(it.characterId),
                                lastMessage = it.createdAt.take(10) // "2025-05-28"
                            )
                        }
                    )
                    adapter.notifyDataSetChanged()
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

    private fun showDeleteDialog(position: Int) {
        // 배경 어둡게 처리
        binding.root.setBackgroundColor(getColor(R.color.Gray700))
        binding.root.alpha = 0.4f

        val dialogView = DialogDeleteBinding.inflate(LayoutInflater.from(this))

        //다이얼로그 중앙 정렬 처리
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER
        dialogView.root.layoutParams = layoutParams

        val dialogContainer = FrameLayout(this)
        dialogContainer.addView(dialogView.root)

        // 다이얼로그 레이아웃 오버레이
        addContentView(
            dialogContainer,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        dialogContainer.setOnClickListener { }

        dialogView.btnCancel.setOnClickListener {
            (dialogContainer.parent as? FrameLayout)?.removeView(dialogContainer)
            binding.root.setBackgroundColor(Color.TRANSPARENT)
            binding.root.alpha = 1.0f
        }

        dialogView.btnDelete.setOnClickListener {
            val token = MyApplication.getUser().getString("jwt", null)
            val voiceId = ttssaveList[position].voiceId

            //더미데이터 방어코드
            /*if (voiceId <= 0) {
                Toast.makeText(this, "유효하지 않은 음성입니다.", Toast.LENGTH_SHORT).show()
                removeDialogOverlay(dialogContainer)
                return@setOnClickListener
            }*/

            if (token != null) {
                val call = RetrofitObject.getRetrofitService.deleteVoice(token, voiceId)
                call.enqueue(object : Callback<RetrofitClient.ResponseVoiceDelete> {
                    override fun onResponse(
                        call: Call<RetrofitClient.ResponseVoiceDelete>,
                        response: Response<RetrofitClient.ResponseVoiceDelete>
                    ) {
                        if (response.isSuccessful && response.body()?.isSuccess == true) {
                            Toast.makeText(this@TtsSaveActivity, "삭제 완료", Toast.LENGTH_SHORT).show()
                            ttssaveList.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        } else {
                            Toast.makeText(this@TtsSaveActivity, "삭제 실패: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                        }
                        removeDialogOverlay(dialogContainer)
                    }

                    override fun onFailure(call: Call<RetrofitClient.ResponseVoiceDelete>, t: Throwable) {
                        Toast.makeText(this@TtsSaveActivity, "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        removeDialogOverlay(dialogContainer)
                    }
                })
            } else {
                Toast.makeText(this@TtsSaveActivity, "로그인 정보 없음", Toast.LENGTH_SHORT).show()
                removeDialogOverlay(dialogContainer)
            }
        }
    }

    private fun removeDialogOverlay(dialogContainer: FrameLayout) {
        (dialogContainer.parent as? FrameLayout)?.removeView(dialogContainer)
        binding.root.setBackgroundColor(Color.TRANSPARENT)
        binding.root.alpha = 1.0f
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
