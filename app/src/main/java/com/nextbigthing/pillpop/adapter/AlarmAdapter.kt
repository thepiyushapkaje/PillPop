package com.nextbigthing.pillpop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nextbigthing.pillpop.databinding.MedicineItemBinding
import com.nextbigthing.pillpop.model.Alarm

class AlarmAdapter(private val alarms: List<Alarm>) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    inner class AlarmViewHolder(private val binding: MedicineItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alarm: Alarm) {
            binding.medicineName.text = alarm.medicineName
            binding.medicineTime.text = String.format("%02d:%02d", alarm.hour, alarm.minute)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = MedicineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(alarms[position])
    }

    override fun getItemCount() = alarms.size
}