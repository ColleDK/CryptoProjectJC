package com.example.cryptoprojectjetpackcompose.model

import com.example.cryptoprojectjetpackcompose.db.entity.TransactionEntity
import java.util.*

data class TransactionModel(
    val cryptoName: String,
    var cryptoSymbol: String,
    var volume: Double,
    var price: Double,
    var timestamp: Date,
    var state: TransactionEntity.Companion.TransactionState
){
    fun toEntity() = TransactionEntity(0, cryptoName, cryptoSymbol, volume, price, timestamp, state)
}
