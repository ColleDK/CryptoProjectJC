package com.example.cryptoprojectjetpackcompose.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OwnedCryptoEntity(
    @PrimaryKey var cryptoName: String,
    var volume: Double
)
