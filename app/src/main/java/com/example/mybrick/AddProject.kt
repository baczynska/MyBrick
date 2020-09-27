package com.example.mybrick

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.lifecycle.MutableLiveData
import com.example.mybrick.database.DatabaseSingleton
import com.example.mybrick.database.entity.Inventory
import com.example.mybrick.xml.DownloadXmlTask


class AddProject : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_project)

        val inputNumber = findViewById<EditText>(R.id.editTextNumber)
        val inputProjectName = findViewById<EditText>(R.id.editTextName)
        val addButton = findViewById<Button>(R.id.addButton)

        val progressBar: ProgressBar = findViewById(R.id.add_project_progressBar)
        progressBar.isVisible = false

        val activity = this

        inputNumber.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                if(inputNumber.text.isNotEmpty()){
                    addButton.isEnabled = true
                }
                else if(inputNumber.text.isEmpty()){
                    addButton.isEnabled = false
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }
        })

        addButton.setOnClickListener{

            progressBar.isVisible = true

            val dialogClickListener =
                DialogInterface.OnClickListener { _, _ ->
                    addButton.isEnabled = false
                    DownloadXmlTask(activity).execute()
                }

            if( inputProjectName.text.isEmpty()) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to add project without name")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", null).show()
            }
            else{
                addButton.isEnabled = false
                DownloadXmlTask(activity).execute()
            }
        }
    }

    val inventoriesLiveData: MutableLiveData<List<Inventory>> by lazy {
        MutableLiveData<List<Inventory>>()
    }

    fun loadInventoriesList() {
        Thread {
            val alsoArchived: Boolean = getSharedPreferences("mySettings", MODE_PRIVATE).getBoolean(resources.getString(R.string.show_archived), false)

            inventoriesLiveData.postValue(DatabaseSingleton.getInstance(this).InventoriesDAO().findAll(alsoArchived))
        }.start()
    }
}