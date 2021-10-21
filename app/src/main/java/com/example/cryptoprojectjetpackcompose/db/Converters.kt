package com.example.cryptoprojectjetpackcompose.db

import androidx.room.TypeConverter
import com.example.cryptoprojectjetpackcompose.db.entity.TransactionEntity
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date?{
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long?{
        return date?.time?.toLong()
    }


    @TypeConverter
    fun stringToList(value: String): List<String>{
        return value.split(",").map { it }
    }

    @TypeConverter
    fun listToString(value: List<String>): String{
        return value.joinToString(separator = ",")
    }

    @TypeConverter
    fun toState(value: String) = enumValueOf<TransactionEntity.Companion.TransactionState>(value)

    @TypeConverter
    fun fromState(value: TransactionEntity.Companion.TransactionState) = value.name
}