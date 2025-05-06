package com.example.android

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android.databinding.FragmentHomeBinding
import android.bluetooth.BluetoothAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class FragmentHome: Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var selectedCharacterIndex = 0
    private lateinit var enableBluetoothLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.character01Layout.setOnClickListener {
            updateSelection(1,
                binding.character01Layout,
                binding.character01Background,
                binding.conanTitle

            )
        }

        binding.character02Layout.setOnClickListener {
            updateSelection(2,
                binding.character02Layout,
                binding.character02Background,
                binding.character02Title
            )
        }

        binding.character03Layout.setOnClickListener {
            updateSelection(3,
                binding.character03Layout,
                binding.character03Background,
                binding.character03Title
            )
        }

        binding.tvPitchValue.text = binding.seekBarPitch.progress.toString()
        binding.tvTimbreValue.text = binding.seekBarTimbre.progress.toString()
        binding.btnDownload.isEnabled = false
        binding.btnBluetooth.isEnabled = false

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


        binding.btnDownload.setOnClickListener {
            if (binding.btnDownload.isEnabled) {
                // 활성화 되어 있을 때만 VideoActivity 이동
                val intent = Intent(requireContext(), VideoActivity::class.java)
                startActivity(intent)
            }
        }

        // Bluetooth 활성화 결과 처리
        enableBluetoothLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 블루투스가 성공적으로 켜졌다면 블루투스 설정 화면으로 이동
                val intent = Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "블루투스가 활성화되지 않았습니다", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBluetooth.setOnClickListener {
            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                Toast.makeText(requireContext(), "블루투스를 지원하지 않는 기기입니다", Toast.LENGTH_SHORT).show()
            } else {
                if (!bluetoothAdapter.isEnabled) {
                    // 블루투스가 꺼져 있으면 켜기 요청
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    enableBluetoothLauncher.launch(enableBtIntent)
                } else {
                    // 블루투스가 이미 켜져 있으면 설정 화면 이동
                    val intent = Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)
                    startActivity(intent)
                }
            }
        }

        return binding.root
    }

    private fun updateSelection(
        newIndex: Int,
        newLayout: ImageView,
        newBackground: View,
        newTitle: TextView
    ) {
        if (selectedCharacterIndex == newIndex) {
            // 이미 선택된 걸 다시 클릭하면 → 해제
            resetCheckState(newLayout, newBackground, newTitle)
            selectedCharacterIndex = 0
            binding.ivSelectedCharacter02.setImageResource(R.drawable.main_center_circle) // 기본 이미지로 되돌림
            binding.btnDownload.isEnabled = false
            binding.btnBluetooth.isEnabled = false
            return
        }

        // 기존 선택 해제
        when (selectedCharacterIndex) {
            1 -> resetCheckState(binding.character01Layout, binding.character01Background, binding.conanTitle)
            2 -> resetCheckState(binding.character02Layout, binding.character02Background, binding.character02Title)
            3 -> resetCheckState(binding.character03Layout, binding.character03Background, binding.character03Title)
        }

        // 새로운 선택 적용
        selectedCharacterIndex = newIndex
        newLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.conan03))
        newBackground.visibility = View.GONE
        newTitle.setBackgroundResource(R.drawable.roundtab__character_title_blue)
        newTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.conan03))

        val drawableRes = when (newIndex) {
            1 -> R.drawable.a_conan         // 코난
            2 -> R.drawable.shinchangtts02  // 짱구
            3 -> R.drawable.charoro         // 케로로
            else -> R.drawable.main_center_circle
        }

        binding.ivSelectedCharacter02.setImageResource(drawableRes)
        binding.btnDownload.isEnabled = true
        binding.btnBluetooth.isEnabled = true

        val tintColor = ContextCompat.getColor(requireContext(), if (newIndex != 0) R.color.conan else R.color.Gray400)
        val tint = ColorStateList.valueOf(tintColor)
        val eqColor = ContextCompat.getColor(requireContext(), if (newIndex != 0) R.color.conan03 else R.color.Gray1000)
        val eqtint = ColorStateList.valueOf(eqColor)

        binding.btnDownload.imageTintList = tint
        binding.btnBluetooth.imageTintList = tint
        binding.ivEQ.imageTintList = eqtint
    }

    private fun resetCheckState(
        layout: ImageView,
        background: View,
        title: TextView
    ) {
        layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.conan01))
        background.visibility = View.VISIBLE
        title.setBackgroundResource(R.drawable.roundtab__character_title)
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.Gray800))
        val defaultTint = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Gray400))
        val defaultEq = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Gray1000))
        binding.btnDownload.imageTintList = defaultTint
        binding.btnBluetooth.imageTintList = defaultTint
        binding.ivEQ.imageTintList = defaultEq
    }

}