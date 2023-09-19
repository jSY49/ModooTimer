package com.jaysdevapp.modootimer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
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

    companion object {
        fun newInstance() = TimerListFragment()
    }

    private lateinit var viewModel: TimerListViewModel
    private lateinit var binding: FragmentTimerListBinding
    private lateinit var myAdpater : MyTimerAdapter
    private lateinit var contxt :Context
    private var userName : String = ""
    private var data : ArrayList<timerData> = arrayListOf()

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
        contxt = activity!!.applicationContext
        myAdpater = MyTimerAdapter(this.activity,arrayListOf(),this)
        setRecycler()
        viewModel.getUserId(contxt)
        dataObserve()
    }

    private fun dataObserve() {
        viewModel.userId.observe(this, Observer { name ->
            if(name.isNotBlank()){
                userName = name
                viewModel.dataUpdate(userName)
                data.clear()
                viewModel.livedata.observe(this){
                    data = it
                    Log.d("TimerListFragment","${data}")
                    myAdpater.updateTimer(data)
                }

                binding.addFloatingButton.setOnClickListener {showDialog()}
                binding.swiperefresh.setOnRefreshListener {
                    viewModel.dataUpdate(userName)
                    binding.swiperefresh.isRefreshing=false
                }

            }else{
                data.clear()
                myAdpater.updateTimer(data)
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
        userName=""
        viewModel.getUserId(contxt)
        Log.d("TimerListFragment","onStart_ userName: ${viewModel.userId.value}")
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
                        viewModel.dataUpdate(userName)
                        Log.d("TimerListFragment","onClicked tName : $userName")
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

    fun deleteDialog(data: timerData): AlertDialog.Builder {
        val actvty = this.activity
        val builder = AlertDialog.Builder(actvty)
        builder
            .setTitle(actvty!!.resources.getString(R.string.deleteInfo))
            .setMessage(actvty.resources.getString(R.string.askDelete,data.name))
            .setCancelable(false)
            .setPositiveButton("확인") { p0, p1 ->
                val name = TimerListViewModel.preferences.getString("userNm","")
                val db = Firebase.firestore
                db.collection("List").document(name).collection("Timer").document(data.id)
                    .delete()
                    .addOnSuccessListener {
                        userName=""
                        viewModel.getUserId(contxt)
                        Toast.makeText(contxt,"삭제 완료되었습니다.",Toast.LENGTH_LONG).show()
                        Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e ->
                        Toast.makeText(contxt,"삭제 실패.",Toast.LENGTH_LONG).show()
                        Log.w(TAG, "Error deleting document", e) }
            }
            .setNegativeButton("취소") { p0, p1 -> }
            .create()
        return builder


    }
}