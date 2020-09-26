package com.example.mybrick

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.mybrick.database.DatabaseSingleton
import com.example.mybrick.database.entity.Inventory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.mybrick.xml.DownloadXmlTask
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    fun loadInventoriesList() {
        Thread {
            val alsoArchived: Boolean = getSharedPreferences("Preferences", Context.MODE_PRIVATE).getBoolean(resources.getString(R.string.show_archived), false)
            inventoriesLiveData.postValue(DatabaseSingleton.getInstance(this).InventoriesDAO().findAll(alsoArchived))
        }.start()
    }

    val inventoriesLiveData: MutableLiveData<List<Inventory>> by lazy {
        MutableLiveData<List<Inventory>>()
    }
    private var myList = mutableListOf<Inventory>()

    override fun onResume() {
        super.onResume()
        loadInventoriesList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.listView)

        val inventoriesObserver = Observer<List<Inventory>> {it ->
            myList = it.toMutableList()
            myList.sortByDescending { it.lastAccess }
            listView.adapter = InventoryAdapter(this, myList)
        }

        inventoriesLiveData.observe(this, inventoriesObserver)

//        Thread {
//            inventoriesLiveData.postValue(DatabaseSingleton.getInstance(this).InventoriesDAO().findAll())
//        }.start()

        listView.setOnItemClickListener { parent, view, position, id ->
            val element = listView.adapter.getItem(position) as Inventory


            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        // przejscie do widoku projektu
                        val intent = Intent(this, AboutProjectActivity::class.java)
                        intent.putExtra("name", element.name)
                        startActivity(intent)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    // Archiwizacja/dearchiwizacja projektu
                                    Thread {
                                        DatabaseSingleton.getInstance(this).InventoriesDAO().changeArchiveStatus(element.name)
                                        loadInventoriesList()
                                    }.start()
                                }
                            }
                        }

                        val builder = AlertDialog.Builder(this)
                        builder.setMessage( if (element.active == 1) getString(R.string.archive_question) else getString(R.string.unarchive_question))
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show()
                    }
                }
            }

            val builder = AlertDialog.Builder(this)
            if (element != null) {
                builder.setMessage(element.name)
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

