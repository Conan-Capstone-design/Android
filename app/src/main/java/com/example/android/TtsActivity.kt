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
import com.example.android.databinding.ActivityTtsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class TtsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTtsBinding

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
            startActivity(Intent(this@TtsActivity, TtsSaveActivity::class.java))
        }
        btnClear.setOnClickListener { etScript.text.clear() }
        btnDownload.setOnClickListener { showToast() }
        btnShare.setOnClickListener { showShareBottomSheet() }

        btnPlay.setOnClickListener {
            btnPlay.visibility = View.GONE
            btnPause.visibility = View.VISIBLE
        }
        btnPause.setOnClickListener {
            btnPause.visibility = View.GONE
            btnPlay.visibility = View.VISIBLE
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
}
