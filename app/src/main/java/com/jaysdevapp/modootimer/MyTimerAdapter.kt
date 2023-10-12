package com.jaysdevapp.modootimer

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
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
    private val selectedItems = SparseBooleanArray()
    private var prePosition = -1
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

            binding.deleteButton.setOnClickListener{
                val builder = timerListFragment.deleteDialog(currentdata.get(position))
                builder.show()
            }
            binding.modifyButton.setOnClickListener {
                timerListFragment.showDialog(currentdata.get(position))
            }
            binding.playButton.setOnClickListener {
                //todo 프래그먼트 이동(데이터 가지고)
                timerListFragment.goListTimer(currentdata.get(position))
            }

            if(selectedItems.get(position)) binding.imageView.setImageResource(R.drawable.baseline_expand_less_24)
            else binding.imageView.setImageResource(R.drawable.baseline_expand_more_24)
            changeVisibility(selectedItems.get(position),binding.hideLayout)

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
        val MyViewHolder: MyViewHolder = holder
        val tmp = datas
        holder.bind(tmp)

        holder.itemView.setOnClickListener {

            if (selectedItems.get(position)) {
                // 펼쳐진 Item을 클릭 시
                selectedItems.delete(position)
            } else {
                // 직전의 클릭됐던 Item의 클릭상태를 지움
                selectedItems.delete(prePosition)
                // 클릭한 Item의 position을 저장
                selectedItems.put(position, true)
            }
            // 해당 포지션의 변화를 알림
            if (prePosition != -1) notifyItemChanged(prePosition)
            notifyItemChanged(position)
            // 클릭된 position 저장
            prePosition = MyViewHolder.adapterPosition
        }

    }


    // 뷰 홀더의 개수 리턴
    override fun getItemCount(): Int {
        return datas.size
    }

    private fun changeVisibility(isExpanded: Boolean, hideLay: RelativeLayout) {
        // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
        val va = if (isExpanded) {
            ValueAnimator.ofInt(0, 200)
        }else {
            ValueAnimator.ofInt(200, 0)
        }
        // Animation이 실행되는 시간, n/1000초
        va.duration = 500
        va.addUpdateListener { animation -> // imageView의 높이 변경
            hideLay.layoutParams.height = animation.animatedValue as Int
            hideLay.requestLayout()
            hideLay.visibility = if (isExpanded) View.VISIBLE else View.GONE
        }
        // Animation start
        va.start()
    }


}