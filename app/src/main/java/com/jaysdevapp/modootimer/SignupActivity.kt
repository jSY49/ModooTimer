package com.jaysdevapp.modootimer

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jaysdevapp.modootimer.databinding.ActivitySignupBinding


class SignupActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignupBinding
    private var checkFlag = false
    private var checkPWFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                            binding.IdInfo.text = resources.getString(R.string.namechck_false)
                            binding.IdInfo.setTextColor(resources.getColor(R.color.red))
                        } else {
                            checkFlag = true
                            binding.IdInfo.text = resources.getString(R.string.namechck_true)
                            binding.IdInfo.setTextColor(resources.getColor(R.color.blue))
                        }

                    } else {
                        Log.d("Check Id", "Failed with: ", task.exception)
                    }
                }
            }

        }


        binding.cancelButton.setOnClickListener { finish() }

        binding.saveButton.setOnClickListener {

            if (!binding.NameEdit.text.isBlank() && !binding.NameEdit.text.isBlank() && checkFlag && checkPWFlag) {
                val db = Firebase.firestore
                val id = binding.NameEdit.text.toString()
                val pw = binding.passwordEdit.text.toString()
                val data = hashMapOf(
                    "id" to id,
                    "pw" to pw
                )
                db.collection("User").document(id)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(this, R.string.saveSuccess, Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, R.string.saveFailure, Toast.LENGTH_LONG).show()
                    }
            } else
                Toast.makeText(this, "Check your Info Please!", Toast.LENGTH_LONG).show()
        }

        binding.checkPasswordEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val input: String = binding.passwordEdit.text.toString()
                if (input.contentEquals(s)) {
                    binding.passwordInfo.text=resources.getText(R.string.pwchck_true)
                    binding.passwordInfo.setTextColor(resources.getColor(R.color.blue))
                    checkPWFlag = true
                } else {
                    binding.passwordInfo.text = resources.getText(R.string.pwchck_false)
                    binding.passwordInfo.setTextColor(resources.getColor(R.color.red))
                    checkPWFlag = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
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



