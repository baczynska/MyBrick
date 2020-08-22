package com.example.mybrick.xml

import android.app.Activity
import android.os.AsyncTask
import android.widget.Button
import android.widget.EditText
import com.example.mybrick.R
import com.example.mybrick.database.DatabaseSingleton
import com.example.mybrick.settings
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadXmlTask(private val activity: Activity) : AsyncTask<String, Void, String>() {
    val inputId: String = activity.findViewById<EditText>(R.id.editTextNumber).editableText.toString()
    val inputName: String = activity.findViewById<EditText>(R.id.editTextName).editableText.toString()


    override fun doInBackground(vararg urls: String): String {
        if (DatabaseSingleton.getInstance(activity.application).InventoriesDAO().checkIfExistsById(inputId)) { // check is set already in database
            // TODO show error message
            this.cancel(true)
        } else if (DatabaseSingleton.getInstance(activity.application).InventoriesDAO().checkIfExistsByName(inputName)) { // check is set already in database
            // TODO show error message
            this.cancel(true)
        }
        return try {
            loadXmlFromNetwork(settings.url + inputId + ".xml")
        } catch (e: IOException) {
            activity.application.resources.getString(R.string.connectionError)
        } catch (e: XmlPullParserException) {
            activity.application.resources.getString(R.string.xmlError)
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result == activity.application.resources.getString(R.string.xmlSuccess)) {
            // TODO show view with decision what to do next
        } else {
            // TODO show error
        }
        activity.findViewById<Button>(R.id.addButton).isEnabled = true
    }

    override fun onCancelled() {
        super.onCancelled()
        activity.findViewById<Button>(R.id.addButton).isEnabled = true
    }

    @Throws(XmlPullParserException::class, IOException::class)
    fun loadXmlFromNetwork(urlString: String) : String {
        val entries: List<Entry>? = downloadFromUrl(urlString)?.use(XMLParser(activity.application)::parse)
        // TODO add set to database
        return activity.application.resources.getString(R.string.xmlSuccess)
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
}