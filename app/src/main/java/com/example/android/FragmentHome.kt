package com.example.android

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android.databinding.FragmentHomeBinding

class FragmentHome: Fragment() {
    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.tvPitchValue.text = binding.seekBarPitch.progress.toString()
        binding.tvTimbreValue.text = binding.seekBarTimbre.progress.toString()

        binding.seekBarPitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvPitchValue.text = progress.toString()
                seekBar?.let { sb ->
                    val available = sb.width - sb.paddingLeft - sb.paddingRight
                    // 현재 Thumb 위치
                    val thumbX = sb.paddingLeft + available * progress / sb.max
                    // TextView 중앙이 Thumb 위로 오도록 보정
                    val textView = binding.tvPitchValue
                    textView.x = sb.x + thumbX + 5f - textView.width / 2f
                    val arrow = binding.ivPitchArrow
                    arrow.x = sb.x + thumbX + 5f- arrow.width / 2f
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 터치를 시작할 때 필요한 동작이 있으면 여기에
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.seekBarTimbre.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvTimbreValue.text = progress.toString()
                seekBar?.let { sb ->
                    val available = sb.width - sb.paddingLeft - sb.paddingRight
                    val thumbX = sb.paddingLeft + available * progress / sb.max
                    val textView = binding.tvTimbreValue
                    textView.x = sb.x + thumbX + 5f - textView.width / 2f
                    val arrow = binding.ivTimbreArrow
                    arrow.x = sb.x + thumbX + 5f - arrow.width / 2f
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        val dimColor = Color.parseColor("#77000000")
        val selectColor = ContextCompat.getColor(requireContext(), R.color.lightblue)
        val conanColor = ContextCompat.getColor(requireContext(), R.color.conan)


        val imageViews = listOf(binding.ivJjanggu, binding.ivConan, binding.ivKeroro)
        val buttons = listOf(binding.btnJjanggu, binding.btnConan, binding.btnKeroro)

        imageViews.forEach { iv ->
            iv.setColorFilter(dimColor, PorterDuff.Mode.SRC_ATOP)
            iv.backgroundTintList = null
        }
        val buttonToIv = mapOf(
            binding.btnJjanggu to binding.ivJjanggu,
            binding.btnConan to binding.ivConan,
            binding.btnKeroro to binding.ivKeroro
        )

        val defaultCenterRes = R.drawable.main_center_circle
        var selectedIv: ImageView? = null

        buttonToIv.forEach { (btn, iv) ->
            btn.setOnClickListener {
                if (selectedIv == iv) {
                    //선택 해제
                    imageViews.forEach {
                        it.setColorFilter(dimColor, PorterDuff.Mode.SRC_ATOP)
                        it.backgroundTintList = null
                        it.setBackgroundResource(R.drawable.circle_border)
                    }
                    buttons.forEach {
                        it.setBackgroundResource(R.drawable.button_outline) // 버튼 배경 초기화
                    }
                    binding.ivEQ.setColorFilter(null) // 기본 색 복원
                    binding.btnBluetooth.setColorFilter(null)
                    binding.btnDownload.setColorFilter(null)

                    binding.ivSelectedCharacter.setImageResource(defaultCenterRes)
                    binding.ivSelectedCharacter.setBackgroundResource(R.drawable.circle_border)
                    selectedIv = null
                } else {
                    //모두 초기화
                    imageViews.forEach {
                        it.setColorFilter(dimColor, PorterDuff.Mode.SRC_ATOP)
                        it.backgroundTintList = null
                        it.setBackgroundResource(R.drawable.circle_border)
                    }
                    buttons.forEach {
                        it.setBackgroundResource(R.drawable.button_outline)
                    }
                    iv.clearColorFilter()
                    iv.setBackgroundResource(R.drawable.circle_border_lightblue)

                    btn.setBackgroundResource(R.drawable.button_outline_lightblue)

                    binding.ivEQ.setColorFilter(selectColor)
                    binding.btnBluetooth.setColorFilter(conanColor)
                    binding.btnDownload.setColorFilter(conanColor)

                    binding.ivSelectedCharacter.setImageDrawable(iv.drawable)
                    binding.ivSelectedCharacter.setBackgroundResource(R.drawable.circle_border_lightblue)
                    selectedIv = iv
                }
            }
        }

        return binding.root
    }

}