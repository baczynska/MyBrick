package com.example.mybrick

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import androidx.core.view.get
import kotlinx.android.synthetic.main.row_item.*

class ItemAdapter(context: Context) : BaseAdapter( ){

    var integer_number : TextView? = null

    private val mContext: Context

    init {
        this.mContext = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(mContext)
        val rowMain = layoutInflater.inflate(R.layout.row_item, parent, false)

        val textView_down = rowMain.findViewById<TextView>(R.id.textView_down)
        val textView_top = rowMain.findViewById<TextView>(R.id.textView_top)

        val myList = (mContext as AboutProjectActivity).myList
        val myItem = myList[position]

        textView_top.text = myItem.title
        textView_down.text = myItem.description

        val increase = rowMain.findViewById<Button>(R.id.increase)
        val decrease = rowMain.findViewById<Button>(R.id.decrease)

        increase.setOnClickListener {
            val listItem = it.parent as ViewGroup
            listOf<View>(listItem).forEach {
                if (it.id == R.id.integer_number) {
                    increaseInteger(listItem.get(listItem.indexOfChild(it)) as TextView)
                }
            }
        }
        decrease.setOnClickListener {
            val listItem = it.parent as ViewGroup
            listOf<View>(listItem).forEach {
                if (it.id == R.id.integer_number) {
                    decreaseInteger(listItem.get(listItem.indexOfChild(it)) as TextView)
                }
            }
        }


        return rowMain
    }

    override fun getItem(position: Int): Any {
        return "TEST"
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return 4
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


}