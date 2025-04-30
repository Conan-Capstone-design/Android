package com.example.android

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.example.android.databinding.FragmentVideoBinding

class FragmentVideo : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    private var selectedVideoUri: Uri? = null

    private val videoPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedVideoUri = data?.data
            if (selectedVideoUri != null) {
                binding.btnConvert.isEnabled = true
                setupBtnConvertStyle()

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        binding.btnConvert.isEnabled = false

        binding.btnUpload.setOnClickListener {
            openGalleryForVideo()
        }

        binding.btnDeleteVideo.setOnClickListener {
            deleteUploadedVideo()
        }
        binding.btnPlayVideo.setOnClickListener {
            binding.videoView.start()  // 영상 재생 시작
            binding.btnPlayVideo.visibility = View.GONE
            binding.btnPauseVideo.visibility = View.VISIBLE
        }

        binding.btnPauseVideo.setOnClickListener {
            binding.videoView.pause() // 영상 일시정지
            binding.btnPauseVideo.visibility = View.GONE
            binding.btnPlayVideo.visibility = View.VISIBLE
        }

        binding.videoView.setOnCompletionListener {
            binding.btnPauseVideo.visibility = View.GONE
            binding.btnPlayVideo.visibility = View.VISIBLE
            binding.videoView.seekTo(1)
        }

        return binding.root
    }

    private fun setupBtnConvertStyle() {
        val isEnabled = binding.btnConvert.isEnabled

        binding.btnConvert.background = ContextCompat.getDrawable(
            binding.root.context,
            if (isEnabled) R.drawable.video_button_enabled else R.drawable.video_button_disabled
        )

        binding.tvConvert.setTextColor(
            ContextCompat.getColor(
                binding.root.context,
                if (isEnabled) android.R.color.white else android.R.color.black
            )
        )
    }

    private fun openGalleryForVideo() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        videoPickerLauncher.launch(intent)
    }

    private fun deleteUploadedVideo() {
        //영상 삭제
        selectedVideoUri = null
        binding.videoView.visibility = View.GONE
        binding.btnDeleteVideo.visibility = View.GONE
        binding.btnPlayVideo.visibility = View.GONE
        binding.btnPauseVideo.visibility = View.GONE

        binding.btnConvert.isEnabled = false
        setupBtnConvertStyle()

        binding.btnUpload.isEnabled = true

        binding.tvConvertedLabel.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topMargin = 40.dpToPx()  // 원래 40dp로 복귀
        }
        binding.btnSave.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topMargin = 7.dpToPx()  // 원래 7dp로 복귀
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}