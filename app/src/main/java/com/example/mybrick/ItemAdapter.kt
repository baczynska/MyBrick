package com.example.mybrick

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import com.example.mybrick.database.entity.InventoryPart

class ItemAdapter(
    private val context: Context,
    private val items: List<LayoutRowData>
) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(context)
        val rowView = layoutInflater.inflate(R.layout.row_item, parent, false)
        val item: LayoutRowData = items[position]

        fillLayout(rowView, item)

        val increase = rowView.findViewById<Button>(R.id.increase)
        val decrease = rowView.findViewById<Button>(R.id.decrease)

        increase.setOnClickListener {
            changePartsQuantity(1, it, position)
        }
        decrease.setOnClickListener {
            changePartsQuantity(-1, it, position)
        }

        return rowView
    }

    override fun getItem(position: Int): Any {
        return items[position].quantityInStore
    }

    override fun getItemId(position: Int): Long {
        return items[position].id.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

    fun increaseInteger(number: TextView) {
        display(number, number.text.toString().toInt() + 1)
    }

    fun decreaseInteger(number: TextView) {
        display(number, number.text.toString().toInt() - 1)
    }

    private fun display(number: TextView, newNumber: Int) {
        number.setText("$newNumber")
    }

    private fun fillLayout(rowView: View, data: LayoutRowData) {
        val maxElements: TextView = rowView.findViewById(R.id.textView_maxElements)
        val itemsNumberElement: TextView = rowView.findViewById(R.id.integer_number)
        val mainLabel: TextView = rowView.findViewById(R.id.textView_top)
        val descriptionLabel: TextView = rowView.findViewById(R.id.textView_down)
        val imageView: ImageView = rowView.findViewById(R.id.imageView)

        maxElements.text = data.quantityInSet.toString()
        itemsNumberElement.text = data.quantityInStore.toString()
        mainLabel.text = data.title
        descriptionLabel.text = data.description
        imageView.setImageBitmap(data.imageBitmap)
    }

    private fun changePartsQuantity(change: Int, clickedButton: View, position: Int) {
        val listItem = clickedButton.parent as LinearLayout
        for (i: Int in 0 until listItem.childCount) {
            val child: View = listItem[i]
            if (child.id == R.id.integer_number) {
                val newQuantity: Int = items[position].quantityInStore + change
                if( 0 <= newQuantity && newQuantity <= items[position].quantityInSet) {
                    items[position].quantityInStore = newQuantity
                    (child as TextView).text = "$newQuantity"
                    if ( newQuantity == items[position].quantityInSet)
                        (listItem.parent as ConstraintLayout).setBackgroundColor(Color.BLUE)
                    else
                        (listItem.parent as ConstraintLayout).setBackgroundColor(Color.WHITE)
                }
            }
        }
    }

}