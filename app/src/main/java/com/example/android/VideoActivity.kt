package com.example.android

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.commit
import com.example.android.connection.RetrofitObject
import com.example.android.databinding.ActivityVideoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class VideoActivity : AppCompatActivity() {
    lateinit var binding: ActivityVideoBinding
    private var selectedVideoUri: Uri? = null
    private var convertedVideoUri: Uri? = null
    private var isConverted = false
    private lateinit var user: SharedPreferences
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = MyApplication.getUser()
        token = user.getString("jwt", "").toString()

        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSave.isEnabled = false

        // Toolbar 설정
        setSupportActionBar(binding.VideoToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 텍스트 직접 넣었으니까 툴바 기본 타이틀은 숨김

        // 뒤로가기 버튼 클릭 처리
        binding.ibArrowBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnConvert.isEnabled = false
        updateBtnConvertStyle()

        binding.btnUpload.setOnClickListener {
            openGalleryForVideo()
        }

        binding.btnDeleteVideo.setOnClickListener {
            deleteUploadedVideo()
        }

        binding.btnPlayVideo.setOnClickListener {
            binding.videoView.start()
            binding.btnPlayVideo.visibility = View.GONE
            binding.btnPauseVideo.visibility = View.VISIBLE
        }

        binding.btnPauseVideo.setOnClickListener {
            binding.videoView.pause()
            binding.btnPauseVideo.visibility = View.GONE
            binding.btnPlayVideo.visibility = View.VISIBLE
        }

        binding.videoView.setOnCompletionListener {
            binding.btnPauseVideo.visibility = View.GONE
            binding.btnPlayVideo.visibility = View.VISIBLE
            binding.videoView.seekTo(1)
        }

        binding.btnSave.setOnClickListener {
            convertedVideoUri?.let { uri ->
                saveVideoToGallery(uri)
                showToastMessage()
            } ?: Toast.makeText(this, "저장할 영상이 없습니다", Toast.LENGTH_SHORT).show()
        }

        binding.btnConvert.setOnClickListener {
            val uri = selectedVideoUri

            if (token.isNullOrBlank() || uri == null) {
                Toast.makeText(this, "먼저 영상을 업로드하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "비디오 변환 중입니다. 잠시만 기다려주세요...", Toast.LENGTH_SHORT).show()

            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "upload_video_${System.currentTimeMillis()}.mp4")
            inputStream?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }

            val videoPart = MultipartBody.Part.createFormData(
                "video",
                file.name,
                file.asRequestBody("video/mp4".toMediaTypeOrNull())
            )
            val targetIndex = "1".toRequestBody("text/plain".toMediaTypeOrNull())

            val call = RetrofitObject.getRetrofitService.convertVideo(token, videoPart, targetIndex)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful && response.body() != null) {
                        // 백그라운드에서 저장 작업 수행
                        GlobalScope.launch(Dispatchers.IO) {
                            val convertedFile = File(cacheDir, "converted_${System.currentTimeMillis()}.mp4")
                            response.body()?.byteStream()?.use { input ->
                                FileOutputStream(convertedFile).use { output -> input.copyTo(output) }
                            }

                            // UI 업데이트는 메인 스레드에서
                            withContext(Dispatchers.Main) {
                                convertedVideoUri = Uri.fromFile(convertedFile)

                                binding.videoView2.apply {
                                    visibility = View.VISIBLE
                                    setVideoURI(convertedVideoUri)
                                    seekTo(1)
                                }

                                binding.btnSave.isEnabled = true
                                isConverted = true

                                binding.btnPlayVideo2.visibility = View.VISIBLE
                                binding.btnPauseVideo2.visibility = View.GONE

                                binding.btnSave.updateLayoutParams<ConstraintLayout.LayoutParams> {
                                    topMargin = 164.dpToPx()
                                }

                                binding.videoView2.setOnCompletionListener {
                                    binding.btnPauseVideo2.visibility = View.GONE
                                    binding.btnPlayVideo2.visibility = View.VISIBLE
                                    binding.videoView2.seekTo(1)
                                }

                                binding.btnPlayVideo2.setOnClickListener {
                                    binding.videoView2.start()
                                    binding.btnPlayVideo2.visibility = View.GONE
                                    binding.btnPauseVideo2.visibility = View.VISIBLE
                                }

                                binding.btnPauseVideo2.setOnClickListener {
                                    binding.videoView2.pause()
                                    binding.btnPauseVideo2.visibility = View.GONE
                                    binding.btnPlayVideo2.visibility = View.VISIBLE
                                }
                            }
                        }
                    } else {
                        val errorMsg = try {
                            val errorJson = JSONObject(response.errorBody()?.string() ?: "")
                            errorJson.optJSONObject("result")?.optString("error")
                                ?: errorJson.optString("message", "비디오 변환 실패")
                        } catch (e: Exception) {
                            "비디오 변환 실패"
                        }
                        Toast.makeText(this@VideoActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    runOnUiThread {
                        Toast.makeText(this@VideoActivity, "서버 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    // 갤러리에서 영상 선택을 위한 인텐트 실행
    private fun openGalleryForVideo() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        videoPickerLauncher.launch(intent)
    }

    // 비디오 삭제 함수(업로드된 영상 및 관련 상태 초기화)
    private fun deleteUploadedVideo() {
        selectedVideoUri = null
        convertedVideoUri = null
        isConverted = false

        binding.videoView.visibility = View.GONE
        binding.videoView2.visibility = View.GONE
        binding.btnDeleteVideo.visibility = View.GONE
        binding.btnPlayVideo.visibility = View.GONE
        binding.btnPauseVideo.visibility = View.GONE
        binding.btnPlayVideo2.visibility = View.GONE
        binding.btnPauseVideo2.visibility = View.GONE

        binding.btnConvert.isEnabled = false
        updateBtnConvertStyle()

        binding.btnUpload.isEnabled = true

        binding.tvConvertedLabel.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topMargin = 40.dpToPx()
        }
        binding.btnSave.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topMargin = 7.dpToPx()
        }
        binding.btnSave.isEnabled = false
    }

    // 변환 버튼 컬러 변경 함수 (enable/disable 상태에 따라 배경 및 텍스트 색 변경)
    private fun updateBtnConvertStyle() {
        val isEnabled = binding.btnConvert.isEnabled
        binding.btnConvert.background = ContextCompat.getDrawable(
            this,
            if (isEnabled) R.drawable.video_button_enabled else R.drawable.video_button_disabled
        )
        binding.tvConvert.setTextColor(
            ContextCompat.getColor(
                this,
                if (isEnabled) android.R.color.white else android.R.color.black
            )
        )
    }

    //// dp 값을 px 값으로 변환하는 확장 함수
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    // 주어진 URI의 영상을 갤러리(영화 폴더)에 저장하는 함수
    private fun saveVideoToGallery(videoUri: Uri) {
        val resolver = contentResolver
        val videoCollection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val newVideoDetails = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "converted_video_${System.currentTimeMillis()}.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/") // 갤러리의 Movies 폴더
        }

        val newVideoUri = resolver.insert(videoCollection, newVideoDetails)

        newVideoUri?.let { outputUri ->
            resolver.openOutputStream(outputUri)?.use { outputStream ->
                resolver.openInputStream(videoUri)?.use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    private val videoPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedVideoUri = data?.data
            if (selectedVideoUri != null) {
                binding.btnConvert.isEnabled = true
                updateBtnConvertStyle()

                binding.btnUpload.isEnabled = false

                // 영상 표시
                binding.videoView.apply {
                    visibility = View.VISIBLE
                    setVideoURI(selectedVideoUri)
                    seekTo(1)  // 썸네일처럼 첫 프레임 표시
                }

                // 버튼 표시
                binding.btnPlayVideo.visibility = View.VISIBLE
                binding.btnDeleteVideo.visibility = View.VISIBLE


                // 위치 이동
                binding.tvConvertedLabel.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topMargin = 131.dpToPx()
                }
                binding.btnSave.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topMargin = 7.dpToPx()
                }

            }
        }
    }

    //Toast 메시지 표시 함수(2초간)
    private fun showToastMessage() {
        binding.toastDownmsg.visibility = View.VISIBLE
        binding.toastDownmsg.alpha = 1f

        binding.toastDownmsg.animate()
            .alpha(0f)
            .setDuration(500) // 사라지는 애니메이션 시간
            .setStartDelay(2000) // 2초 뒤에 사라짐
            .withEndAction {
                binding.toastDownmsg.visibility = View.GONE
                binding.toastDownmsg.alpha = 1f
            }
            .start()
    }
}