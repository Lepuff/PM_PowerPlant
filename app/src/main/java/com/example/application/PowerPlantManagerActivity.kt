package com.example.application

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.application.Data.Common

class PowerPlantManagerActivity : AppCompatActivity() {

    private lateinit var nuclearUserBtn: Button
    private lateinit var localBroadcastManager: LocalBroadcastManager

    private val checkOutPowerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Common.countdown_timer!!.cancel()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_power_plant_manager)
        nuclearUserBtn = findViewById(R.id.nuclear_user_button)
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(checkOutPowerReceiver, IntentFilter(Common.KEY_DESTROY))

        nuclearUserBtn.setOnClickListener {
            startActivity(Intent(this@PowerPlantManagerActivity, NuclearUserActivity::class.java))
        }
    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(checkOutPowerReceiver)
        super.onDestroy()
    }

}
