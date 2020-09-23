package com.example.application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var nuclearTechnicianBtn: Button
    private lateinit var powerPlantManagerBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nuclearTechnicianBtn = findViewById(R.id.layout_nuclear_technician_button)
        powerPlantManagerBtn = findViewById(R.id.layout_power_plant_button)

        nuclearTechnicianBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, NuclearTechnicianActivity::class.java))
        }

        powerPlantManagerBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PowerPlantManagerActivity::class.java))
        }

    }


}
