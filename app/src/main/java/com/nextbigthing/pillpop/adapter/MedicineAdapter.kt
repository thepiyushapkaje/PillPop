package com.nextbigthing.pillpop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nextbigthing.pillpop.R
import com.nextbigthing.pillpop.model.Medicine

class MedicineAdapter(private val medicineList: List<Medicine>) :
    RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    inner class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.medicineName)
        val timeTextView: TextView = itemView.findViewById(R.id.medicineTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.medicine_item, parent, false)
        return MedicineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicineList[position]
        holder.nameTextView.text = medicine.name
        holder.timeTextView.text = medicine.time
    }

    override fun getItemCount() = medicineList.size
}