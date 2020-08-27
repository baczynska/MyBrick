package com.example.mybrick

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about_project.*
import kotlinx.android.synthetic.main.row_item.*

class AboutProjectActivity : AppCompatActivity() {

    val i1 = Item("Lego1", "Bardzo wazny element", "image.jpg")
    val i2 = Item("Lego2", "tez bardzo wazny element", "image.jpg")
    val i3 = Item("Lego3", "ekstra element", "image.jpg")
    val i4 = Item("Lego4", "inny element", "image.jpg")
    val i5 = Item("Lego5", "inny", "image.jpg")

    val myList = arrayOf<Item>(i1, i2, i3, i4, i5)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_project)

        val title = findViewById<TextView>(R.id.textView_title)
        val code = intent.getIntExtra("code", 0)

        title.text = code.toString()

        val listView = findViewById<ListView>(R.id.listView)
        val listItems = arrayOfNulls<String>(myList.size)

        for (i in 0 until myList.size) {
            val item = myList[i]
            listItems[i] = item.title
        }

        val adapter = ItemAdapter(this)
        listView.adapter = adapter



    }



}
