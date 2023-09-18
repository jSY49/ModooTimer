package com.jaysdevapp.modootimer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jaysdevapp.modootimer.databinding.FragmentLoginDialogBinding


class LoginDialog : DialogFragment() {

    private lateinit var onClickListener: OnDialogClickListener
    val res : MutableLiveData<Boolean> = MutableLiveData()
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

    private lateinit var binding: FragmentLoginDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            dialog?.dismiss()
        }
        binding.saveButton.setOnClickListener {
            val id =binding.idTextview.text
            val pw = binding.passwordTextview.text
            if(id.isNotBlank()&&pw.isNotBlank()){
                //TODO 로그인 되면 로그인 후 dismiss & 아니면 토스트
                checkLoginInfo(id.toString(),pw.toString())
                res.observe(this, Observer {
                    if(res.value == true){
                        Log.d("AddUserNameDialog","onViewCreated() called = res : ${res.value}")
                        onClickListener.onClicked(id.toString())
                        dismiss()
                    }else{
                        Toast.makeText(context,resources.getText(R.string.Loginfailed),Toast.LENGTH_LONG).show()
                    }
                })
            }
            else
                Toast.makeText(context,resources.getText(R.string.askFill),Toast.LENGTH_LONG).show()
        }

//        binding.idTextview.setOnEditorActionListener{ textView, action, event ->
//            var handled = false
//
//            if (action == EditorInfo.IME_ACTION_DONE) {
//                // 키보드 내리기
//                val inputMethodManager = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//                inputMethodManager.hideSoftInputFromWindow(textView.windowToken, 0)
//                handled = true
//            }
//
//            handled
//        }

        binding.signupBtn.setOnClickListener {
            dismiss()
            val intent = Intent(activity?.applicationContext, SignupActivity::class.java)
            startActivity(intent)

        }

    }

    private fun checkLoginInfo(id: String, pw: String){

        val db = Firebase.firestore
        db.collection("User")
            .whereEqualTo("id",id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if(pw.contentEquals(document.data["pw"].toString())) {
                        res.postValue(true)
                    }else{
                        res.postValue(false)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("AddUserNameDialog", "Error getting documents: ", exception)
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