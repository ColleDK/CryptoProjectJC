package com.example.cryptoprojectjetpackcompose.model

import com.example.cryptoprojectjetpackcompose.db.entity.OwnedCryptoEntity

data class OwnedCryptoModel(var cryptoName: String,
                            var cryptoSymbol: String,
                            var volume: Double) {
    fun toEntity() = OwnedCryptoEntity(cryptoName, cryptoSymbol, volume)
}