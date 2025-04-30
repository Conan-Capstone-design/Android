package com.example.android

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Pattern
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.android.databinding.ActivityMypageProfileBinding

class MypageProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityMypageProfileBinding
    private val PICK_IMAGE_REQUEST = 1 // 이미지를 선택하는 요청 코드
    var isPasswordVisible = false
    private var password: String = ""
    private var email: String= ""
    private var nickName: String= ""
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textchecking.visibility = View.GONE
        binding.textchecking2.visibility = View.GONE
        // EditText를 binding으로 참조
        val textPassword = binding.textPassword
        val textPasswordCheck = binding.textPasswordcheck
        val eyeImageView = binding.imageView23
        val eyeCheckImageView = binding.imageView24

        val button2 = binding.button2

        textPassword.setText(password)

        binding.textID.hint = nickName
        binding.textID2.text = email

        binding.imageView10.setOnClickListener {
            finish()
        }

        binding.imageView18.setOnClickListener {
            openGallery()
        }

        eyeImageView.setOnClickListener {
            togglePasswordVisibility(textPassword, eyeImageView)
        }
        eyeCheckImageView.setOnClickListener {
            togglePasswordVisibility(textPasswordCheck, eyeCheckImageView)
        }

        textPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // 입력된 비밀번호가 조건을 만족하는지 확인
                val isValidPassword = isPasswordValid(textPassword.text.toString())

                // 비밀번호가 조건을 만족하지 않으면 textchecking의 visibility를 View.VISIBLE로 설정
                if (!isValidPassword) {
                    binding.textchecking.visibility = View.VISIBLE
                } else {
                    // 비밀번호가 조건을 만족하면 textchecking의 visibility를 View.GONE으로 설정
                    binding.textchecking.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        // textPasswordCheck의 텍스트 변경 감지
        textPasswordCheck.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // textPassword와 textPasswordCheck의 값이 같을 때 배경을 업데이트
                if (textPassword.text.toString() == textPasswordCheck.text.toString()) {
                    button2.setBackgroundResource(R.drawable.blue_rectangle)
                    binding.button2.setTextColor(ContextCompat.getColor(this@MypageProfileActivity, R.color.white))
                    binding.textchecking2.visibility = View.GONE
                }
                else {
                    button2.setBackgroundResource(R.drawable.gray_rectangle)
                    binding.button2.setTextColor(ContextCompat.getColor(this@MypageProfileActivity, R.color.Gray900))
                    binding.textchecking2.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.button2.setOnClickListener {
            finish()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        // 비밀번호가 8자리 이상이며 영문, 숫자, 특수문자를 포함하는지 확인하는 로직
        val pattern = Pattern.compile("(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@$!%*?&]{8,}")
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }

    private fun togglePasswordVisibility(editText: EditText, imageView: ImageView) {
        // 비밀번호 보이기/감추기 기능 구현
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            // 비밀번호를 보이도록 변경
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            imageView.setImageResource(R.drawable.eye_on)
        } else {
            // 비밀번호를 감추도록 변경
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            imageView.setImageResource(R.drawable.eye_off)
        }

        // 커서를 마지막으로 이동하여 비밀번호 글자가 보이도록 함
        editText.setSelection(editText.text.length)
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data!!

            // 이제 selectedImageUri를 사용하여 이미지를 처리하고, ImageView에 설정할 수 있습니다.
            // 예시로 Glide 사용하는 방법:
            Glide.with(this)
                .load(selectedImageUri)
                .into(binding.imageView18)
        }
    }
}