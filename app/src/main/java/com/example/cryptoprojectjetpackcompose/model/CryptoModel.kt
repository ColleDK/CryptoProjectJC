package com.example.cryptoprojectjetpackcompose.model

import android.graphics.Bitmap
import com.example.cryptoprojectjetpackcompose.db.entity.CryptoEntity
import java.io.Serializable

data class CryptoModel(val name: String,
                       val symbol: String,
                       var priceUsd: Double,
                       var changePercent24Hr: Double,
                       var supply: Double,
                       @Transient var picture: Bitmap? = null): Serializable{

    fun toEntity() = CryptoEntity(name, symbol, priceUsd, changePercent24Hr, supply)
}
