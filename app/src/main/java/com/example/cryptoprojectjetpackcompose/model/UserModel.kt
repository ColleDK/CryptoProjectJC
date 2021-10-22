package com.example.cryptoprojectjetpackcompose.model

import com.example.cryptoprojectjetpackcompose.db.entity.UserEntity

data class UserModel(
    var balance: Double,
    var currentCryptos: MutableList<CryptoModel>,
    var transactions: MutableList<TransactionModel>
) {
    fun toEntity(id: Int): UserEntity {
        val list: List<String> = listOf(currentCryptos.forEach { it.name }.toString())
        return UserEntity(id, balance, list)
    }
}