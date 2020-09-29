package com.example.application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

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
