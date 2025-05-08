package com.example.android

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.android.databinding.FragmentHomeBinding
import android.bluetooth.BluetoothAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class FragmentHome : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var selectedCharacterIndex = 0
    private lateinit var enableBluetoothLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // 캐릭터 선택 핸들링
        binding.character01Layout.setOnClickListener {
            updateSelection(1, binding.character01Layout, binding.character01Background, binding.conanTitle)
        }

        binding.character02Layout.setOnClickListener {
            updateSelection(2, binding.character02Layout, binding.character02Background, binding.character02Title)
        }

        binding.character03Layout.setOnClickListener {
            updateSelection(3, binding.character03Layout, binding.character03Background, binding.character03Title)
        }

        binding.tvPitchValue.text = binding.seekBarPitch.progress.toString()
        binding.tvTimbreValue.text = binding.seekBarTimbre.progress.toString()
        binding.btnDownload.isEnabled = false
        binding.btnBluetooth.isEnabled = false

        // Pitch SeekBar
        binding.seekBarPitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvPitchValue.text = progress.toString()
                seekBar?.let {
                    val available = it.width - it.paddingLeft - it.paddingRight
                    val thumbX = it.paddingLeft + available * progress / it.max
                    binding.tvPitchValue.x = it.x + thumbX + 5f - binding.tvPitchValue.width / 2f
                    binding.ivPitchArrow.x = it.x + thumbX + 5f - binding.ivPitchArrow.width / 2f
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Timbre SeekBar
        binding.seekBarTimbre.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvTimbreValue.text = progress.toString()
                seekBar?.let {
                    val available = it.width - it.paddingLeft - it.paddingRight
                    val thumbX = it.paddingLeft + available * progress / it.max
                    binding.tvTimbreValue.x = it.x + thumbX + 5f - binding.tvTimbreValue.width / 2f
                    binding.ivTimbreArrow.x = it.x + thumbX + 5f - binding.ivTimbreArrow.width / 2f
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 다운로드 버튼
        binding.btnDownload.setOnClickListener {
            if (binding.btnDownload.isEnabled) {
                val intent = Intent(requireContext(), VideoActivity::class.java)
                startActivity(intent)
            }
        }

        // 블루투스 요청 결과 핸들러
        enableBluetoothLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "블루투스가 활성화되지 않았습니다", Toast.LENGTH_SHORT).show()
                }
            }

        // 블루투스 버튼
        binding.btnBluetooth.setOnClickListener {
            val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                Toast.makeText(requireContext(), "블루투스를 지원하지 않는 기기입니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Android 12 이상: BLUETOOTH_CONNECT 권한 체크
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT),
                        1001
                    )
                    return@setOnClickListener
                }
            }

            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableBtIntent)
            } else {
                val intent = Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)
                startActivity(intent)
            }
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.btnBluetooth.performClick()
            } else {
                Toast.makeText(requireContext(), "블루투스 권한이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSelection(
        newIndex: Int,
        newLayout: ImageView,
        newBackground: View,
        newTitle: TextView
    ) {
        if (selectedCharacterIndex == newIndex) {
            resetCheckState(newLayout, newBackground, newTitle)
            selectedCharacterIndex = 0
            binding.ivSelectedCharacter02.setImageResource(R.drawable.main_center_circle)
            binding.btnDownload.isEnabled = false
            binding.btnBluetooth.isEnabled = false
            return
        }

        when (selectedCharacterIndex) {
            1 -> resetCheckState(binding.character01Layout, binding.character01Background, binding.conanTitle)
            2 -> resetCheckState(binding.character02Layout, binding.character02Background, binding.character02Title)
            3 -> resetCheckState(binding.character03Layout, binding.character03Background, binding.character03Title)
        }

        selectedCharacterIndex = newIndex
        newLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.conan03))
        newBackground.visibility = View.GONE
        newTitle.setBackgroundResource(R.drawable.roundtab__character_title_blue)
        newTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.conan03))

        val drawableRes = when (newIndex) {
            1 -> R.drawable.a_conan
            2 -> R.drawable.shinchangtts02
            3 -> R.drawable.charoro
            else -> R.drawable.main_center_circle
        }

        binding.ivSelectedCharacter02.setImageResource(drawableRes)
        binding.btnDownload.isEnabled = true
        binding.btnBluetooth.isEnabled = true

        val tintColor = ContextCompat.getColor(requireContext(), if (newIndex != 0) R.color.conan else R.color.Gray400)
        val eqColor = ContextCompat.getColor(requireContext(), if (newIndex != 0) R.color.conan03 else R.color.Gray1000)

        binding.btnDownload.imageTintList = ColorStateList.valueOf(tintColor)
        binding.btnBluetooth.imageTintList = ColorStateList.valueOf(tintColor)
        binding.ivEQ.imageTintList = ColorStateList.valueOf(eqColor)
    }

    private fun resetCheckState(layout: ImageView, background: View, title: TextView) {
        layout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.conan01))
        background.visibility = View.VISIBLE
        title.setBackgroundResource(R.drawable.roundtab__character_title)
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.Gray800))
        binding.btnDownload.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Gray400))
        binding.btnBluetooth.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Gray400))
        binding.ivEQ.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.Gray1000))
    }
}
