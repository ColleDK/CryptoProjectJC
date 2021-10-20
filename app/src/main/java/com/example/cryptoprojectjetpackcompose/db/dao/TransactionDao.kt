package com.example.cryptoprojectjetpackcompose.db.dao

import androidx.room.*
import com.example.cryptoprojectjetpackcompose.db.entity.TransactionEntity

@Dao
interface TransactionDao{

    @Query("SELECT * FROM TransactionEntity")
    suspend fun getTransactions(): List<TransactionEntity>

    @Query("SELECT * FROM TransactionEntity WHERE transactionID = :id")
    suspend fun getTransaction(id: Int): TransactionEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(vararg transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)


}