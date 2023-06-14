package com.template

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Firebase.firestore

        val docData = db.collection("database").document("check")

        docData.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("result_db", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("result_db", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("result_db", "get failed with ", exception)
            }
    }
}