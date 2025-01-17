package com.example.application

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.Model.NuclearTechnician
import com.example.application.Adapters.NuclearTechnicianAdapter
import com.example.application.Data.Common
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class NuclearUserActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val nuclearTechnicianList: ArrayList<NuclearTechnician> = ArrayList()
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var localBroadcastManager: LocalBroadcastManager


    private val checkOutNuclearUserReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Common.countdown_timer!!.cancel()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuclear_user)
        recyclerView = findViewById<View>(R.id.technician_recyclerListView) as RecyclerView
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(checkOutNuclearUserReceiver, IntentFilter(Common.KEY_DESTROY))
    }

    override fun onStart() {
        super.onStart()
        setRecyclerView()
        getNuclearTechnician()
    }

    private fun getNuclearTechnician() {

        val userRef: CollectionReference = FirebaseFirestore.getInstance().collection("User")

        userRef.get()
            .addOnCompleteListener {task ->
                if(task.isSuccessful) {

                    val querySnapshot: QuerySnapshot = task.result!!
                    val nuclearTechnician = ArrayList<NuclearTechnician>()

                    if (querySnapshot.isEmpty) {
                        val adapter = NuclearTechnicianAdapter(applicationContext, nuclearTechnician)
                        recyclerView.adapter = adapter

                    } else {
                        for (document in task.result!!) {
                            nuclearTechnician.add(NuclearTechnician(document.get("username").toString(), document.get("user_Id").toString()))
                        }
                        nuclearTechnician.sortWith(compareBy{it.userName})
                        val adapter = NuclearTechnicianAdapter(applicationContext, nuclearTechnician)
                        recyclerView.adapter = adapter
                    }
                }
            }
    }

    private fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)
        val myAdapter = NuclearTechnicianAdapter(applicationContext, nuclearTechnicianList)

        recyclerView = recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = myAdapter
            visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(checkOutNuclearUserReceiver)
        super.onDestroy()
    }
}
