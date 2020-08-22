package com.example.mybrick

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_main.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val showArchivedSwitch = findViewById<Switch>(R.id.archivedSwitch)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val pathPrefixOfURL = findViewById<EditText>(R.id.pathPrefixOfUrlPlainText)
        val settingsView = findViewById<ConstraintLayout>(R.id.settingsView)

        pathPrefixOfURL.setText(settings.url)
        showArchivedSwitch.isChecked = settings.showArchived

        saveButton.setOnClickListener {
            settings.url = pathPrefixOfURL.text.toString()
            settings.showArchived = showArchivedSwitch.isChecked
            this.finish()
        }

    }

}