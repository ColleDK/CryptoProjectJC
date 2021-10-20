package com.example.cryptoprojectjetpackcompose.db.dao

import androidx.room.*
import com.example.cryptoprojectjetpackcompose.db.entity.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM UserEntity")
    suspend fun getUsers(): List<UserEntity>

    @Query("SELECT * FROM UserEntity WHERE userID = :id")
    suspend fun getUser(id: Int): UserEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(vararg user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)


}