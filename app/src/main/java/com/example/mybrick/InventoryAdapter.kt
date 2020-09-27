package com.example.mybrick

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.mybrick.database.entity.Inventory
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class InventoryAdapter(
    private val context: Context,
    private val inventories: MutableList<Inventory>,
) : BaseAdapter() {

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val archivedColor = Color.parseColor("#A3A64A")
        val layoutInflater = LayoutInflater.from(context)
        val rowView = layoutInflater.inflate(R.layout.row_inventory, parent, false)

        val item = inventories[position]
        rowView.findViewById<TextView>(R.id.inventoryName).text = item.name
        rowView.findViewById<TextView>(R.id.inventoryCode).text = "Code: ${item.id}"

        val dateTime: LocalDateTime = LocalDateTime.ofEpochSecond(item.lastAccess.toLong(), 0, ZoneOffset.ofHours(2))
        val formatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("d-MM-yyyy HH:mm")
        TimeZone.getDefault().displayName
        rowView.findViewById<TextView>(R.id.inventoryRowLastAccessValue).text = dateTime.format(formatter)

        if(item.active != 1){
            rowView.findViewById<TextView>(R.id.inventoryName).setTextColor(archivedColor)
            rowView.findViewById<TextView>(R.id.inventoryCode).setTextColor(archivedColor)
            rowView.findViewById<TextView>(R.id.inventoryRowLastAccessLabel).setTextColor(archivedColor)
            rowView.findViewById<TextView>(R.id.inventoryRowLastAccessValue).setTextColor(archivedColor)
        }

        return rowView
    }

    override fun getItem(position: Int): Any {
        return inventories[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return inventories.size
    }
}