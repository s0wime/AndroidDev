package com.android.lab3

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class TrainRecord(
    val id: Long,
    val departure: String,
    val arrival: String,
    val time: String,
    val createdAt: String
)

class TrainDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "trains.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "trains"
        private const val COL_ID = "id"
        private const val COL_DEPARTURE = "departure"
        private const val COL_ARRIVAL = "arrival"
        private const val COL_TIME = "time"
        private const val COL_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_DEPARTURE TEXT NOT NULL, " +
                "$COL_ARRIVAL TEXT NOT NULL, " +
                "$COL_TIME TEXT NOT NULL, " +
                "$COL_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insert(departure: String, arrival: String, time: String): Long {
        val values = ContentValues().apply {
            put(COL_DEPARTURE, departure)
            put(COL_ARRIVAL, arrival)
            put(COL_TIME, time)
        }
        return writableDatabase.insert(TABLE_NAME, null, values)
    }

    fun getAll(): List<TrainRecord> {
        val list = mutableListOf<TrainRecord>()
        val cursor = readableDatabase.query(
            TABLE_NAME, null, null, null, null, null, "$COL_ID DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    TrainRecord(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_ID)),
                        departure = it.getString(it.getColumnIndexOrThrow(COL_DEPARTURE)),
                        arrival = it.getString(it.getColumnIndexOrThrow(COL_ARRIVAL)),
                        time = it.getString(it.getColumnIndexOrThrow(COL_TIME)),
                        createdAt = it.getString(it.getColumnIndexOrThrow(COL_CREATED_AT))
                    )
                )
            }
        }
        return list
    }

    fun delete(id: Long): Int {
        return writableDatabase.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.toString()))
    }

    fun deleteAll(): Int {
        return writableDatabase.delete(TABLE_NAME, null, null)
    }
}
