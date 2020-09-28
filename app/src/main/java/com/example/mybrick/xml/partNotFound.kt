package com.example.mybrick.xml

import java.lang.Exception

class partNotFound(message: String): Exception(message) {
    companion object {
        fun createMessage(itemId: Int?, colorId: Int?): String {
            return "Item not found. ( itemID = $itemId, colorID = $colorId )"
        }
    }
}