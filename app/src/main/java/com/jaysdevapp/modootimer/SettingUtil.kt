package com.jaysdevapp.modootimer

import android.content.Context
import android.content.SharedPreferences

class SettingUtil (context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("settingUtil", Context.MODE_PRIVATE)

    fun getValue(key: String, defValue: Boolean):Boolean{
        return preferences.getBoolean(key,defValue)
    }

    fun setValue(key: String, defValue: Boolean){
        preferences.edit().putBoolean(key, defValue).apply()
    }
}