package com.example.application.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.application.Model.TimeStamp
import com.example.application.R
import androidx.recyclerview.widget.RecyclerView
import com.example.application.Data.Common
import com.example.application.Model.TimeStamps
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList


class TimeStampAdapter(var context: Context, var arrayList: ArrayList<TimeStamp>): RecyclerView.Adapter<TimeStampAdapter.ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ItemHolder(inflater, parent)
        }

        override fun getItemCount(): Int {
            return arrayList.size
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val item:TimeStamp = arrayList[position]
            holder.bind(item)
        }

        class ItemHolder(inflater: LayoutInflater, parent: ViewGroup):
            RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_row_timestamp, parent, false)){

            private var clockInText: TextView? = null
            private var clockOutText: TextView? = null
            private var hoursText: TextView? = null
            private var radiationExposedText: TextView? = null

            init {
                clockInText = itemView.findViewById(R.id.txt_row_clocked_in)
                clockOutText = itemView.findViewById(R.id.txt_row_clocked_out)
                hoursText = itemView.findViewById(R.id.txt_row_hours)
                radiationExposedText =  itemView.findViewById(R.id.txt_row_radiation_exposed)

            }

            fun bind(timeStamp: TimeStamp) {

                clockInText?.text = getDateTime(timeStamp.clock_In!!.toDate())
                clockOutText?.text = getDateTime(timeStamp.clock_Out!!.toDate())
                hoursText?.text = timeStamp.hours.toString()
                radiationExposedText?.text = timeStamp.radiation_Exposed.toString()

            }

            private fun getDateTime(date: Date): String? {
                try {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    return sdf.format(date)

                } catch (e: Exception) {
                    return e.toString()
                }
            }
        }


}




