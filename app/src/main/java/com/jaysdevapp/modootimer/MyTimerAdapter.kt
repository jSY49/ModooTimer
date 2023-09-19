package com.jaysdevapp.modootimer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.jaysdevapp.modootimer.databinding.TimerItemBinding


class MyTimerAdapter(
    private val con: FragmentActivity?,
    private var datas: ArrayList<timerData>,
    private var timerListFragment: TimerListFragment
) :
    RecyclerView.Adapter<MyTimerAdapter.MyViewHolder>() {

    val TAG = "MyMovieAdapter"

    @SuppressLint("NotifyDataSetChanged")
    fun updateTimer(newDatas: ArrayList<timerData>) {
        datas.clear()
        datas.addAll(newDatas)
        notifyDataSetChanged()
    }

    // 생성된 뷰 홀더에 값 지정
    inner class MyViewHolder(val binding: TimerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context = binding.root.context

        fun bind(currentdata: List<timerData>) {
            binding.timerNm.text= currentdata[position].name

            binding.deleteImageButton.setOnClickListener{
                val builder = timerListFragment.deleteDialog(currentdata.get(position))
                 builder.show()
                //TODO  리사이클러 아이템 삭제
            }
            binding.modifyImageButton.setOnClickListener {
                //todo 데이터 수정 dialog
            }
            binding.playImageButton.setOnClickListener {
                //프래그먼트 이동(데이터 가지고)
            }

        }
    }

    // 어떤 xml 으로 뷰 홀더를 생성할지 지정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //val binding = RecyclerMovieBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        val inflater = LayoutInflater.from(parent.context)
        val binding = TimerItemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    // 뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tmp = datas
        holder.bind(tmp)
    }


    // 뷰 홀더의 개수 리턴
    override fun getItemCount(): Int {
        return datas.size
    }



}