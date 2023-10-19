package com.jaysdevapp.modootimer

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TimerListViewModel : ViewModel() {

    var livedata: MutableLiveData<ArrayList<timerData>> = MutableLiveData()
    private var arr : ArrayList<timerData> = arrayListOf()
    var userId  : MutableLiveData<String> = MutableLiveData()
    companion object{
        lateinit var preferences: UserUtil
    }

    fun dataUpdate(name :String){
        arr.clear()
        getData(name)
    }

    fun dataSearchUpdate(name :String, keyword :String){
        getSearchData(name,keyword)
    }

    fun getUserId(context: Context){
        getId(context)
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
                        doc.data["sec"].toString(),
                        doc.data["checkId"].toString().toInt()
                        )
                    )
                }
                livedata.postValue(arr)
            }
            .addOnFailureListener { exception ->
                Log.w("TimerListViewModel_getData()", "Error getting documents: ", exception)
            }
    }


    private fun getSearchData(name: String, keyword: String) {

    }

    private fun getId(context: Context){
        preferences = UserUtil(context)
        userId.postValue(preferences.getString("userNm", ""))
    }

    fun addName(key: String, name: String) {
        preferences.setString(key,name)
    }
}