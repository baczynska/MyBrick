package com.example.mybrick

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextClock
import android.widget.TextView

class ItemAdapter(context: Context) : BaseAdapter( ){

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


}