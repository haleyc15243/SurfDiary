package com.halebop.surfdiary.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.halebop.surfdiary.application.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}