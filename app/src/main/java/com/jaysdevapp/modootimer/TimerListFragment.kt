package com.jaysdevapp.modootimer

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jaysdevapp.modootimer.databinding.FragmentTimerListBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class   TimerListFragment : Fragment() {

    //TOdo 닉네임 설정 다이얼로그 호출 ✅
    //TODO 닉네임 중복 검사 checkBtn ✅
    //TODO  not중복 : db/shared 저장 ✅
    //TODO 닉네임 제한 (길이)
    //TOdo 리사이클러 뷰 ✅


    companion object {
        fun newInstance() = TimerListFragment()
        lateinit var preferences: UserUtil
    }

    private lateinit var viewModel: TimerListViewModel
    private lateinit var binding: FragmentTimerListBinding
    private lateinit var name : String
    private var myAdpater = MyTimerAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TimerListViewModel::class.java)
        preferences = UserUtil(activity!!.applicationContext)
        name = preferences.getString("userNm", "")
        setRecycler()

        if(name.isNotBlank()){
            viewModel.dataUpdate(name)
            viewModel.livedata.observe(this){
                myAdpater.updateTimer(it)
            }
        }

        binding.addFloatingButton.setOnClickListener {
            if (name.isBlank()) showNameDialog()
            else    showDialog()
        }
    }

    fun setRecycler(){
        binding.timerRecycler.apply {
            layoutManager = LinearLayoutManager(context).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }
            adapter = myAdpater
        }
    }

    private fun showDialog() {
        val dialog = AddListDialog()

        dialog.setOnClickListener(object : AddListDialog.OnDialogClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onClicked(tName: String, h: Int, m: Int, s: Int) {
//                Toast.makeText(context,"$name : $h:$m:$s",Toast.LENGTH_LONG).show()
                val db = Firebase.firestore
                val data = hashMapOf(
                    "name" to tName,
                    "hour" to h,
                    "minute" to m,
                    "sec" to s
                )

                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
                val formatted = current.format(formatter)

                //TODO 회워아이디와 타이머 아이디 생성
                db.collection("List").document(name)
                    .collection("Timer").document()
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(context, R.string.saveSuccess, Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, R.string.saveFailure, Toast.LENGTH_LONG).show()
                    }


            }

        })

        dialog.show(requireActivity().supportFragmentManager, "AddTimer")
    }
    private fun showNameDialog() {
        val dialog = AddUserNameDialog()

        dialog.setOnClickListener(object : AddUserNameDialog.OnDialogClickListener {
            override fun onClicked(name: String,flag : Boolean) {
                if(flag) {
                    val db = Firebase.firestore
                    val data = hashMapOf(
                        "name" to name,
                    )

                    //TODO 회워아이디와 타이머 아이디 생성
                    db.collection("User").document(name)
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener {
                            Toast.makeText(context, R.string.saveSuccess, Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, R.string.saveFailure, Toast.LENGTH_LONG).show()
                        }

                preferences.setString("userNm",name)

                }else{

                }
            }
        })
        dialog.show(requireActivity().supportFragmentManager, "AddName")
    }

}