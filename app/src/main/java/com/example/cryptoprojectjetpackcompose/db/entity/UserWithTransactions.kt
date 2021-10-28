/*
package com.example.cryptoprojectjetpackcompose.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithTransactions(
    @Embedded
    val userEntity: UserEntity,
    @Relation(
        parentColumn = "userTransactions",
        entityColumn = "transactionID",
        entity = TransactionEntity::class
    )
    val transactions: List<TransactionEntity>
)*/
