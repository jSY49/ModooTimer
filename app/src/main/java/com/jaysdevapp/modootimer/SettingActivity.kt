package com.jaysdevapp.modootimer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jaysdevapp.modootimer.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    //TODO 로그인 되어 있을 떄만 로그아웃 버튼 보이도록 수정

    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoutBtn.setOnClickListener { doLogout() }

    }
    private fun doLogout() {
        val pref = getSharedPreferences("userUtil", MODE_PRIVATE)
        val editor = pref.edit()
        editor.clear().apply()
        finish()
    }
}