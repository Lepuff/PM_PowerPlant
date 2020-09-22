package com.example.application.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.application.Model.NuclearTechnician
import com.example.application.R


class NuclearTechnicianAdapter(var context: Context, var arrayList: ArrayList<NuclearTechnician>): RecyclerView.Adapter<NuclearTechnicianAdapter.ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ItemHolder(inflater, parent)
        }

        override fun getItemCount(): Int {
            return arrayList.size
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val item: NuclearTechnician = arrayList[position]
            holder.bind(item)

            holder.itemView.setOnClickListener {

            }

        }

        class ItemHolder(inflater: LayoutInflater, parent: ViewGroup):
            RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_row_technician_user, parent, false)){

            private var username: TextView? = null

            init {
                username = itemView.findViewById(R.id.txt_row_username)

            }

            fun bind(nuclearTechnician: NuclearTechnician) {
                username?.text = nuclearTechnician.userName

            }

        }

}