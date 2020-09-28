package com.example.mybrick.xml

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.view.isVisible
import java.io.IOException
import java.io.InputStream
import java.lang.NullPointerException
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import android.content.Intent
import android.os.AsyncTask
import kotlin.jvm.Throws
import com.example.mybrick.AboutProjectActivity
import com.example.mybrick.AddProject
import com.example.mybrick.R
import com.example.mybrick.database.DatabaseSingleton
import com.example.mybrick.database.entity.Code
import com.example.mybrick.database.entity.Inventory
import com.example.mybrick.database.entity.InventoryPart
import org.xmlpull.v1.XmlPullParserException

class xmlDownload(private val activity: AddProject) : AsyncTask<String, Void, String>() {

    val inputId: String = activity.findViewById<EditText>(R.id.editTextNumber).editableText.toString()
    val inputName: String = activity.findViewById<EditText>(R.id.editTextName).editableText.toString()
    var err: Boolean = false

    override fun doInBackground(vararg urls: String): String {
        val sharedPref = activity.getSharedPreferences("mySettings", Context.MODE_PRIVATE)
        val sourceUrl = sharedPref.getString("sourceUrl", activity.application.resources.getString(R.string.url) )

        val inputIdAsInt = inputId.toIntOrNull()

        if (inputIdAsInt != null) {

            // to id już istnieje
            if (DatabaseSingleton.getInstance(activity.application).InventoriesDAO().checkById(inputIdAsInt)) {
                err = true
                return activity.application.resources.getString(R.string.id_exists_error)
            }

            // ta nazwa już istnieje
            if (DatabaseSingleton.getInstance(activity.application).InventoriesDAO().checkByName(inputName)) {
                err = true
                return activity.application.resources.getString(R.string.name_repeated_error)
            }

        } else {
            err = true
            return activity.application.resources.getString(R.string.not_a_number)
        }

        try {
            return loadXmlFromNetwork("$sourceUrl$inputId.xml", inputId, inputName) }
        catch (e: IOException) {
            err = true
            return activity.application.resources.getString(R.string.connectionError)
        } catch (e: XmlPullParserException) {
            err = true
            return activity.application.resources.getString(R.string.xmlError)
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->

            val intent = Intent(activity, AboutProjectActivity::class.java)
            if( inputName.isNotEmpty()){
                    intent.putExtra("name", inputName)}
            else{
                    intent.putExtra("name", inputId)
                }
            activity.startActivity(intent)
        }

        val finishActivity = DialogInterface.OnClickListener { dialog, which ->
            activity.finish()
        }

        if (err) {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(result).setPositiveButton("OK", null).show()
        } else {

            val builder = AlertDialog.Builder(activity)
            builder.setMessage(result)
                .setPositiveButton("Go to project", dialogClickListener)
                .setNegativeButton("Back to menu", finishActivity).show()
        }


        val progressBar: ProgressBar = activity.findViewById(R.id.add_project_progressBar)
        progressBar.isVisible = false

        activity.findViewById<Button>(R.id.addButton).isEnabled = true
        activity.loadInventoriesList()
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun loadXmlFromNetwork( urlString: String, inventoryId: String, inventoryName: String, ): String {

        val parts: HashMap<String, List<*>>? = downloadFromUrl(urlString)?.use(XMLParser(activity.application)::parse)

        if (parts != null) {

            var inventoryNameToSave = inventoryId
            val currentTime = Instant.now().epochSecond

            if (inventoryName.length > 0){
                inventoryNameToSave = inventoryName
            }

            DatabaseSingleton.getInstance(activity.applicationContext).InventoriesDAO().insert(
                Inventory( inventoryId.toInt(), inventoryNameToSave, lastAccess = currentTime.toInt())
            )
            val partsNotFounded = ArrayList<String>(parts["itemsNotFound"] as List<String>)
            (parts["items"] as List<Entry>).forEach {
                try {
                    val newInventoryPart: InventoryPart = it.castToInventoryPart(inventoryId.toInt())

                    saveImage(newInventoryPart, activity.applicationContext)

                    DatabaseSingleton.getInstance(activity.applicationContext).InventoriesPartsDAO().insertPart(newInventoryPart)
                } catch (e: NullPointerException) {
                    partsNotFounded.add(partNotFound.createMessage(it.itemID, it.colorID))
                }
            }
            return activity.application.resources.getString(R.string.xmlSuccess)
        }
        return activity.application.resources.getString(R.string.xmlError)
    }


    @Throws(IOException::class)
    private fun downloadFromUrl(urlString: String): InputStream? {

        return (URL(urlString).openConnection() as? HttpURLConnection)?.run {
            readTimeout = 10000
            connectTimeout = 15000
            requestMethod = "GET"
            doInput = true
            connect()
            inputStream
        }
    }

    private fun saveImage(part: InventoryPart, context: Context) {

        val databaseSingleton = DatabaseSingleton.getInstance(context)
        val partCode = databaseSingleton.PartsDAO().findCodeById(part.itemID)
        val colorCode = databaseSingleton.ColorsDAO().findCodeById(part.colorId)

        fun loadImage(): String {
            if (colorCode != null) {
                return "https://www.bricklink.com/P/$colorCode/$partCode.jpg" }
            else {
                return "https://www.bricklink.com/PL/$partCode.jpg" }
        }

        val partFounded = databaseSingleton.CodesDAO().findByItemIdAndColorId(
            part.itemID,
            part.colorId
        )

        if (partFounded != null) {
            try {
                partFounded.image = downloadImageWithURL(loadImage())
                DatabaseSingleton.getInstance(context).CodesDAO().updateCode(partFounded)
            } catch (e: IOException) {
                throw IOException(activity.application.resources.getString(R.string.imageForPartNotFound))
            }
        } else{
            try {
                DatabaseSingleton.getInstance(context).CodesDAO()
                    .insertNewCode(Code(0, part.itemID, part.colorId, null, downloadImageWithURL(loadImage())))
            } catch (e: IOException) {
                throw IOException(activity.application.resources.getString(R.string.imageForPartNotFound))
            }
        }
    }

    @Throws(IOException::class)
    private fun downloadImageWithURL(url: String): ByteArray? {

        try {
            return downloadFromUrl(url)?.readBytes()
        } catch (e: IOException) {
            throw IOException(activity.application.resources.getString(R.string.imageForPartNotFound))
        }
    }

}