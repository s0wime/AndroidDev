package com.android.lab3

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.lab3.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var dbHelper: TrainDbHelper
    private lateinit var adapter: TrainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = TrainDbHelper(this)
        adapter = TrainAdapter(
            onDeleteClick = { record ->
                AlertDialog.Builder(this)
                    .setTitle("Видалення")
                    .setMessage("Видалити запис \"${record.departure} - ${record.arrival}\"?")
                    .setPositiveButton("Так") { _, _ ->
                        dbHelper.delete(record.id)
                        loadData()
                    }
                    .setNegativeButton("Ні", null)
                    .show()
            }
        )

        binding.recyclerHistory.adapter = adapter

        binding.buttonBack.setOnClickListener { finish() }

        binding.buttonDeleteAll.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Видалення")
                .setMessage("Видалити всі записи?")
                .setPositiveButton("Так") { _, _ ->
                    dbHelper.deleteAll()
                    loadData()
                }
                .setNegativeButton("Ні", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val records = dbHelper.getAll()
        adapter.submitList(records)

        if (records.isEmpty()) {
            binding.textEmpty.visibility = View.VISIBLE
            binding.recyclerHistory.visibility = View.GONE
            binding.buttonDeleteAll.visibility = View.GONE
        } else {
            binding.textEmpty.visibility = View.GONE
            binding.recyclerHistory.visibility = View.VISIBLE
            binding.buttonDeleteAll.visibility = View.VISIBLE
        }
    }
}
