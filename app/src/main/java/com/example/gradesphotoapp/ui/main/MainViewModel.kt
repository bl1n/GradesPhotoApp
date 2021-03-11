package com.example.gradesphotoapp.ui.main

import androidx.lifecycle.ViewModel
import java.time.LocalDate

class MainViewModel : ViewModel() {

    fun toCamera() {
    }

    fun getNewFileName(): String {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            "${LocalDate.now()}"
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }
}
