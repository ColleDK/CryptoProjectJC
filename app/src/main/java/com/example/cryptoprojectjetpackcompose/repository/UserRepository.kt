package com.example.cryptoprojectjetpackcompose.repository

import android.content.SharedPreferences
import android.util.Log
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
        Log.e("UserInstantiated", prefs.getBoolean("instantiated", false).toString())
        println(prefs.getBoolean("instantiated", false))
        if (!prefs.getBoolean("instantiated", false)){
            return createUser()
        }

        // load user from database
        val user = dbRoom.userDao().getUserWithCrypto()
        Log.e("CurrentUser", user.toString())
        val result = user[0].userEntity.toModel()
        for (crypt in user[0].currentCryptos){
            Log.e("UserCryptos", crypt.toModel().toString())
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

    suspend fun updateUser(newUser: UserModel) {
        val userEntity = newUser.toEntity()
        dbRoom.userDao().updateUser(userEntity.balance, userEntity.ownedCryptoName.joinToString(separator = ","))
    }


}