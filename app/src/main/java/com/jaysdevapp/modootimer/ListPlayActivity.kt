package com.jaysdevapp.modootimer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jaysdevapp.modootimer.databinding.ActivityListPlayBinding

class ListPlayActivity : AppCompatActivity() {

    private val binding by lazy { ActivityListPlayBinding.inflate(layoutInflater) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val hour = intent.getIntExtra("hour",0)
        val min = intent.getIntExtra("min",0)
        val sec = intent.getIntExtra("sec",0)
        Log.d("ListPlayActivity","onCreate = \"$hour : $min : $sec\"")
        binding.btn.text = "$hour : $min : $sec"
        binding.btn.setOnClickListener { finish() }
    }
}