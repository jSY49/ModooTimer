package com.jaysdevapp.modootimer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jaysdevapp.modootimer.MainActivity.Companion.pref
import com.jaysdevapp.modootimer.databinding.FragmentBasicTimerBinding
import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.timer

class BasicTimerFragment : Fragment() {

    private lateinit var viewModel: BasicTimerViewModel
    private lateinit var binding: FragmentBasicTimerBinding
    lateinit var numberPicker : SetNumberPicker
    var tmpH = 0
    var tmpM = 0
    var tmpS = 0
    var co : Job? =null

    private var pauseFlag = false
    private var timer: Timer? = null

    var vibrator :Vibrator? = null
    var ringtone: Ringtone? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_basic_timer, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[BasicTimerViewModel::class.java]

        numberPicker = SetNumberPicker(binding.numberPicker1,binding.numberPicker2,binding.numberPicker3,0,0,0)
        numberPicker.setting()

        binding.startButton.setOnClickListener { startButtonClick() }
        binding.cancelButton.setOnClickListener { cancelButtonClick() }
        binding.pauseButton.setOnClickListener { pauseButtonClick() }

    }

    private fun setNotification() {

        //진동
        if (pref.getValue("vibration", false)) {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    activity?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                activity?.getSystemService(VIBRATOR_SERVICE) as Vibrator
            }
        }
        if (pref.getValue("sound", false)) {
            val notify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(activity!!.applicationContext, notify)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pauseButtonClick() {
        pauseFlag = !pauseFlag
        Log.d("BasicTimerFragment","pauseButtonClick - pauseFlag : $pauseFlag")
        if(pauseFlag) {  //정지 상태
            timer?.cancel()
            co?.cancel()
            binding.cntTextview.clearAnimation()
            binding.cntTextview.visibility=View.INVISIBLE
            binding.pauseButton.text = resources.getString(R.string.replay)
        }else{//다시 시작
            tmpS+=1
            runTimerTask()
            binding.pauseButton.text = resources.getString(R.string.pause)
        }

    }

    private fun cancelButtonClick() {
        //카운트다운 정지
        co?.let {
            binding.cntTextview.clearAnimation()
            binding.cntTextview.visibility=View.INVISIBLE
            it.cancel()
        }
        //타이머 정지
        timer?.let {
            binding.TimerLayout.visibility = View.INVISIBLE
            binding.stopLayout.visibility = View.VISIBLE
            it.cancel()
        }
        vibrator?.cancel()
        ringtone?.stop()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startButtonClick() {
        setNotification()
        var hour  = numberPicker.getHour()
        var min  = numberPicker.getMin()
        var sec  = numberPicker.getSec()
        binding.pauseButton.text = resources.getString(R.string.pause)
        if (hour != 0 || min != 0 || sec != 0) {
            binding.stopLayout.visibility = View.INVISIBLE
            binding.TimerLayout.visibility = View.VISIBLE
            binding.timer =TIMER(hour,min,sec)

            tmpH = hour
            tmpM = min
            tmpS = sec
            startTimer()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun startTimer() {
        binding.pauseButton.visibility=View.VISIBLE
        runTimerTask()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun runTimerTask() {
        timer?.cancel()

        startCont()

        timer = timer(period = 1000, initialDelay = 3200) {
            if (tmpS != 0) {// 0초 아니면
                tmpS--
            } else if (tmpM != 0) {// 0분 아니면
                tmpS = 59
                tmpM--
            } else if (tmpH != 0) {// 0시간 아니면
                tmpS = 59
                tmpM = 59
                tmpH--
            }

            activity?.runOnUiThread{
                binding.timer = TIMER(tmpH,tmpM,tmpS)
                // 시분초가 다 0이
                if(tmpH == 0 && tmpM == 0 && tmpS == 0) {
                    val vloop = longArrayOf(600,300)//600진동 300 대기
//                    vibrator.vibrate(VibrationEffect.createOneShot(1000,50))
                    vibrator?.vibrate(vloop, 0) //계속 진동
                    ringtone?.play()

                    binding.cancelButton.text=resources.getString(R.string.done)
                    binding.pauseButton.visibility=View.INVISIBLE
                }
            }
        }

    }
    private fun startCont(){
        binding.cntTextview.visibility=View.VISIBLE
        co = CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Default) {

                val anim = AnimationUtils.loadAnimation(activity?.applicationContext,R.anim.fade_in)
                for(i in 3 downTo 1){
                    activity?.runOnUiThread {
                        binding.cntTextview.startAnimation(anim)
                        binding.cntTextview.text=i.toString()
                    }
                    delay(1000)
                }
            }
            binding.cntTextview.clearAnimation()
            binding.cntTextview.visibility=View.INVISIBLE
        }
    }

}