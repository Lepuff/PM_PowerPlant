package com.example.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.Model.TimeStamp
import com.example.application.Adapters.TimeStampAdapter
import com.example.application.Data.Common_2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class TimeStampActivity : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private val timeStampList: ArrayList<TimeStamp> = ArrayList()
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_stamp)

        recyclerView = findViewById<View>(R.id.timeStamp_recyclerListView) as RecyclerView
    }

    override fun onStart() {
        super.onStart()
        setRecyclerView()
        getTimeStamps()
    }


    private fun getTimeStamps() {

        val userId: String = if(Common_2.currentRole!!) {
            intent.getStringExtra("userId")!!
        }
        else {
            Common_2.currentUserId!!
        }

        val workRef: CollectionReference = FirebaseFirestore.getInstance().collection("Work").document(userId).collection("Date")

        workRef.get()
            .addOnCompleteListener {task ->
                if(task.isSuccessful) {

                    val querySnapshot: QuerySnapshot = task.result!!
                    val timeStamp = ArrayList<TimeStamp>()

                    if (querySnapshot.isEmpty) {
                        val adapter = TimeStampAdapter(applicationContext, timeStamp)
                        recyclerView.adapter = adapter

                    } else {
                        for (document in task.result!!) {
                            timeStamp.add(TimeStamp(document.getTimestamp("clock_In")!!.toDate(),document.getTimestamp("clock_Out")!!.toDate(), document.getDouble("hours"), document.getDouble("radiation_Exposed") ))
                        }

                        val adapter = TimeStampAdapter(applicationContext, timeStamp)
                        recyclerView.adapter = adapter
                    }
                }
            }
    }

    private fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)
        val myAdapter = TimeStampAdapter(applicationContext, timeStampList)

        recyclerView = recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = myAdapter
            visibility = View.VISIBLE
        }
    }


}
