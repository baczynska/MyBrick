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

    private val doneColor = Color.parseColor("#E91E63")
    private val mainColor = Color.WHITE

    private fun myInit(rowView: View, data: LayoutRowData) {

        val maxItems: TextView = rowView.findViewById(R.id.textView_maxElements)
        maxItems.text = data.quantityInSet.toString()

        val itemNumber: TextView = rowView.findViewById(R.id.integer_number)
        itemNumber.text = data.quantityInStore.toString()

        val label: TextView = rowView.findViewById(R.id.textView_top)
        label.text = data.name

        val description: TextView = rowView.findViewById(R.id.textView_down)
        description.text = data.description

        val image: ImageView = rowView.findViewById(R.id.imageView)
        image.setImageBitmap(data.imageMap)

        if( (data.quantityInSet == data.quantityInStore)){

            (maxItems.parent as ConstraintLayout).setBackgroundColor(doneColor)

        } else {

            (maxItems.parent as ConstraintLayout).setBackgroundColor(mainColor)

        }
    }


    private fun plusOrMinusOne(diff: Int, clickedButton: View, position: Int) {

        val itemList = clickedButton.parent as LinearLayout

        for (i: Int in 0 until itemList.childCount) {

            val child: View = itemList[i]

            if (child.id == R.id.integer_number) {

                val numberOfItems = items[position].quantityInStore
                val maxItems = items[position].quantityInSet
                if( (numberOfItems > 0) and (numberOfItems < maxItems)) {

                    val numberOfItemsNow = numberOfItems + diff
                    items[position].quantityInStore = numberOfItemsNow
                    (child as TextView).text = numberOfItemsNow.toString()

                    if ( numberOfItemsNow == maxItems)
                        (itemList.parent as ConstraintLayout).setBackgroundColor(doneColor)
                    else
                        (itemList.parent as ConstraintLayout).setBackgroundColor(mainColor)
                }
            }
        }
    }


    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(context)
        val thisRow = layoutInflater.inflate(R.layout.row_item, parent, false)
        val item: LayoutRowData = items[position]

        myInit(thisRow, item)

        val increase = thisRow.findViewById<Button>(R.id.increase)
        val decrease = thisRow.findViewById<Button>(R.id.decrease)

        increase.setOnClickListener {
            plusOrMinusOne(1, it, position)
        }
        decrease.setOnClickListener {
            plusOrMinusOne(-1, it, position)
        }

        return thisRow
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





}