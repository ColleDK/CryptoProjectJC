package com.example.cryptoprojectjetpackcompose.repository

import android.content.SharedPreferences
import com.example.cryptoprojectjetpackcompose.db.DBRoom
import com.example.cryptoprojectjetpackcompose.db.entity.TransactionEntity
import com.example.cryptoprojectjetpackcompose.db.entity.UserEntity
import com.example.cryptoprojectjetpackcompose.model.UserModel
import java.util.*

class UserRepository(
    private val dbRoom: DBRoom,
    private val prefs: SharedPreferences
) {

    suspend fun getUser(): UserModel {
        // if new user
        if (!prefs.getBoolean("instantiated", false)){
            return createUser()
        }

        // load user from database
        val user = dbRoom.userDao().getUserWithCrypto()
        val result = user[0].userEntity.toModel()
        for (crypt in user[0].currentCryptos){
            result.currentCryptos.add(crypt.toModel())
        }

        return result
    }

    suspend fun createUser(): UserModel{
        val user = UserEntity(balance = 10000.0, ownedCryptoName = listOf())
        val transaction = TransactionEntity(cryptoSymbol = "", volume = 0.0, price = 10000.0, timestamp = Date(), state = TransactionEntity.Companion.TransactionState.INSTALLATION)

        dbRoom.userDao().insertUser(user)
        dbRoom.transactionDao().insertTransaction(transaction)

        prefs.edit().putBoolean("instantiated", true).apply()
        return user.toModel()
    }
}