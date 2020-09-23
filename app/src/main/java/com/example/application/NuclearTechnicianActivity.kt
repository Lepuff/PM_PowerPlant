package com.example.application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class NuclearTechnicianActivity : AppCompatActivity() {


    private lateinit var timeStampBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuclear_technician)

        timeStampBtn = findViewById(R.id.timeStamp_button)

        timeStampBtn.setOnClickListener {
            startActivity(Intent(this@NuclearTechnicianActivity, TimeStampActivity::class.java))
        }



    }
}
