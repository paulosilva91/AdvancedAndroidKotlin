package com.example.android.clippingexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.android.clippingexample.controls.ClippedView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ClippedView(this))
    }
}
