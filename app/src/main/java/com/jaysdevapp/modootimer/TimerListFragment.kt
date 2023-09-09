package com.jaysdevapp.modootimer

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jaysdevapp.modootimer.databinding.FragmentTimerListBinding

class TimerListFragment : Fragment() {

    //TOdo 닉네임 설정 다이얼로그 호출 ✅
    //TODO 닉네임 중복 검사 checkBtn
    //TODO 닉네임 제한 (길이)
    //TODO  not중복 : db/shared 저장
    companion object {
        fun newInstance() = TimerListFragment()
        lateinit var preferences: UserUtil
    }

    private lateinit var viewModel: TimerListViewModel
    private lateinit var binding: FragmentTimerListBinding

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

        binding.addFloatingButton.setOnClickListener {
            var name = preferences.getString("userNm", "")

            Log.d("TimerListFragment","onActivityCreated UserName : $name")

            if (name.isBlank())
                showNameDialog()
            else
                showDialog()
        }


    }

    private fun showDialog() {
        val dialog = AddListDialog()

        dialog.setOnClickListener(object : AddListDialog.OnDialogClickListener {
            override fun onClicked(name: String, h: Int, m: Int, s: Int) {
//                Toast.makeText(context,"$name : $h:$m:$s",Toast.LENGTH_LONG).show()
                val db = Firebase.firestore
                val data = hashMapOf(
                    "name" to name,
                    "hour" to h,
                    "minute" to m,
                    "sec" to s
                )


                //TODO 회워아이디와 타이머 아이디 생성
                db.collection("List").document("회원아이디")
                    .collection("Timer").document("타이머아이디")
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
            override fun onClicked(name: String) {
                Toast.makeText(context,"$name",Toast.LENGTH_LONG).show()

            }
        })
        dialog.show(requireActivity().supportFragmentManager, "AddName")
    }
}