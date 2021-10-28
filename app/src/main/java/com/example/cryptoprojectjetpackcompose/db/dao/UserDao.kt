package com.example.cryptoprojectjetpackcompose.db.dao

import androidx.room.*
import com.example.cryptoprojectjetpackcompose.db.Converters
import com.example.cryptoprojectjetpackcompose.db.entity.UserEntity
import com.example.cryptoprojectjetpackcompose.db.entity.UserWithCryptos

@Dao
interface UserDao {

    @Query("SELECT * FROM UserEntity")
    suspend fun getUsers(): List<UserEntity>

    @Transaction
    @Query("SELECT * FROM UserEntity")
    suspend fun getUserWithCrypto(): List<UserWithCryptos>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(vararg user: UserEntity)

    /*
    @Update
    suspend fun updateUser(user: UserEntity)
*/

    @Transaction
    @Query("UPDATE UserEntity SET balance = :userBalance, ownedCryptoName = :userCryptos WHERE userID = (SELECT userID FROM UserEntity LIMIT 1)")
    suspend fun updateUser(userBalance: Double, userCryptos: String)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM UserEntity WHERE userID = :id")
    suspend fun deleteUserById(id: Int)

}