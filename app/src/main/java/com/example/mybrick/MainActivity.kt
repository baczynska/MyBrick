package com.example.mybrick

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    val p1 = Project(1, "Project1")
    val p2 = Project(2, "Project2")
    val p3 = Project(3, "Project3")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.listView)

        val myList = arrayOf<Project>(p1, p2, p3)
        val listItems = arrayOfNulls<String>(myList.size)
        for (i in 0 until myList.size) {
            val item = myList[i]
            listItems[i] = item.name
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter
        

        val addProjectButton = findViewById<Button>(R.id.addProjectButton)
        addProjectButton.setOnClickListener{
            val intent = Intent(this, AddProject::class.java)
            startActivity(intent)
        }

        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener{
            val intent = Intent(this, SettingsActivity::class.java )
            startActivity(intent)
        }
    }
}