package com.android.lab3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.lab3.databinding.ItemTrainBinding

class TrainAdapter(
    private val onDeleteClick: (TrainRecord) -> Unit
) : ListAdapter<TrainRecord, TrainAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val binding: ItemTrainBinding) : RecyclerView.ViewHolder(binding.root)

    private class DiffCallback : DiffUtil.ItemCallback<TrainRecord>() {
        override fun areItemsTheSame(a: TrainRecord, b: TrainRecord) = a.id == b.id
        override fun areContentsTheSame(a: TrainRecord, b: TrainRecord) = a == b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTrainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = getItem(position)
        holder.binding.textRoute.text = "${record.departure} → ${record.arrival}"
        holder.binding.textTime.text = "Час: ${record.time}"
        holder.binding.textDate.text = record.createdAt
        holder.binding.buttonDelete.setOnClickListener { onDeleteClick(record) }
    }
}
