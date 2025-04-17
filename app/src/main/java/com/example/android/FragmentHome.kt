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

        binding.tvPitchValue.text = binding.seekBarTimbre.progress.toString()
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
                    textView.x = sb.x + thumbX - textView.width / 2f
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 터치를 시작할 때 필요한 동작이 있으면 여기에
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 터치를 끝낼 때 필요한 동작이 있으면 여기에
            }
        })

        binding.seekBarTimbre.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvTimbreValue.text = progress.toString()
                seekBar?.let { sb ->
                    val available = sb.width - sb.paddingLeft - sb.paddingRight
                    val thumbX = sb.paddingLeft + available * progress / sb.max
                    val textView = binding.tvTimbreValue
                    textView.x = sb.x + thumbX - textView.width / 2f
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // 터치를 시작할 때 필요한 동작이 있으면 여기에
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // 터치를 끝낼 때 필요한 동작이 있으면 여기에
            }
        })

        val dimColor = Color.parseColor("#77000000")
        val selectColor = ContextCompat.getColor(requireContext(), R.color.lightblue)
        val selectTint   = ColorStateList.valueOf(selectColor)

        val imageViews = listOf(binding.ivJjanggu, binding.ivConan, binding.ivKeroro)
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
                    imageViews.forEach {
                        it.setColorFilter(dimColor, PorterDuff.Mode.SRC_ATOP)
                        it.backgroundTintList = null
                    }
                    binding.ivSelectedCharacter.setImageResource(defaultCenterRes)
                    binding.ivSelectedCharacter.backgroundTintList = null
                    selectedIv = null
                } else {
                    imageViews.forEach {
                        it.setColorFilter(dimColor, PorterDuff.Mode.SRC_ATOP)
                        it.backgroundTintList = null
                    }
                    iv.clearColorFilter()
                    iv.backgroundTintList = selectTint
                    binding.ivSelectedCharacter.setImageDrawable(iv.drawable)
                    binding.ivSelectedCharacter.backgroundTintList = selectTint
                    selectedIv = iv
                }
            }
        }

        return binding.root
    }

}