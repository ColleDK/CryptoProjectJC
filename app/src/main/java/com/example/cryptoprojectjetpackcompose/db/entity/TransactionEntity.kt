package com.example.cryptoprojectjetpackcompose.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cryptoprojectjetpackcompose.model.TransactionModel
import java.util.*

@Entity
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) var transactionID: Int = 0,
    var cryptoName: String,
    var volume: Double,
    var price: Double,
    var timestamp: Date
) {

    //fun toModel() = TransactionModel(cryptoName, volume, price, timestamp)

}