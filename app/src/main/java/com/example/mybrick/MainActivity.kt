package com.example.mybrick

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.mybrick.database.DatabaseSingleton
import com.example.mybrick.database.entity.Inventory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer


class MainActivity : AppCompatActivity() {

    var adapter : ArrayAdapter<String>? = null;

    private val inventoriesLiveData: MutableLiveData<List<Inventory>> by lazy {
        MutableLiveData<List<Inventory>>()
    }
    private val myList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.listView)

//        val myList = arrayOf<Project>(p1, p2, p3)
//        val listItems = arrayOfNulls<String>(myList.size)
//        for (i in 0 until myList.size) {
//            val itemName = myList[i].name
//            listItems[i] = itemName
//        }

        val inventoriesObserver = Observer<List<Inventory>> {
            // Update the UI, in this case, a TextView.
            myList.clear()
            it.forEach {
                myList.add(it.name)
            }
            adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, myList)
            listView.adapter = adapter
        }

        inventoriesLiveData.observe(this, inventoriesObserver)

        Thread {
            inventoriesLiveData.postValue(DatabaseSingleton.getInstance(this).InventoriesDAO().findAll())
        }.start()

        listView.setOnItemClickListener { parent, view, position, id ->
            val element = adapter?.getItem(position)


            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        // przejscie do widoku projektu
                        val intent = Intent(this, AboutProjectActivity::class.java)
                        if (element != null) {

                            val index = adapter?.getPosition(element)

                            intent.putExtra("name", element)

                            startActivity(intent)
                        }


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
            if (element != null) {
                builder.setMessage(element)
                    .setPositiveButton("OPEN", dialogClickListener)
                    .setNegativeButton("ARCHIVE", dialogClickListener).show()
            }


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

