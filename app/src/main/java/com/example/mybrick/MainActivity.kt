package com.example.mybrick

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.mybrick.xml.DownloadXmlTask
import kotlinx.android.synthetic.main.activity_main.*


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

        listView.setOnItemClickListener { parent, view, position, id ->
            val element = adapter.getItem(position)


            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        // przejscie do widoku projektu
                        val intent = Intent(this, AboutProjectActivity::class.java)
                        intent.putExtra("title", element)
                        startActivity(intent)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {

                        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    // Archiwizacja projektu
                                }
                                DialogInterface.BUTTON_NEGATIVE -> {

                                }
                            }
                        }

                        val builder = AlertDialog.Builder(this)
                        builder.setMessage("Are you sure you want to archive this project")
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show()
                    }
                }
            }

            val builder = AlertDialog.Builder(this)
            builder.setMessage(element)
                .setPositiveButton("OPEN", dialogClickListener)
                .setNegativeButton("ARCHIVE", dialogClickListener).show()


        }
        

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

