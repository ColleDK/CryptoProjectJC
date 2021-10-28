package com.example.cryptoprojectjetpackcompose.model

import com.example.cryptoprojectjetpackcompose.db.entity.UserEntity

data class UserModel(
    var balance: Double,
    var currentCryptos: MutableList<CryptoModel>,
    var transactions: MutableList<TransactionModel>
) {
    fun toEntity(): UserEntity {
        val tempListCrypto: MutableList<String> = mutableListOf()
        currentCryptos.forEach{tempListCrypto.add(it.name)}
        val cryptoList: List<String> = tempListCrypto.toList()

        return UserEntity(0, balance, cryptoList)
    }
}