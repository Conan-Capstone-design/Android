package com.example.android

import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.connection.RetrofitClient
import com.example.android.connection.RetrofitObject
import com.example.android.databinding.FragmentHomeBinding
import com.example.android.databinding.FragmentMypageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentMypage: Fragment() {
    private lateinit var binding: FragmentMypageBinding
    private lateinit var user: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var token: String
    private lateinit var adapter: CharacterListsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        // 사용자 SharedPreferences 초기화
        user = MyApplication.getUser()
        token = user.getString("jwt", "").toString()


        binding.imageViesecession.setOnClickListener {
            val intent = Intent(requireContext(), SecessionActivity::class.java)
            startActivity(intent)
        }

        binding.imageView13.setOnClickListener {
            val intent = Intent(requireContext(), MypageProfileActivity::class.java)
            startActivity(intent)
        }

        binding.imageView17.setOnClickListener {
            val intent = Intent(requireContext(), MypageSaveActivity::class.java)
            startActivity(intent)
        }

        binding.imageView18.setOnClickListener {
            (activity as? MainActivity)?.switchToFragmentTTS()
        }

        binding.imageVielogout.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            val stackBuilder = TaskStackBuilder.create(requireContext())
            stackBuilder.addNextIntentWithParentStack(intent)
            stackBuilder.startActivities()
        }
        loadProfileImage()
        return binding.root
    }

    private fun loadProfileImage() {
        val call = RetrofitObject.getRetrofitService.getprofile("$token")
        call.enqueue(object : Callback<RetrofitClient.Responsegetprofile> {
            override fun onResponse(call: Call<RetrofitClient.Responsegetprofile>, response: Response<RetrofitClient.Responsegetprofile>) {
                if (response.isSuccessful) {
                    Log.d("Retrofit4", response.toString())
                    // response.body()를 통해 Responsegetmypage 객체에 접근
                    val profileData = response.body()?.result
                    Log.d("Retrofit41", profileData.toString())
                    profileData?.let {
                        // 이미지를 가져와서 이미지뷰에 설정
                        val imageUrl = it.user.profileImg
                        if (imageUrl != null) {
                            // 이미지 URL이 null이 아닌 경우 Glide를 사용하여 이미지 로드
                            Glide.with(requireContext())
                                .load(imageUrl)
                                .into(binding.mypage)
                        } else {
                            binding.mypage.setImageResource(R.drawable.default_icon)
                        }

                        // 닉네임을 가져와서 SharedPreferences에 저장
                        val userName = it.user.nickname
                        if (userName != null) {
                            binding.textView35.text = userName
                        }

                        val email = it.user.email
                        if (email != null) {
                            binding.textView36.text = email
                        }

                        val characterData = it.commentList
                        // 식물 데이터가 없으면 'textViewnot'을 보이도록 처리
                        if (characterData.isNullOrEmpty()) {
                            binding.characterlistFalse.visibility = View.VISIBLE
                            binding.characterlistTrue.visibility = View.GONE
                        } else {
                            binding.characterlistFalse.visibility = View.GONE
                            binding.characterlistTrue.visibility = View.VISIBLE
                            setupRecyclerView(characterData)
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        response.body()?.message ?: "Unknown error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onFailure(call: Call<RetrofitClient.Responsegetprofile>, t: Throwable) {
                val errorMessage = "Call Failed: ${t.message}"
                Log.d("Retrofit", errorMessage)
            }
        })
    }

    private fun setupRecyclerView(plantData: List<RetrofitClient.commentList>) {
        // RecyclerView의 레이아웃 매니저 설정
        binding.listListRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        // RecyclerView의 어댑터 설정
        adapter = CharacterListsAdapter(plantData)
        binding.listListRv.adapter = adapter
    }
    override fun onResume() {
        super.onResume()
        // 프로필 이미지 다시 불러오기
        loadProfileImage()
    }
}