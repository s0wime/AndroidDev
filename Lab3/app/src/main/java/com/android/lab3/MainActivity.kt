package com.android.lab3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun showResult(result: String) {
        val resultFragment = ResultFragment.newInstance(result)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentResultContainer, resultFragment)
            .commit()
    }

    fun hideResultAndClearInput() {
        val resultFragment = supportFragmentManager.findFragmentById(R.id.fragmentResultContainer)
        if (resultFragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(resultFragment)
                .commit()
        }

        val inputFragment = supportFragmentManager.findFragmentById(R.id.fragmentInput)
        if (inputFragment is InputFragment) {
            inputFragment.clearForm()
        }
    }
}
