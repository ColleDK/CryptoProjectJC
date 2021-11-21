package com.example.cryptoprojectjetpackcompose.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cryptoprojectjetpackcompose.model.OwnedCryptoModel

@Entity
data class OwnedCryptoEntity(
    @PrimaryKey var cryptoName: String,
    var cryptoSymbol: String,
    var volume: Double
) {
    fun toModel() = OwnedCryptoModel(cryptoName, cryptoSymbol, volume)
}
