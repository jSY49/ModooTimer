package com.jaysdevapp.modootimer

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jaysdevapp.modootimer.databinding.FragmentTimerListBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class   TimerListFragment : Fragment() {

    //TOdo 데이터 추가 후 리사이클러 자동 업데이트 = notifi 안먹힘
    //TODO 새타이머 추가 후 프래그먼트 업데이트 = onstart 하지 않음
    //TODO 로그아웃 후 기존 보이던 데이터 없애주기 (설정 화면 액티비티에서 돌아오면 )=
    
    companion object {
        fun newInstance() = TimerListFragment()
    }

    private lateinit var viewModel: TimerListViewModel
    private lateinit var binding: FragmentTimerListBinding
    private var myAdpater = MyTimerAdapter(arrayListOf())
    private lateinit var contxt :Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimerListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TimerListViewModel::class.java)
        contxt =activity!!.applicationContext

        setRecycler()
        viewModel.getUserId(contxt)
        viewModel.userId.observe(this, Observer { name ->
            if(name.isNotBlank()){
                viewModel.dataUpdate(name)
                viewModel.livedata.observe(this){
                    myAdpater.updateTimer(it)
                }

                binding.addFloatingButton.setOnClickListener {showDialog()}
                binding.swiperefresh.setOnRefreshListener {
                    viewModel.dataUpdate(name)
                    viewModel.livedata.observe(this){
                        myAdpater.updateTimer(it)
                    }
                    binding.swiperefresh.isRefreshing=false
                }

            }else{
                binding.addFloatingButton.setOnClickListener {showNameDialog()}
                binding.swiperefresh.setOnRefreshListener {
                    binding.swiperefresh.isRefreshing=false
                }
            }
        })

    }

    override fun onStart() {
        super.onStart()
        Log.d("TimerListFragment","onStart() called")
        viewModel.getUserId(contxt)
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

                db.collection("List").document(viewModel.userId.value.toString())
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
        val dialog = LoginDialog()

        dialog.setOnClickListener(object : LoginDialog.OnDialogClickListener {
            override fun onClicked(name: String) {
                viewModel.addName("userNm",name)
                viewModel.getUserId(contxt)
            }
        })
        dialog.show(requireActivity().supportFragmentManager, "AddName")
    }

}