package com.jaysdevapp.modootimer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jaysdevapp.modootimer.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

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
        editor.remove("userNm")
        editor.apply()
        finish()
    }
}