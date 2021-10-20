package com.example.cryptoprojectjetpackcompose.model

import java.util.*

data class TransactionModel(
    val crypto: CryptoModel,
    var volume: Double,
    var price: Double,
    var timestamp: Date
)
