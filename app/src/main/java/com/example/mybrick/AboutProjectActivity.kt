package com.example.mybrick

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutProjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_project)

        val title = findViewById<TextView>(R.id.textView_title)
        val titleToPut = intent.getStringExtra("title")

        title.text = titleToPut


    }







}