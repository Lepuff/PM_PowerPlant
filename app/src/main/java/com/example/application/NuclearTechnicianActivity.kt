package com.example.application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class NuclearTechnicianActivity : AppCompatActivity() {


    private lateinit var button: Button
    private lateinit var button2: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuclear_technician)

        button = findViewById(R.id.timeStamp_button)
        button2 = findViewById(R.id.nuclear_button)

        button.setOnClickListener {
            startActivity(Intent(this@NuclearTechnicianActivity, TimeStampActivity::class.java))
        }

        button2.setOnClickListener {
            startActivity(Intent(this@NuclearTechnicianActivity, PowerPlantManagerActivity::class.java))
        }

    }
}
