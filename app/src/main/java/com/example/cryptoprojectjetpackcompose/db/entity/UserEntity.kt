package com.example.cryptoprojectjetpackcompose.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cryptoprojectjetpackcompose.model.UserModel

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true) var userID: Int = 0,
    var balance: Double) {

    fun toModel() = UserModel(balance = balance, mutableSetOf(), mutableListOf())

}