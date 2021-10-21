package com.example.cryptoprojectjetpackcompose.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cryptoprojectjetpackcompose.model.TransactionModel
import java.util.*

@Entity
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) var transactionID: Int = 0,
    var cryptoSymbol: String,
    var volume: Double,
    var price: Double,
    var timestamp: Date,
    var state: TransactionState
) {

    companion object{
        enum class TransactionState(val value: Int){
            BOUGHT(0), SOLD(1), INSTALLATION(2), UNKNOWN(3)
        }
    }

    fun toModel() = TransactionModel(cryptoSymbol, volume, price, timestamp, state)

}