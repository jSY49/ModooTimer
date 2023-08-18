package com.jaysdevapp.modootimer

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Point
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.jaysdevapp.modootimer.databinding.FragmentTimerListBinding

class TimerListFragment : Fragment() {

    companion object {
        fun newInstance() = TimerListFragment()
    }

    private lateinit var viewModel: TimerListViewModel
    private lateinit var binding : FragmentTimerListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerListBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TimerListViewModel::class.java)

        binding.addFloatingButton.setOnClickListener { showDialog() }


    }

    private fun showDialog() {
        val dialog = AddListDialog()

        dialog.setOnClickListener(object : AddListDialog.OnDialogClickListener {
            override fun onClicked(name: String,h: Int, m : Int, s :Int)
            {
                Toast.makeText(context,"$name : $h:$m:$s",Toast.LENGTH_LONG).show()
            }

        })

        dialog.show(requireActivity().supportFragmentManager,"AddTimer")
    }

}