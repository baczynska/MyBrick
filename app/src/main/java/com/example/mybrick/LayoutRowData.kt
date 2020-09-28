package com.example.mybrick

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.mybrick.database.DatabaseSingleton
import com.example.mybrick.database.entity.InventoryPart

class LayoutRowData( context: Context, item: InventoryPart, ) {
    var id: Int
    var name: String
    var description: String
    var imageMap: Bitmap?
    var quantityInStore: Int
    var quantityInSet: Int

    init {
        val databaseSingleton: DatabaseSingleton = DatabaseSingleton.getInstance(context)
        
        id = item.id

        name = databaseSingleton.PartsDAO().findById(item.itemID)?.let {
            it.namePL ?: it.name
        } ?: ""

        description = databaseSingleton.ColorsDAO().findByCode(item.colorId)?.let {
            "${it.namePL ?: it.name} [${item.itemID}]"
        } ?: ""

        imageMap = databaseSingleton.CodesDAO().findByItemIdAndColorId(item.itemID, item.colorId)?.image?.let {
                BitmapFactory.decodeByteArray(it, 0, it.size) }

        quantityInStore = item.QuantityInStore
        quantityInSet = item.quantityInSet
    }
}
