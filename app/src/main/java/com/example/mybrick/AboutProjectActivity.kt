package com.example.mybrick

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.mybrick.database.DatabaseSingleton
import com.example.mybrick.database.entity.InventoryPart
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AboutProjectActivity : AppCompatActivity() {
    private val inventoriesPartsLiveData: MutableLiveData<List<LayoutRowData>> by lazy {
        MutableLiveData<List<LayoutRowData>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_project)

        val progressBar: ProgressBar = findViewById(R.id.about_project_progressBar)
        progressBar.isVisible = true

        val listView = findViewById<ListView>(R.id.listView)

        val title = findViewById<TextView>(R.id.textView_title)
        val name = intent.getStringExtra("name")

        title.text = name

        val partsObserver = Observer<List<LayoutRowData>> {
            progressBar.isVisible = false
            listView.adapter = ItemAdapter(this, it)
        }

        inventoriesPartsLiveData.observe(this, partsObserver)

        Thread {
            val databaseSingleton: DatabaseSingleton = DatabaseSingleton.getInstance(this)
            databaseSingleton.InventoriesDAO().updateLastAccessTime(name)
            val codeInventory: Int? = databaseSingleton.InventoriesDAO().findIdByName(name)
            if (codeInventory != null) {
                val inventoryPartsList: List<InventoryPart> = databaseSingleton.InventoriesPartsDAO().findAllByInventoryId(
                    codeInventory)
                inventoriesPartsLiveData.postValue(inventoryPartsList.map {
                    LayoutRowData(this,
                        it)
                })
            } else {
                throw Throwable("Inventory not found")
            }
        }.start()
    }

}
