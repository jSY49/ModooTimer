package com.jaysdevapp.modootimer


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jaysdevapp.modootimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val fragmentManager = supportFragmentManager
    private var basicTimerFragment: BasicTimerFragment? = null
    private var timerListFragment: TimerListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        setNavigaion()

    }

    private fun setNavigaion() {

        basicTimerFragment = BasicTimerFragment()
        fragmentManager.beginTransaction().replace(R.id.main_layout, basicTimerFragment!!).commitAllowingStateLoss();

        _binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_basic -> {
                    if(basicTimerFragment == null){
                        basicTimerFragment = BasicTimerFragment()
                        fragmentManager.beginTransaction().add(R.id.main_layout,basicTimerFragment!!).commit()
                    }
                    if(basicTimerFragment != null) fragmentManager.beginTransaction().show(basicTimerFragment!!).commit()
                    if(timerListFragment != null) fragmentManager.beginTransaction().hide(timerListFragment!!).commit()
                    return@setOnItemSelectedListener true
                }

                R.id.navigation_list -> {
                    if(timerListFragment==null){
                        timerListFragment = TimerListFragment()
                        fragmentManager.beginTransaction().add(R.id.main_layout,timerListFragment!!).commit()
                    }
                    if(basicTimerFragment != null) fragmentManager.beginTransaction().hide(basicTimerFragment!!).commit()
                    if(timerListFragment != null) fragmentManager.beginTransaction().show(timerListFragment!!).commit()
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener true
            }
        }
    }
}