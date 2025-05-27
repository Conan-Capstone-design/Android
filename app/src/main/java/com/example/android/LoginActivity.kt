package com.example.android

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.connection.RetrofitClient
import com.example.android.connection.RetrofitObject
import com.example.android.databinding.ActivityLoginBinding
import com.example.android.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private var isChecked: Boolean = false

    private lateinit var user: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MyApplication.initializeUser(this)
        user = MyApplication.getUser()
        editor = user.edit()

//        binding.button.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }

        binding.button.setOnClickListener {
            val email = binding.IDtext.text.toString()
            val password = binding.Passwordtext.text.toString()

            val call = RetrofitObject.getRetrofitService.login(RetrofitClient.Requestlogin(email, password))
            call.enqueue(object : Callback<RetrofitClient.Responselogin> {
                override fun onResponse(call: Call<RetrofitClient.Responselogin>, response: Response<RetrofitClient.Responselogin>) {
                    Log.d("Retrofit21", response.toString())
                    if (response.isSuccessful) {
                        val response = response.body()
                        Log.d("Retrofit2", response.toString())
                        if(response != null){
                            if(response.isSuccess) {
                                val token = response.result
                                editor.putString("email", email)
                                editor.putString("password", password)
                                editor.putString("jwt", token)
                                editor.apply()
                                Log.d("logintoken", token)
                                val intent =
                                    Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<RetrofitClient.Responselogin>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        binding.textView16.setOnClickListener {
            val intent = Intent(this, AgreefirstActivity::class.java)
            startActivity(intent)
        }
        binding.imageView12.setOnClickListener {
            // 토글 상태 변경
            isChecked = !isChecked
            // 이미지 변경
            if (isChecked) {
                binding.imageView12.setImageResource(R.drawable.btn_checkbox_checked)
            } else {
                binding.imageView12.setImageResource(R.drawable.btn_checkbox)
            }
        }
    }
}