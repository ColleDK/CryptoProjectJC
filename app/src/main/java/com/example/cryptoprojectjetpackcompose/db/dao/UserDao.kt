package com.example.cryptoprojectjetpackcompose.db.dao

import androidx.room.*
import com.example.cryptoprojectjetpackcompose.db.entity.UserEntity

@Dao
interface UserDao {

    // Get the top user in the database
    @Query("SELECT * FROM UserEntity ORDER BY userID LIMIT 1")
    suspend fun getUser(): UserEntity

    // Get all users in the database
    @Query("SELECT * FROM UserEntity")
    suspend fun getUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(vararg user: UserEntity)

    // Update the users balance without the user id given
    // Assuming the first user is the correct user
    @Transaction
    @Query("UPDATE UserEntity SET balance = :balance WHERE userID = (SELECT userID FROM UserEntity ORDER BY userID LIMIT 1)")
    suspend fun updateUserWithoutID(balance: Double)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM UserEntity WHERE userID = :id")
    suspend fun deleteUserById(id: Int)

}