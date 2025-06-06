package com.example.android

import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.android.databinding.ActivitySecessionBinding

class SecessionActivity: AppCompatActivity() {
    lateinit var binding: ActivitySecessionBinding
    private var isChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView10.setOnClickListener {
            finish()
        }
        // 이미지 뷰 클릭 이벤트 리스너 설정
        binding.imageView25.setOnClickListener {
            // 토글 상태 변경
            isChecked = !isChecked

            // 이미지 변경
            if (isChecked) {
                binding.imageView25.setImageResource(R.drawable.btn_checkbox_checked)
                binding.button3.setBackgroundResource(R.drawable.blue_rectangle)
                binding.button3.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                binding.imageView25.setImageResource(R.drawable.btn_checkbox)
                binding.button3.setBackgroundResource(R.drawable.gray_rectangle)
                binding.button3.setTextColor(ContextCompat.getColor(this, R.color.Gray900))
            }
        }
        binding.button3.setOnClickListener {
            if (!isChecked) {
                Toast.makeText(this, "동의 후 탈퇴가 가능합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val token = MyApplication.getUser().getString("jwt", null)

            if (token != null) {
                val call = com.example.android.connection.RetrofitObject.getRetrofitService.withdrawUser(token)
                call.enqueue(object : retrofit2.Callback<com.example.android.connection.RetrofitClient.ResponseWithdraw> {
                    override fun onResponse(
                        call: retrofit2.Call<com.example.android.connection.RetrofitClient.ResponseWithdraw>,
                        response: retrofit2.Response<com.example.android.connection.RetrofitClient.ResponseWithdraw>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            if (result != null && result.isSuccess) {
                                Toast.makeText(this@SecessionActivity, result.result.message, Toast.LENGTH_SHORT).show()
                                // SharedPreferences 초기화
                                MyApplication.getUser().edit().clear().apply()
                                // 로그인 화면으로 이동
                                val intent = Intent(this@SecessionActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@SecessionActivity, result?.message ?: "탈퇴 실패", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this@SecessionActivity, "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(
                        call: retrofit2.Call<com.example.android.connection.RetrofitClient.ResponseWithdraw>,
                        t: Throwable
                    ) {
                        Toast.makeText(this@SecessionActivity, "통신 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("Secession", "API Failure", t)
                    }
                })
            } else {
                Toast.makeText(this, "로그인 토큰이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}