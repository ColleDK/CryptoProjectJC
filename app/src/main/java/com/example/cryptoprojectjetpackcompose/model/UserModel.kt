package com.example.cryptoprojectjetpackcompose.model

data class UserModel(
    var balance: Double,
    var currentCryptos: MutableList<CryptoModel>,
    var transactions: MutableList<TransactionModel>
) {
}