package com.example.cryptoprojectjetpackcompose.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cryptoprojectjetpackcompose.db.dao.CryptoDao
import com.example.cryptoprojectjetpackcompose.db.dao.OwnedCryptoDao
import com.example.cryptoprojectjetpackcompose.db.dao.TransactionDao
import com.example.cryptoprojectjetpackcompose.db.dao.UserDao
import com.example.cryptoprojectjetpackcompose.db.entity.CryptoEntity
import com.example.cryptoprojectjetpackcompose.db.entity.OwnedCryptoEntity
import com.example.cryptoprojectjetpackcompose.db.entity.TransactionEntity
import com.example.cryptoprojectjetpackcompose.db.entity.UserEntity

@Database(entities = [CryptoEntity::class, TransactionEntity::class, UserEntity::class, OwnedCryptoEntity::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class DBRoom: RoomDatabase() {
    abstract fun cryptoDao(): CryptoDao
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun ownedCryptoDao(): OwnedCryptoDao


    companion object{
        fun build(context: Context): DBRoom{
            return Room.databaseBuilder(context, DBRoom::class.java, "cryptoDBJC")
                //.addMigrations(MIGRATION_1_2)
                .build()
        }
    }

    /*
    object MIGRATION_1_2 : Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE TransactionEntity RENAME COLUMN 'cryptoName' TO 'cryptoSymbol'")
            database.execSQL("ALTER TABLE TransactionEntity ADD COLUMN 'state' TEXT NOT NULL DEFAULT 'UNKNOWN'")
            database.execSQL("ALTER TABLE UserEntity ADD COLUMN 'ownedCryptoName' TEXT NOT NULL DEFAULT ''")
        }
    }*/



}