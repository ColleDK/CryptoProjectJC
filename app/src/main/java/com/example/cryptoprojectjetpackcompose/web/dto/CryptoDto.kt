package com.example.cryptoprojectjetpackcompose.web.dto

import com.example.cryptoprojectjetpackcompose.model.CryptoModel

data class CryptoDto(
    val name: String,
    val symbol: String,
    var priceUsd: Double,
    var changePercent24Hr: Double,
    var supply: Double
){
    fun toModel() = CryptoModel(name, symbol, priceUsd, changePercent24Hr, supply, null)
}
