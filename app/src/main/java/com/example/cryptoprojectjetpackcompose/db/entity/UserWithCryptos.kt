package com.example.cryptoprojectjetpackcompose.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithCryptos(
    @Embedded val userEntity: UserEntity,
    @Relation(
        parentColumn = "ownedCryptoName",
        entityColumn = "name",
        entity = CryptoEntity::class
    )
    val currentCryptos: List<CryptoEntity>
)
