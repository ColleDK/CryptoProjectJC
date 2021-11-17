package com.example.cryptoprojectjetpackcompose.viewmodel

import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.db.entity.TransactionEntity
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.OwnedCryptoModel
import com.example.cryptoprojectjetpackcompose.model.TransactionModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import kotlinx.coroutines.launch
import java.util.*

/**
 * Class that will hold the information about the user and passed on by service locator
 */
class UserViewModel : ViewModel(){
    private val _user = mutableStateOf(UserModel(10000.0, mutableSetOf(), mutableListOf()))
    val user = _user

    fun getUser(){
        viewModelScope.launch {
            _user.value = ServiceLocator.getUserRepository().getUser()
        }
    }

    fun buyCrypto(crypto: CryptoModel, price: Double){
        // Make sure the user has enough money
        // TODO throw exception or create message the view can use
        if (price > _user.value.balance) return
        viewModelScope.launch {
            // Get the volume of the crypto to be bought
            val volume = price / crypto.priceUsd

            // Create a transaction
            val newTransaction = TransactionModel(cryptoName = crypto.name, volume = volume, price = price, timestamp = Date(), state = TransactionEntity.Companion.TransactionState.BOUGHT)
            // Insert the transaction to the database
            ServiceLocator.getDBRoom().transactionDao().insertTransaction(newTransaction.toEntity())

            // Check if the user already owns this crypto
            val userOwnCrypto = _user.value.currentCryptos.find { it.cryptoName == crypto.name }
            if (userOwnCrypto == null){
                // Create new owned crypto
                val newOwnedCrypto = OwnedCryptoModel(cryptoName = crypto.name, volume = volume)
                // Add the new crypto to the user
                _user.value.currentCryptos.plus(newOwnedCrypto)
                // Insert the new crypto into the database
                ServiceLocator.getDBRoom().ownedCryptoDao().insertOwnedCrypto(newOwnedCrypto.toEntity())
            } else {
                // Update the volume of the owned crypto
                _user.value.currentCryptos.find { it.cryptoName == crypto.name }!!.volume += volume
                ServiceLocator.getDBRoom().ownedCryptoDao().updateOwnedCrypto(cryptoName = crypto.name, volume = _user.value.currentCryptos.find { it.cryptoName == crypto.name }!!.volume)
            }

            // Update the user
            _user.value.balance -= price
            ServiceLocator.getDBRoom().userDao().updateUserWithoutID(_user.value.balance)

        }
    }

    // TODO Selling a crypto
    fun sellCrypto(crypto: CryptoModel, amount: Double){

    }



}