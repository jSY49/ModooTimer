package com.jaysdevapp.modootimer


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val curId: Int = item.getItemId()
        when (curId) {
            R.id.menu_setting -> {
                val intent = Intent(applicationContext, SettingActivity::class.java)
                startActivity(intent)
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        if(currentFocus is EditText) {
            currentFocus!!.clearFocus()
        }

        return super.dispatchTouchEvent(ev)
    }
}