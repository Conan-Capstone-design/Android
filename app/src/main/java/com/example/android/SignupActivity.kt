package com.example.android

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.android.databinding.ActivityAgreefirstBinding
import com.example.android.databinding.ActivityLoginBinding
import com.example.android.databinding.ActivitySignupBinding
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // EditText를 binding으로 참조
        val writePassword = binding.writePassword
        val checkPassword = binding.checkPassword
        val eyeImageView = binding.imageView23
        val eyeCheckImageView = binding.imageView24
        val writeEmail = binding.writeEmail
        val idError = binding.idError
        val passwordErrorMatching = binding.passwordErrorMatching
        val writeNickname = binding.writeNickname
        val nicknameError = binding.nicknameError
        val passwordErrorCondition = binding.passwordErrorCondition

        // 초기에 비밀번호를 가리도록 설정
        writePassword.transformationMethod = PasswordTransformationMethod.getInstance()
        checkPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        eyeImageView.setOnClickListener {
            togglePasswordVisibility(writePassword, eyeImageView)
        }
        eyeCheckImageView.setOnClickListener {
            togglePasswordVisibility(checkPassword, eyeCheckImageView)
        }

        binding.imageView10.setOnClickListener {
            finish()
        }

        // 아이디 작성 후 커서가 깜빡이지 않을 때 오류 메시지 표시
        writeEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val emailText = s.toString()
                if (!emailText.contains("@") && emailText.isNotBlank()) {
                    idError.visibility = View.VISIBLE
                } else {
                    idError.visibility = View.GONE
                }
                updateButtonBackground()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 비밀번호 유효성 검사 함수
        fun isValidPassword(password: String): Boolean {
            val pattern: Pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!~@#\$%^&*(),.?\":{}|<>]).{8,}$")
            return pattern.matcher(password).matches()
        }

        // writePassword에 텍스트 변경 감지자 추가
        writePassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val passwordText = s.toString()
                if (passwordText.length < 8 || !isValidPassword(passwordText)) {
                    binding.passwordErrorCondition.visibility = View.VISIBLE
                    checkPassword.isEnabled = false
                } else {
                    binding.passwordErrorCondition.visibility = View.GONE
                    checkPassword.isEnabled = true
                }
                updateButtonBackground()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // check_password 작성 후
        checkPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val checkPasswordText = s.toString()
                val writePasswordText = writePassword.text.toString()

                if (checkPasswordText != writePasswordText) {
                    passwordErrorMatching.visibility = View.VISIBLE
                } else {
                    passwordErrorMatching.visibility = View.GONE
                }
                updateButtonBackground()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // writeNickname에 특수 문자가 있는지 검사하여 오류 메시지 표시
        writeNickname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val nicknameText = s.toString()
                if (containsSpecialCharacter(nicknameText)) {
                    nicknameError.visibility = View.VISIBLE
                } else {
                    nicknameError.visibility = View.GONE
                }
                updateButtonBackground()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.signupbtn.setOnClickListener {
            val idErrorVisible = binding.idError.visibility != View.VISIBLE
            val passwordMatchingVisible = binding.passwordErrorMatching.visibility != View.VISIBLE
            val nicknameErrorVisible = binding.nicknameError.visibility != View.VISIBLE
            val passwordConditionVisible = binding.passwordErrorCondition.visibility != View.VISIBLE

            val isInputFilled =
                binding.writeEmail.text.isNotBlank() &&
                        binding.writePassword.text.isNotBlank() &&
                        binding.checkPassword.text.isNotBlank() &&
                        binding.writeNickname.text.isNotBlank()

            val isAllValid =
                idErrorVisible &&
                        passwordMatchingVisible &&
                        nicknameErrorVisible &&
                        passwordConditionVisible &&
                        isInputFilled

            if (isAllValid) {
                val email = binding.writeEmail.text.toString()
                val password = binding.writePassword.text.toString()
                val nickName = binding.writeNickname.text.toString()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun containsSpecialCharacter(text: String): Boolean {
        val pattern = Pattern.compile("[!~@#$%^&*(),.?\":{}|<>]")
        val matcher = pattern.matcher(text)
        return matcher.find()
    }

    private fun togglePasswordVisibility(editText: EditText, imageView: ImageView) {
        if (editText.transformationMethod == PasswordTransformationMethod.getInstance()) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            imageView.setImageResource(R.drawable.eye_on)
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            imageView.setImageResource(R.drawable.eye_off)
        }
        editText.setSelection(editText.text.length)
    }

    private fun updateButtonBackground() {
        val idErrorVisible = binding.idError.visibility != View.VISIBLE
        val passwordMatchingVisible = binding.passwordErrorMatching.visibility != View.VISIBLE
        val nicknameErrorVisible = binding.nicknameError.visibility != View.VISIBLE
        val passwordConditionVisible = binding.passwordErrorCondition.visibility != View.VISIBLE

        val isInputFilled =
            binding.writeEmail.text.isNotBlank() &&
                    binding.writePassword.text.isNotBlank() &&
                    binding.checkPassword.text.isNotBlank() &&
                    binding.writeNickname.text.isNotBlank()

        val isAllValid = idErrorVisible &&
                passwordMatchingVisible &&
                nicknameErrorVisible &&
                passwordConditionVisible &&
                isInputFilled

        if (isAllValid) {
            binding.signupbtn.setBackgroundResource(R.drawable.blue_rectangle)
            binding.signupbtn.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.signupbtn.isEnabled = true
        } else {
            binding.signupbtn.setBackgroundResource(R.drawable.gray_rectangle)
            binding.signupbtn.setTextColor(ContextCompat.getColor(this, R.color.Gray900))
            binding.signupbtn.isEnabled = false
        }
    }
}