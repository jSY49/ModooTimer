package com.jaysdevapp.modootimer

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jaysdevapp.modootimer.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignupBinding
    private var checkFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cancelButton.setOnClickListener {   finish()   }

        binding.saveButton.setOnClickListener {

            if (!binding.NameEdit.text.isBlank() && !binding.NameEdit.text.isBlank() && checkFlag) {

            } else
                Toast.makeText(this, "Your name check Please!", Toast.LENGTH_LONG).show()
        }

        binding.checkBtn.setOnClickListener {
            if (binding.NameEdit.text.isBlank()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.askUserName),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val rootRef = FirebaseFirestore.getInstance()
                val docIdRef = rootRef.collection("User").document(binding.NameEdit.text.toString())
                docIdRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document.exists()) {
                            checkFlag = false
//                            binding.NameInfo.text=resources.getString(R.string.namechck_false)
//                            binding.NameInfo.setTextColor(resources.getColor(R.color.red))
                        } else {
                            checkFlag = true
//                            binding.NameInfo.text=resources.getString(R.string.namechck_true)
//                            binding.NameInfo.setTextColor(resources.getColor(R.color.blue))
                        }

                    } else {
                        Log.d("Check Id", "Failed with: ", task.exception)
                    }
                }
            }

        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        if (currentFocus is EditText) {
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

}



/*
//회원가입 디비 추가
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
}*/
