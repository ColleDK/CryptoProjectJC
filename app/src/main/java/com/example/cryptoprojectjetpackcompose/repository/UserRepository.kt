package com.example.cryptoprojectjetpackcompose.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.cryptoprojectjetpackcompose.db.DBRoom
import com.example.cryptoprojectjetpackcompose.db.entity.TransactionEntity
import com.example.cryptoprojectjetpackcompose.db.entity.UserEntity
import com.example.cryptoprojectjetpackcompose.model.OwnedCryptoModel
import com.example.cryptoprojectjetpackcompose.model.TransactionModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import java.util.*

class UserRepository(
    private val dbRoom: DBRoom,
    private val prefs: SharedPreferences,
    private val transactionRepository: TransactionRepository,
    private val ownedCryptoRepository: OwnedCryptoRepository
) {

    suspend fun getUser(): UserModel {
        // Check if the user has been created before
        // Might have to be rewritten to something better
        Log.e("UserInstantiated", prefs.getBoolean("instantiated", false).toString())
        if (!prefs.getBoolean("instantiated", false)){
            // return a new user
            return createUser()
        }

        // If the user has been instantiated before we get it from the database
        // Assuming there is only 1 user
        val userModel = dbRoom.userDao().getUser().toModel()
        Log.e("CurrentUser", userModel.toString())

        // Retrieve the transactions and owned cryptos and insert into the model
        val transactionSet = transactionRepository.getTransactions()
        val ownedCryptoSet = ownedCryptoRepository.getOwnedCryptos().toMutableSet()

        userModel.currentCryptos = ownedCryptoSet
        userModel.transactions = transactionSet

        return userModel
    }

    suspend fun createUser(): UserModel{
        // Set the user balance to 10000 and add the initial transaction
        val user = UserEntity(balance = 10000.0)
        val transaction = TransactionModel(cryptoName = "", cryptoSymbol = "", volume = 0.0, price = 10000.0, timestamp = Date(), state = TransactionEntity.Companion.TransactionState.INSTALLATION)

        // Insert the user and transaction into the database
        dbRoom.userDao().insertUser(user)
        transactionRepository.addTransaction(transaction)

        // Add the instantiated into the preference manager
        prefs.edit().putBoolean("instantiated", true).apply()
        return user.toModel()
    }

    suspend fun updateUser(newUser: UserModel) {
        // update the balance of a user
        dbRoom.userDao().updateUserWithoutID(newUser.balance)
    }


}