package com.example.cryptoprojectjetpackcompose.repository

import com.example.cryptoprojectjetpackcompose.db.DBRoom
import com.example.cryptoprojectjetpackcompose.model.TransactionModel

class TransactionRepository(
    private val dbRoom: DBRoom
) {
    suspend fun getTransactions(): MutableList<TransactionModel>{
        val data = dbRoom.transactionDao().getTransactions()
        val result = mutableListOf<TransactionModel>()
        data.forEach { result.add(it.toModel()) }

        return result
    }

    suspend fun addTransaction(transaction: TransactionModel){
        dbRoom.transactionDao().insertTransaction(transaction.toEntity())
    }

    suspend fun deleteTransaction(transaction: TransactionModel){
        dbRoom.transactionDao().deleteTransaction(transaction.toEntity())
    }

}