package com.jaysdevapp.modootimer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jaysdevapp.modootimer.databinding.FragmentBasicTimerBinding
import java.util.*
import kotlin.concurrent.timer

class BasicTimerFragment : Fragment() {

    companion object {
        fun newInstance() = BasicTimerFragment()
    }

    private lateinit var viewModel: BasicTimerViewModel
    private lateinit var _binding: FragmentBasicTimerBinding
    private var hour = 0
    private var min = 0
    private var sec = 0
    var tmpH = hour
    var tmpM = min
    var tmpS = sec

    private var pauseFlag = false
    private var timer: Timer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBasicTimerBinding.inflate(layoutInflater, container, false)
        return _binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[BasicTimerViewModel::class.java]

        setNumberPicker()
        _binding.startButton.setOnClickListener { startButtonClick() }
        _binding.cancelButton.setOnClickListener { cancelButtonClick() }
        _binding.pauseButton.setOnClickListener { pauseButtonClick() }


    }

    private fun pauseButtonClick() {
        pauseFlag = !pauseFlag
        Log.d("BasicTimerFragment","pauseButtonClick - pauseFlag : $pauseFlag")
        if(pauseFlag) {  //정지 상태
            timer?.cancel()
            _binding.pauseButton.text = resources.getString(R.string.replay)
        }else{//다시 시작
            tmpS+=1
            runTimerTask()
            _binding.pauseButton.text = resources.getString(R.string.pause)
        }

    }

    private fun cancelButtonClick() {
        //1. 타이머 정지
        timer?.cancel()
        //2. layout 변경
        _binding.stopLayout.visibility = View.VISIBLE
        _binding.TimerLayout.visibility = View.GONE
    }

    private fun startButtonClick() {
        _binding.pauseButton.text = resources.getString(R.string.pause)
        if (hour != 0 || min != 0 || sec != 0) {
            _binding.stopLayout.visibility = View.GONE
            _binding.TimerLayout.visibility = View.VISIBLE
            _binding.hourTv.text = hour.toString()
            _binding.minTv.text = min.toString()
            _binding.secTv.text = sec.toString()

            tmpH = hour
            tmpM = min
            tmpS = sec
            startTimer()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startTimer() {
        _binding.pauseButton.visibility=View.VISIBLE
        runTimerTask()
    }

    private fun runTimerTask() {
        timer?.cancel()
        timer = timer(period = 1000) {
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
                    _binding.secTv.text=("0$tmpS")
                } else {
                    _binding.secTv.text= tmpS.toString()
                }

                if(tmpM <= 9){
                    _binding.minTv.text=("0$tmpM");
                } else {
                    _binding.minTv.text= tmpM.toString()
                }

                if(tmpH <= 9){
                    _binding.hourTv.text = "0$tmpH";
                } else {
                    _binding.hourTv.text=(tmpH.toString());
                }

                // 시분초가 다 0이라면 toast를 띄우고 타이머를 종료한다..
                if(tmpH == 0 && tmpM == 0 && tmpS == 0) {
                    _binding.cancelButton.text=resources.getString(R.string.done)
                    _binding.pauseButton.visibility=View.GONE
                }
            }
        }

    }

    private fun setNumberPicker() {

        _binding.numberPicker1.maxValue = 23
        _binding.numberPicker2.maxValue = 59
        _binding.numberPicker3.maxValue = 59

        _binding.numberPicker1.minValue = 0
        _binding.numberPicker2.minValue = 0
        _binding.numberPicker3.minValue = 0

        _binding.numberPicker1.wrapSelectorWheel = true
        _binding.numberPicker2.wrapSelectorWheel = true
        _binding.numberPicker3.wrapSelectorWheel = true

        _binding.numberPicker1.setOnValueChangedListener { picker, oldVal, newVal ->
            hour = newVal
        }
        _binding.numberPicker2.setOnValueChangedListener { picker, oldVal, newVal ->
            min = newVal
        }
        _binding.numberPicker3.setOnValueChangedListener { picker, oldVal, newVal ->
            sec = newVal
        }

    }


}