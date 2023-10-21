package com.jaysdevapp.modootimer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.jaysdevapp.modootimer.MainActivity.Companion.pref
import com.jaysdevapp.modootimer.databinding.ActivitySettingBinding


class SettingActivity : AppCompatActivity(), View.OnClickListener {

    //TODO 로그인 되어 있을 떄만 로그아웃 버튼 보이도록 수정

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.soundSwitch.isChecked = pref.getValue("sound",false)
        binding.vibrationSwitch.isChecked = pref.getValue("vibration",false)


        binding.logoutBtn.setOnClickListener(this)
        binding.soundSwitch.setOnCheckedChangeListener { switch, isChecked -> setAlarm(switch, isChecked , this) }
        binding.vibrationSwitch.setOnCheckedChangeListener { switch, isChecked -> setAlarm(switch, isChecked , this) }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.logout_btn -> {
                val pref = getSharedPreferences("userUtil", MODE_PRIVATE)
                val editor = pref.edit()
                editor.clear().apply()
                finish()
            }
            else -> {}
        }
    }

    private fun setAlarm(switch: CompoundButton?, checked: Boolean,context: Context) {
        Log.d("SettingActivity", "setAlarm-> { ${switch?.id}} is $checked")

        when (switch?.id) {
            R.id.sound_switch -> {
                pref.setValue("sound",checked)
            }
            R.id.vibration_switch -> {
                pref.setValue("vibration",checked)
            }
            else -> {}
        }

    }
}