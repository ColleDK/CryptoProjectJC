package com.example.cryptoprojectjetpackcompose.model

import com.example.cryptoprojectjetpackcompose.db.entity.UserEntity

data class UserModel(
    var balance: Double,
    var currentCryptos: MutableSet<OwnedCryptoModel>,
    var transactions: MutableList<TransactionModel>
) {
    fun toEntity(): UserEntity = UserEntity(0, balance)
}