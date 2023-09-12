package com.jaysdevapp.modootimer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TimerListViewModel : ViewModel() {

    var livedata: MutableLiveData<ArrayList<timerData>> = MutableLiveData()
    private var arr : ArrayList<timerData> = arrayListOf()
    fun dataUpdate(name :String){
        getData(name)
    }

    private fun getData(name :String){
        val db = Firebase.firestore
        db.collection("List").document(name).collection("Timer")
//            .whereEqualTo("capital", true)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    arr.add(timerData(
                        doc.id,
                        doc.data["name"].toString(),
                        doc.data["hour"].toString(),
                        doc.data["minute"].toString(),
                        doc.data["sec"].toString())
                    )
                }
                livedata.postValue(arr)
            }
            .addOnFailureListener { exception ->
                Log.w("TimerListViewModel_getData()", "Error getting documents: ", exception)
            }
    }
}