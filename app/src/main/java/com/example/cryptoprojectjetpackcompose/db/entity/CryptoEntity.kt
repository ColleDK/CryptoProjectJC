package com.example.cryptoprojectjetpackcompose.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cryptoprojectjetpackcompose.model.CryptoModel

@Entity
data class CryptoEntity(
    @PrimaryKey var name: String,
    var symbol: String,
    var priceUsd: Double,
    var changePercent24Hr: Double,
    var supply: Double) {

    fun toModel() = CryptoModel(name, symbol, priceUsd, changePercent24Hr, supply, null)

}