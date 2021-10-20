package com.example.cryptoprojectjetpackcompose.db.dao

import androidx.room.*
import com.example.cryptoprojectjetpackcompose.db.entity.CryptoEntity

@Dao
interface CryptoDao{

    @Query("SELECT * FROM CryptoEntity")
    suspend fun getCryptos(): List<CryptoEntity>

    @Query("SELECT * FROM CryptoEntity WHERE name = :name")
    suspend fun getCrypto(name: String): CryptoEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrypto(vararg crypto: CryptoEntity)

    @Delete
    suspend fun deleteCrypto(crypto: CryptoEntity)


}