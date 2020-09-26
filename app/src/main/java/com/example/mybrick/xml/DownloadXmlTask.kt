package com.example.mybrick.xml

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.widget.Button
import android.widget.EditText
import com.example.mybrick.AboutProjectActivity
import com.example.mybrick.R
import com.example.mybrick.database.DatabaseSingleton
import com.example.mybrick.database.entity.Code
import com.example.mybrick.database.entity.Inventory
import com.example.mybrick.database.entity.InventoryPart
import com.example.mybrick.settings
import com.example.mybrick.xml.exceptions.PartNotFound
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.lang.NullPointerException
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant

class DownloadXmlTask(private val activity: Activity) : AsyncTask<String, Void, String>() {
    val inputId: String = activity.findViewById<EditText>(R.id.editTextNumber).editableText.toString()
    val inputName: String = activity.findViewById<EditText>(R.id.editTextName).editableText.toString()
    var failure = false


    override fun doInBackground(vararg urls: String): String {

       deleteAllInventories()

        val sharedPref = activity.getSharedPreferences("Preferences",Context.MODE_PRIVATE)
        val sourceUrl = settings.url


        if (DatabaseSingleton.getInstance(activity.application).InventoriesDAO().checkIfExistsById(
                inputId.toInt()
            )) { // check if set already exist in database
            failure = true
            return "Error DXT_01 - this project already exist in database"
        } else if (DatabaseSingleton.getInstance(activity.application).InventoriesDAO().checkIfExistsByName(inputName)) {
            // check if setName already exist in database
            failure = true
            return "Error DXT_02 - this projeckt's name already exist in database"
        }
        return try {
            loadXmlFromNetwork("$sourceUrl$inputId.xml", inputId, inputName)
        } catch (e: IOException) {
            failure = true
            activity.application.resources.getString(R.string.connectionError)
        } catch (e: XmlPullParserException) {
            failure = true
            activity.application.resources.getString(R.string.xmlError)
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {

                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    val intent = Intent(activity, AboutProjectActivity::class.java)
                    val toSend = if (inputName.isEmpty()) inputId else inputName
                    intent.putExtra("name", toSend)

                    activity.startActivity(intent)
                }
            }
        }

        if (failure) {
            val builder = AlertDialog.Builder(activity)
            if (result != null) {
                builder.setMessage(result).setPositiveButton("OK", dialogClickListener ).show()
            }
        } else {
            val builder = AlertDialog.Builder(activity)
            if (result != null) {
                builder.setMessage(result).setPositiveButton("Stay here", dialogClickListener ).setNegativeButton("Go to project", dialogClickListener).show()
            }
        }
        activity.findViewById<Button>(R.id.addButton).isEnabled = true
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun loadXmlFromNetwork(urlString: String, inventoryId: String, inventoryName: String?) : String {
        val parts: HashMap<String, List<*>>? = downloadFromUrl(urlString)?.use(XMLParser(activity.application)::parse)
        if (parts != null) {
            DatabaseSingleton.getInstance(activity.applicationContext).InventoriesDAO().insert(
                Inventory(
                    inventoryId.toInt(),
                    inventoryName ?: inventoryId,
                    lastAccess = Instant.now().epochSecond.toInt()
                )
            )
            val notFoundParts = ArrayList<String>(parts["itemsNotFound"] as List<String>)
            (parts.get("items") as List<Entry>).forEach {
                try {
                    val newInventoryPart: InventoryPart = it.castToInventoryPart(inventoryId.toInt())
                    saveImage(newInventoryPart, activity.applicationContext)
                    DatabaseSingleton.getInstance(activity.applicationContext).InventoriesPartsDAO().insertPart(newInventoryPart)
                } catch (e: NullPointerException) {
                    notFoundParts.add(PartNotFound.createMessage(it.itemID, it.colorID))
                }
            }
            return aboutNotFoundParts(activity.application.resources.getString(R.string.xmlSuccess), notFoundParts)
        }
        return activity.application.resources.getString(R.string.xmlError)
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    @Throws(IOException::class)
    private fun downloadFromUrl(urlString: String): InputStream? {
        val url = URL(urlString)
        return (url.openConnection() as? HttpURLConnection)?.run {
            readTimeout = 10000
            connectTimeout = 15000
            requestMethod = "GET"
            doInput = true
            // Starts the query
            connect()
            inputStream
        }
    }

    private fun saveImage(part: InventoryPart, context: Context) {
        // code jest rekordem w tabeli Code gdzie trzymamy obrazki
        val databaseSingleton = DatabaseSingleton.getInstance(context)
        val partCode = databaseSingleton.PartsDAO().findCodeById(part.itemID)
        val colorCode = databaseSingleton.ColorsDAO().findCodeById(part.colorId)
        databaseSingleton.CodesDAO().findByItemIdAndColorId(
            part.itemID,
            part.colorId
        )?.let { code: Code ->
            if (partCode != null) {
                val imageURL =
                    if (colorCode != null) "https://www.bricklink.com/P/$colorCode/$partCode.jpg"
                    else "https://www.bricklink.com/PL/$partCode.jpg"
                if (code.image == null) {
                    try {
                        code.image = downloadImage(imageURL)
                        DatabaseSingleton.getInstance(context).CodesDAO().updateCode(code)
                    } catch (e: IOException) {
                        //image not found
                    }
                }
            }
        } ?: run {
            if (partCode != null) {
                val imageURL =
                    if (colorCode != null) "https://www.bricklink.com/P/$colorCode/$partCode.jpg"
                    else "https://www.bricklink.com/PL/$partCode.jpg"
                var image: ByteArray? = null
                try {
                    image = downloadImage(imageURL)
                } catch (e: IOException) {
                    //image not found
                }
                DatabaseSingleton.getInstance(context).CodesDAO()
                    .insertNewCode(Code(0, part.itemID, part.colorId, null, image))
            }
        }
    }

    @Throws(IOException::class)
    private fun downloadImage(url: String) : ByteArray {
        downloadFromUrl(url)?.use {
            return it.readBytes()
        }
        throw IOException("Image not found")
    }

    private fun aboutNotFoundParts(message: String, notFoundParts: List<String>): String {
        message.plus("\n")
        notFoundParts.forEach { message.plus("\n" + it) }
        return message
    }

    private fun deleteAllInventories() {
        DatabaseSingleton.getInstance(activity).InventoriesPartsDAO().deleteAll()
        DatabaseSingleton.getInstance(activity).InventoriesDAO().deleteAll()
    }

    private fun concatMessageWithInfoAboutNotFoundParts(
        message: String,
        notFoundParts: List<String>,
    ): String {
        message.plus("\n")
        notFoundParts.forEach { message.plus("\n" + it) }
        return message
    }
}