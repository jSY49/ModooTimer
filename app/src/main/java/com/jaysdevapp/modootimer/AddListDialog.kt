package com.jaysdevapp.modootimer

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.jaysdevapp.modootimer.databinding.FragmentAddListDialogBinding


class AddListDialog : DialogFragment() {

    private var hour = 0
    private var min = 0
    private var sec = 0


    private lateinit var onClickListener: OnDialogClickListener
    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }
    interface OnDialogClickListener
    {
        fun onClicked(name: String, tHour:Int, tMin: Int, tSec:Int)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //false로 설정해 주면 화면밖 혹은 뒤로가기 버튼시 다이얼로그라 dismiss 되지 않는다.
        isCancelable = true
    }

    private lateinit var binding: FragmentAddListDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNumberPicker()
        binding.cancelButton.setOnClickListener {
            dialog?.dismiss()
        }
        binding.saveButton.setOnClickListener {
            if(!binding.NameEdit.text.isBlank()){
                onClickListener.onClicked(binding.NameEdit.text.toString(),hour,min,sec)
                dismiss()
            }

            else
                Toast.makeText(context,"Timer Info is Blank!",Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()

        //다이얼로그 사이즈 기기 사이즈 -> 다이얼로그 사이즈 설정
        val windowManager = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
//        val deviceheight = size.y
        params?.width = (deviceWidth * 0.9).toInt()
//        params?.height = (deviceheight * 0.5).toInt()
        //둥글둥글모서리
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun setNumberPicker() {

        binding.numberPicker1.maxValue = 23
        binding.numberPicker2.maxValue = 59
        binding.numberPicker3.maxValue = 59

        binding.numberPicker1.minValue = 0
        binding.numberPicker2.minValue = 0
        binding.numberPicker3.minValue = 0

        binding.numberPicker1.wrapSelectorWheel = true
        binding.numberPicker2.wrapSelectorWheel = true
        binding.numberPicker3.wrapSelectorWheel = true

        binding.numberPicker1.setOnValueChangedListener { picker, oldVal, newVal ->
            hour = newVal
        }
        binding.numberPicker2.setOnValueChangedListener { picker, oldVal, newVal ->
            min = newVal
        }
        binding.numberPicker3.setOnValueChangedListener { picker, oldVal, newVal ->
            sec = newVal
        }

    }


}