package com.example.android

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.databinding.ActivityTtssaveBinding
import com.example.android.databinding.DialogDeleteBinding

class TtsSaveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTtssaveBinding
    private lateinit var adapter: TtsSaveRVAdapter
    private val ttssaveList = mutableListOf<TtsSaveModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTtssaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listBackBtn.setOnClickListener {
            finish()
        }

        adapter = TtsSaveRVAdapter(ttssaveList, onDeleteClick = { position ->
            showDeleteDialog(position)
        })

        binding.listListRv.layoutManager = LinearLayoutManager(this)
        binding.listListRv.adapter = adapter

        // 임시 데이터 추가
        ttssaveList.addAll(
            listOf(
                TtsSaveModel("코난", "2025.01.12"),
                TtsSaveModel("케로로", "2025.01.13")
            )
        )
        adapter.notifyDataSetChanged()
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
            ttssaveList.removeAt(position)
            adapter.notifyItemRemoved(position)
            (dialogContainer.parent as? FrameLayout)?.removeView(dialogContainer)
            binding.root.setBackgroundColor(Color.TRANSPARENT)
            binding.root.alpha = 1.0f
        }
    }
}
