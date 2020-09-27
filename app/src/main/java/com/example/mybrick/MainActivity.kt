package com.example.mybrick

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.mybrick.database.DatabaseSingleton
import com.example.mybrick.database.entity.Inventory


class MainActivity : AppCompatActivity() {

    private fun loadInventoriesList() {
        Thread {
            val alsoArchived: Boolean = getSharedPreferences("mySettings", Context.MODE_PRIVATE).getBoolean(
                resources.getString(
                    R.string.show_archived
                ), false
            )
            inventoriesLiveData.postValue(
                DatabaseSingleton.getInstance(this).InventoriesDAO().findAll(
                    alsoArchived
                )
            )
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

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val deleteColor = Color.RED
        val cancelColor = Color.DKGRAY
        val archiveColor = Color.parseColor("#CCCC00")
        val unarchiveColor = Color.parseColor("#EEEE00")
        val openColor = Color.parseColor("#009900")

        val listView = findViewById<ListView>(R.id.listView)

        val inventoriesObserver = Observer<List<Inventory>> { it ->
            myList = it.toMutableList()
            myList.sortByDescending { it.lastAccess }
            listView.adapter = InventoryAdapter(this, myList)
        }

        inventoriesLiveData.observe(this, inventoriesObserver)

        listView.setOnItemClickListener { _, _, position, _ ->
            val element = listView.adapter.getItem(position) as Inventory


            val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        // przejscie do widoku projektu
                        val intent = Intent(this, AboutProjectActivity::class.java)
                        intent.putExtra("name", element.name)
                        startActivity(intent)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        val dialogClickListener = DialogInterface.OnClickListener { _: DialogInterface, _: Int ->
                            Thread {
                                DatabaseSingleton.getInstance(this).InventoriesDAO()
                                    .changeArchiveStatus(element.name)
                                loadInventoriesList()
                            }.start()
                        }

                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(
                            if (element.active == 1) getString(R.string.archive_question) else getString(
                                R.string.unarchive_question
                            )
                        )
                            .setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", null).show()
                    }
                    DialogInterface.BUTTON_NEUTRAL -> {

                        val deletingThis = DialogInterface.OnClickListener { _: DialogInterface, _: Int ->
                            Thread {
                                DatabaseSingleton.getInstance(this).InventoriesPartsDAO().deteteForThis(element.id)
                                DatabaseSingleton.getInstance(this).InventoriesDAO().deleteThis(element.name)

                                loadInventoriesList()
                            }.start()
                        }

                        val dialog : AlertDialog

                        val builder = AlertDialog.Builder(this)
                        dialog = builder.setMessage("Are you sure you want to delete this project?")
                            .setPositiveButton("Yes", deletingThis)
                            .setNegativeButton("CANCEL", null).create()

                        dialog.setOnShowListener {

                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(deleteColor)
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(cancelColor)
                        }
                        dialog.show()
                    }
                }
            }

            val builder = AlertDialog.Builder(this)
            val dialog : AlertDialog
            dialog = builder.setMessage(element.name)
                .setPositiveButton("OPEN", dialogClickListener)
                .setNegativeButton("ARCHIVE", dialogClickListener)
                .setNeutralButton("DELETE", dialogClickListener).create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(deleteColor)

                if (element.active != 1) {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setText("UNARCHIVE")
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(unarchiveColor)
                } else
                {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(archiveColor)}

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(openColor)
            }
            dialog.show()

        }

        fun doingWhenAddProject(){
            val intent = Intent(this, AddProject::class.java)
            startActivity(intent)
        }

        val addProjectButton = findViewById<Button>(R.id.addProjectButton)
        val addProjectTextView = findViewById<TextView>(R.id.AddNewProject_textView)
        addProjectButton.setOnClickListener{
            doingWhenAddProject()
        }
        addProjectTextView.setOnClickListener{
            doingWhenAddProject()
        }

        fun doingWhenSettings(){
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val settingsTextView = findViewById<TextView>(R.id.Settings_textView)
        settingsButton.setOnClickListener{
            doingWhenSettings()
        }
        settingsTextView.setOnClickListener{
            doingWhenSettings()
        }

        fun doingWhenDelete(){
            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        Thread {
                            DatabaseSingleton.getInstance(this).InventoriesPartsDAO().deleteAll()
                            DatabaseSingleton.getInstance(this).InventoriesDAO().deleteAll()

                            loadInventoriesList()
                        }.start()
                    }
                }
            }

            val builder = AlertDialog.Builder(this)
            val dialog : AlertDialog
            dialog = builder.setMessage("Are you sure you want to delete all projects?")
                .setPositiveButton("YES", dialogClickListener)
                .setNeutralButton("CANCEL", null).create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(cancelColor)

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(deleteColor)
            }
            dialog.show()
        }

        val deleteButton = findViewById<Button>(R.id.button_deleteAll)
        val deleteTextView = findViewById<TextView>(R.id.DeleteAllProjects_textView)
        deleteButton.setOnClickListener{
            doingWhenDelete()
        }
        deleteTextView.setOnClickListener{
            doingWhenDelete()
        }

    }
}

