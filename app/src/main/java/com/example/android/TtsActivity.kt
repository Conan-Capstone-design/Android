package com.example.android

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.android.connection.RetrofitClient
import com.example.android.connection.RetrofitObject
import com.example.android.databinding.ActivityTtsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class TtsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTtsBinding
    private var savedVoiceUrl: String = ""
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTtsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupListeners()
        setupTextWatchers()
        setProfileImage()
    }

    private fun setupViews() = with(binding) {
        btnPause.visibility = View.GONE
    }

    private fun setupListeners() = with(binding) {
        ibArrowBack.setOnClickListener { finish() }
        ibMenu.setOnClickListener {
            val characterName = intent.getStringExtra("character_name") ?: "케로로"
            val characterId = intent.getIntExtra("character_id", 0)
            val newIntent = Intent(this@TtsActivity, TtsSaveActivity::class.java)
            newIntent.putExtra("character_name", characterName)
            newIntent.putExtra("character_id", characterId)
            startActivity(newIntent)
        }
        btnClear.setOnClickListener { etScript.text.clear() }
        btnShare.setOnClickListener { showShareBottomSheet() }

        btnPlay.setOnClickListener {
            btnPlay.visibility = View.GONE
            btnPause.visibility = View.VISIBLE

            // 일시정지된 경우 재생
            if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
                mediaPlayer?.start()
                return@setOnClickListener
            }

            val token = MyApplication.getUser().getString("jwt", null)

            val character = intent.getStringExtra("character_name") ?: "케로로"
            val dialogueText = binding.etScript.text.toString()

            if (token.isNullOrBlank() || dialogueText.isBlank()) {
                Toast.makeText(this@TtsActivity, "문장을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RetrofitClient.RequestVoicePlay(character, dialogueText)
            val call = RetrofitObject.getRetrofitService.playVoice(token, request)

            call.enqueue(object : Callback<RetrofitClient.ResponseVoicePlay> {
                override fun onResponse(
                    call: Call<RetrofitClient.ResponseVoicePlay>,
                    response: Response<RetrofitClient.ResponseVoicePlay>
                ) {
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        val voiceUrl = response.body()?.result?.voiceUrl

                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(voiceUrl)
                            setOnPreparedListener { start() }
                            setOnCompletionListener {
                                btnPause.visibility = View.GONE
                                btnPlay.visibility = View.VISIBLE
                            }
                            prepareAsync()
                        }

                        savedVoiceUrl = voiceUrl ?: ""
                    } else {
                        Toast.makeText(this@TtsActivity, "음성 생성 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RetrofitClient.ResponseVoicePlay>, t: Throwable) {
                    Toast.makeText(this@TtsActivity, "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnPause.setOnClickListener {
            btnPause.visibility = View.GONE
            btnPlay.visibility = View.VISIBLE

            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause() // 일시정지
                }
            }
        }

        binding.btnDownload.setOnClickListener {
            val token = MyApplication.getUser().getString("jwt", null)
            Log.d("TTS_DEBUG", "JWT Token: $token")
            val characterId = intent.getIntExtra("character_id", 0)
            val titleText = binding.etTitle.text.toString()        // 제목
            val contentText = binding.etScript.text.toString()     // 본문

            // 저장할 음성 파일이 없는 경우
            if (token.isNullOrBlank() || characterId == 0 || titleText.isBlank() || contentText.isBlank() || savedVoiceUrl.isBlank()) {
                Toast.makeText(this@TtsActivity, "음성 생성 후 저장할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 음성 파일 다운로드 → 임시 파일로 저장
            Thread {
                try {
                    val inputStream = URL(savedVoiceUrl).openStream()
                    val audioFile = File(cacheDir, "voice_${System.currentTimeMillis()}.wav")
                    FileOutputStream(audioFile).use { output -> inputStream.copyTo(output) }

                    runOnUiThread {
                        uploadVoiceToServer(token, characterId, titleText, contentText, audioFile)
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@TtsActivity, "파일 저장 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()


            // Multipart 전송용 파라미터 구성
            /*val characterIdPart = characterId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val titlePart = titleText.toRequestBody("text/plain".toMediaTypeOrNull())           // 제목
            val dialogueTextPart = contentText.toRequestBody("text/plain".toMediaTypeOrNull())  // 본문

            val voicePart = MultipartBody.Part.createFormData(
                name = "voice",
                filename = audioFile.name,
                body = audioFile.asRequestBody("audio/wav".toMediaTypeOrNull())
            )

            val call = RetrofitObject.getRetrofitService.voiceSaveMultipart(
                token = token,
                characterId = characterIdPart,
                voice = voicePart,
                title = titlePart,               // 제목
                dialogueText = dialogueTextPart      // 본문
            )

            call.enqueue(object : Callback<RetrofitClient.ResponseVoiceSave> {
                override fun onResponse(
                    call: Call<RetrofitClient.ResponseVoiceSave>,
                    response: Response<RetrofitClient.ResponseVoiceSave>
                ) {
                    Log.d("TTS_DEBUG", "Response code: ${response.code()}, message: ${response.message()}")
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        showToast() // 저장 성공 토스트
                    } else {
                        Log.d("TTS_DEBUG", "Response body: ${response.body()}")
                        Toast.makeText(this@TtsActivity, "저장 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RetrofitClient.ResponseVoiceSave>, t: Throwable) {
                    Log.e("TTS_DEBUG", "Server error: ${t.message}", t)
                    Toast.makeText(this@TtsActivity, "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })*/
        }
    }

    private fun setupTextWatchers() = with(binding) {
        listOf(etTitle, etScript).forEach { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    editText.setTextColor(
                        ContextCompat.getColor(
                            this@TtsActivity,
                            if (!s.isNullOrBlank()) R.color.black01 else R.color.Gray500
                        )
                    )
                }
            })
        }
    }

    private fun setProfileImage() {
        val imageResId = intent.getIntExtra("image_res_id", R.drawable.tts_profile)
        binding.ivProfile.setImageResource(imageResId)
    }

    private fun showToast() = with(binding.toastDownTts) {
        visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            visibility = View.GONE
        }, 2000)
    }

    private fun showShareBottomSheet() {
        val view = layoutInflater.inflate(R.layout.tts_share_bottomsheet, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        val fileName = binding.etTitle.text.toString().trim().ifEmpty { "제목없음" }
        view.findViewById<TextView>(R.id.tv_share_filename).text = "$fileName.mp3"

        listOf(
            R.id.btn_share_kakao,
            R.id.btn_share_instagram,
            R.id.btn_share_x
        ).forEach { id ->
            view.findViewById<ImageView>(id).setOnClickListener { dialog.dismiss() }
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun uploadVoiceToServer(
        token: String,
        characterId: Int,
        titleText: String,
        contentText: String,
        audioFile: File
    ) {
        val characterIdPart = characterId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val titlePart = titleText.toRequestBody("text/plain".toMediaTypeOrNull())
        val dialogueTextPart = contentText.toRequestBody("text/plain".toMediaTypeOrNull())
        val voicePart = MultipartBody.Part.createFormData(
            name = "voice",
            filename = audioFile.name,
            body = audioFile.asRequestBody("audio/wav".toMediaTypeOrNull())
        )

        val call = RetrofitObject.getRetrofitService.voiceSaveMultipart(
            token = token,
            characterId = characterIdPart,
            voice = voicePart,
            title = titlePart,
            dialogueText = dialogueTextPart
        )

        call.enqueue(object : Callback<RetrofitClient.ResponseVoiceSave> {
            override fun onResponse(
                call: Call<RetrofitClient.ResponseVoiceSave>,
                response: Response<RetrofitClient.ResponseVoiceSave>
            ) {
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    showToast()
                } else {
                    Toast.makeText(this@TtsActivity, "저장 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RetrofitClient.ResponseVoiceSave>, t: Throwable) {
                Toast.makeText(this@TtsActivity, "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
