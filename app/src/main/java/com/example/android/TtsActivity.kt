package com.example.android

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView

class TtsActivity : AppCompatActivity() {

    private lateinit var ivProfile: ShapeableImageView
    private lateinit var etTitle: EditText
    private lateinit var etScript: EditText
    private lateinit var btnBack: ImageButton
    private lateinit var btnMenu: ImageButton
    private lateinit var btnClear: ImageButton
    private lateinit var btnShare: ImageButton
    private lateinit var btnPlay: ImageButton
    private lateinit var btnPause: ImageButton
    private lateinit var btnDownload: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tts)

        // 뷰 초기화
        ivProfile = findViewById(R.id.iv_profile)
        etTitle = findViewById(R.id.et_title)
        etScript = findViewById(R.id.et_script)
        btnBack = findViewById(R.id.ib_arrow_back)
        btnMenu = findViewById(R.id.ib_menu)
        btnClear = findViewById(R.id.btn_clear)
        btnShare = findViewById(R.id.btn_share)
        btnPlay = findViewById(R.id.btn_play)
        btnPause = findViewById(R.id.btn_pause)
        btnDownload = findViewById(R.id.btn_download)

        // 뒤로가기 버튼 → 액티비티 종료
        btnBack.setOnClickListener {
            finish()
        }

        // 메뉴 버튼 클릭 → 추후 팝업 등 구현
        btnMenu.setOnClickListener {

        }

        // 대사 초기화 버튼
        btnClear.setOnClickListener {
            etScript.text.clear()
        }

        // 공유 버튼 클릭
        btnShare.setOnClickListener {
            showShareBottomSheet()
        }

        // 재생 버튼 클릭
        btnPlay.setOnClickListener {
            btnPlay.visibility = View.GONE
            btnPause.visibility = View.VISIBLE
        }

        btnPause.setOnClickListener {
            btnPlay.visibility = View.VISIBLE
            btnPause.visibility = View.GONE
        }

        val toastView = findViewById<TextView>(R.id.toast_down_tts)
        // 다운로드 버튼 클릭
        btnDownload.setOnClickListener {
            toastView.visibility = View.VISIBLE

            // 2초 뒤에 다시 GONE으로 변경
            Handler(Looper.getMainLooper()).postDelayed({
                toastView.visibility = View.GONE
            }, 2000)
        }

        // 텍스트 입력에 따른 색상 변경
        setupTextColorWatcher(etTitle)
        setupTextColorWatcher(etScript)

        val imageResId = intent.getIntExtra("image_res_id", R.drawable.tts_profile)
        ivProfile.setImageResource(imageResId)
    }

    private fun setupTextColorWatcher(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newColor = if (!s.isNullOrBlank()) {
                    ContextCompat.getColor(this@TtsActivity, R.color.black01)
                } else {
                    ContextCompat.getColor(this@TtsActivity, R.color.Gray500)
                }
                editText.setTextColor(newColor)
            }
        })
    }

    private fun showShareBottomSheet() {
        val view = layoutInflater.inflate(R.layout.tts_share_bottomsheet, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        val tvFileName = view.findViewById<TextView>(R.id.tv_share_filename)
        val rawTitle = etTitle.text.toString().trim()

        // 제목이 없으면 기본 이름 사용
        val fileName = if (rawTitle.isNotEmpty()) rawTitle else "제목없음"
        tvFileName.text = "$fileName.mp3"

        // 버튼 이벤트들
        view.findViewById<ImageView>(R.id.btn_share_kakao).setOnClickListener {
            // 카카오 공유 처리
            dialog.dismiss()
        }

        view.findViewById<ImageView>(R.id.btn_share_instagram).setOnClickListener {
            // 인스타그램 공유 처리
            dialog.dismiss()
        }

        view.findViewById<ImageView>(R.id.btn_share_x).setOnClickListener {
            // X 공유 처리
            dialog.dismiss()
        }

        dialog.show()
    }
}