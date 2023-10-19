package com.jaysdevapp.modootimer

import android.annotation.SuppressLint
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.jaysdevapp.modootimer.databinding.ActivityListPlayBinding
import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.timer

class ListPlayActivity : AppCompatActivity() {

    private lateinit var binding :ActivityListPlayBinding
    private var timer: Timer? = null
    private var vibrator : Vibrator? = null
    private var ringtone: Ringtone? = null
    private var pauseFlag = false

    var tmpName = ""
    var tmpH = 0
    var tmpM = 0
    var tmpS = 0
    var co : Job? =null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_list_play)
        setContentView(binding.root)

        tmpName = intent.getStringExtra("name").toString()
        tmpH = intent.getIntExtra("hour",0)
        tmpM = intent.getIntExtra("min",0)
        tmpS = intent.getIntExtra("sec",0)

        binding.listplay = LISTPLAY(tmpName,tmpH,tmpM,tmpS)
        setNotification()
        binding.cancelButton.setOnClickListener {
            cancelButtonClick()
            co?.cancel()
            binding.cntTextview.visibility=View.GONE
            finish()
        }
        binding.pauseButton.setOnClickListener { pauseButtonClick() }

        runTimerTask()
    }

    private fun setNotification() {

        //진동
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        val notify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(applicationContext, notify)

    }

    private fun cancelButtonClick() {
        timer?.cancel()
        vibrator?.cancel()
        ringtone?.stop()
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun runTimerTask() {
        timer?.cancel()
        binding.cntTextview.visibility=View.VISIBLE
        co =CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Default) {
                val anim = AnimationUtils.loadAnimation(applicationContext,R.anim.fade_in)
                for(i in 3 downTo 1){
                    runOnUiThread {
                        binding.cntTextview.startAnimation(anim)
                        binding.cntTextview.text=i.toString()
                    }
                    delay(1000)
                }
            }
            binding.cntTextview.visibility=View.GONE
        }

        timer = timer(period = 1000, initialDelay = 3200){
            // 0초 이상이면
            if (tmpS != 0) {
                //1초씩 감소
                tmpS--

                // 0분 이상이면
            } else if (tmpM != 0) {
                // 1분 = 60초
                tmpS = 60
                tmpS--
                tmpM--

                // 0시간 이상이면
            } else if (tmpH != 0) {
                // 1시간 = 60분
                tmpS = 60
                tmpM = 60
                tmpS--
                tmpM--
                tmpH--
            }

            runOnUiThread{
                binding.listplay = LISTPLAY(tmpName,tmpH,tmpM,tmpS)
                // 시분초가 다 0이라면 toast를 띄우고 타이머를 종료한다..
                if(tmpH == 0 && tmpM == 0 && tmpS == 0) {
                    val vloop = longArrayOf(600,300)//600진동 300 대기
//                    vibrator.vibrate(VibrationEffect.createOneShot(1000,50))
                    vibrator?.vibrate(vloop, 0) //계속 진동
                    ringtone?.play()

                    binding.cancelButton.text=resources.getString(R.string.done)
                    binding.pauseButton.visibility=View.GONE
                }
            }
        }

    }
}