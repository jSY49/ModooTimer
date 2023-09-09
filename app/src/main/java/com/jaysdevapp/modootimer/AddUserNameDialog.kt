package com.jaysdevapp.modootimer

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.jaysdevapp.modootimer.databinding.FragmentAddListDialogBinding
import com.jaysdevapp.modootimer.databinding.FragmentSetNameDialogBinding

class AddUserNameDialog : DialogFragment() {

    private lateinit var onClickListener: OnDialogClickListener
    fun setOnClickListener(listener: OnDialogClickListener)
    {
        onClickListener = listener
    }
    interface OnDialogClickListener
    {
        fun onClicked(name: String)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    private lateinit var binding: FragmentSetNameDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSetNameDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            dialog?.dismiss()
        }
        binding.saveButton.setOnClickListener {
            if(!binding.NameEdit.text.isBlank()){
                onClickListener.onClicked(binding.NameEdit.text.toString())
                dismiss()
            }

            else
                Toast.makeText(context,"Your name is Blank!",Toast.LENGTH_LONG).show()
        }


        binding.NameEdit.setOnEditorActionListener{ textView, action, event ->
            var handled = false

            if (action == EditorInfo.IME_ACTION_DONE) {
                // 키보드 내리기
                val inputMethodManager = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(textView.windowToken, 0)
                handled = true
            }

            handled
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

}