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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jaysdevapp.modootimer.databinding.FragmentBasicTimerBinding
import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.timer

class BasicTimerFragment : Fragment() {

    companion object {
        fun newInstance() = BasicTimerFragment()
    }

    private lateinit var viewModel: BasicTimerViewModel
    private lateinit var binding: FragmentBasicTimerBinding
    lateinit var numberPicker : SetNumberPicker
    var tmpH = 0
    var tmpM = 0
    var tmpS = 0
    var co : Job? =null

    private var pauseFlag = false
    private var timer: Timer? = null

    lateinit var vibrator :Vibrator
    lateinit var ringtone: Ringtone
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBasicTimerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[BasicTimerViewModel::class.java]

        setNotification()
        numberPicker = SetNumberPicker(binding.numberPicker1,binding.numberPicker2,binding.numberPicker3,0,0,0)
        numberPicker.setting()

        binding.startButton.setOnClickListener { startButtonClick() }
        binding.cancelButton.setOnClickListener { cancelButtonClick() }
        binding.pauseButton.setOnClickListener { pauseButtonClick() }


    }

    private fun setNotification() {

        //진동
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = activity?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            activity?.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        val notify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(activity!!.applicationContext, notify)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun pauseButtonClick() {
        pauseFlag = !pauseFlag
        Log.d("BasicTimerFragment","pauseButtonClick - pauseFlag : $pauseFlag")
        if(pauseFlag) {  //정지 상태
            timer?.cancel()
            binding.pauseButton.text = resources.getString(R.string.replay)
        }else{//다시 시작
            tmpS+=1
            runTimerTask()
            binding.pauseButton.text = resources.getString(R.string.pause)
        }

    }

    private fun cancelButtonClick() {
        //1. 타이머 정지
        timer?.cancel()
        //2. layout 변경
        binding.stopLayout.visibility = View.VISIBLE
        binding.TimerLayout.visibility = View.GONE
        binding.cntTextview.visibility=View.GONE

        co?.cancel()
        vibrator.cancel()
        ringtone.stop()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startButtonClick() {
        var hour  = numberPicker.getHour()
        var min  = numberPicker.getMin()
        var sec  = numberPicker.getSec()
        binding.pauseButton.text = resources.getString(R.string.pause)
        if (hour != 0 || min != 0 || sec != 0) {
            binding.stopLayout.visibility = View.GONE
            binding.TimerLayout.visibility = View.VISIBLE
            binding.hourTv.text = hour.toString()
            binding.minTv.text = min.toString()
            binding.secTv.text = sec.toString()

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
            binding.cntTextview.visibility=View.GONE
        }

        timer = timer(period = 1000, initialDelay = 3000) {
            // 0초 이상이면
            if (tmpS != 0) {
                //1초씩 감소
                tmpS--;

                // 0분 이상이면
            } else if (tmpM != 0) {
                // 1분 = 60초
                tmpS = 60;
                tmpS--;
                tmpM--;

                // 0시간 이상이면
            } else if (tmpH != 0) {
                // 1시간 = 60분
                tmpS = 60;
                tmpM = 60;
                tmpS--;
                tmpM--;
                tmpH--;
            }

            activity?.runOnUiThread{
                // UI 조작
                if(tmpS <= 9){
                    binding.secTv.text=("0$tmpS")
                } else {
                    binding.secTv.text= tmpS.toString()
                }

                if(tmpM <= 9){
                    binding.minTv.text=("0$tmpM");
                } else {
                    binding.minTv.text= tmpM.toString()
                }

                if(tmpH <= 9){
                    binding.hourTv.text = "0$tmpH";
                } else {
                    binding.hourTv.text=(tmpH.toString());
                }

                // 시분초가 다 0이라면 toast를 띄우고 타이머를 종료한다..
                if(tmpH == 0 && tmpM == 0 && tmpS == 0) {
                    val vloop = longArrayOf(600,300)//600진동 300 대기
//                    vibrator.vibrate(VibrationEffect.createOneShot(1000,50))
                    vibrator.vibrate(vloop, 0) //계속 진동
                    ringtone.play()

                    binding.cancelButton.text=resources.getString(R.string.done)
                    binding.pauseButton.visibility=View.GONE
                }
            }
        }

    }

}