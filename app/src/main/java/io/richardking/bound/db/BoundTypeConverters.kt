package io.richardking.bound.db

import android.arch.persistence.room.TypeConverter
import android.util.Log

/**
* Bound database type converters
* */
object BoundTypeConverters {
    @TypeConverter
    @JvmStatic
    fun stringToStringList(data: String?): List<String>? {
        return data?.let {
            it.split(",").map {
                try {
                    it
                } catch (ex: NumberFormatException) {
                    Log.e("TypeConverter",ex.toString())
                    null
                }
            }
        }?.filterNotNull()
    }

    @TypeConverter
    @JvmStatic
    fun stringListToString(strings: List<String>?): String? {
        return strings?.joinToString(",")
    }


}