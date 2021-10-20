package com.example.cryptoprojectjetpackcompose.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cryptoprojectjetpackcompose.db.dao.CryptoDao
import com.example.cryptoprojectjetpackcompose.db.dao.TransactionDao
import com.example.cryptoprojectjetpackcompose.db.dao.UserDao
import com.example.cryptoprojectjetpackcompose.db.entity.CryptoEntity
import com.example.cryptoprojectjetpackcompose.db.entity.TransactionEntity
import com.example.cryptoprojectjetpackcompose.db.entity.UserEntity

@Database(entities = [CryptoEntity::class, TransactionEntity::class, UserEntity::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class DBRoom: RoomDatabase() {
    abstract fun cryptoDao(): CryptoDao
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao


    companion object{
        fun build(context: Context): DBRoom{
            return Room.databaseBuilder(context, DBRoom::class.java, "cryptoDB")
                .build()
        }
    }



}