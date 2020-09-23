package com.example.application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.Model.NuclearTechnician
import com.example.application.Model.TimeStamp
import com.example.application.adapters.NuclearTechnicianAdapter
import com.example.application.adapters.TimeStampAdapter
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class PowerPlantManagerActivity : AppCompatActivity() {

    private lateinit var nuclearUserBtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_power_plant_manager)
        nuclearUserBtn = findViewById(R.id.nuclear_user_button)

        nuclearUserBtn.setOnClickListener {
            startActivity(Intent(this@PowerPlantManagerActivity, NuclearUserActivity::class.java))
        }
    }



}
