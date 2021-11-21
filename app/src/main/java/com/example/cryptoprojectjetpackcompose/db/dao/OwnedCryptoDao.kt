package com.example.cryptoprojectjetpackcompose.db.dao

import androidx.room.*
import com.example.cryptoprojectjetpackcompose.db.entity.CryptoEntity
import com.example.cryptoprojectjetpackcompose.db.entity.OwnedCryptoEntity

@Dao
interface OwnedCryptoDao {
    @Query("SELECT * FROM OwnedCryptoEntity")
    suspend fun getOwnedCryptos(): List<OwnedCryptoEntity>

    @Query("SELECT * FROM OwnedCryptoEntity WHERE cryptoName = :cryptoName")
    suspend fun getOwnedCrypto(cryptoName: String): OwnedCryptoEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwnedCrypto(vararg crypto: OwnedCryptoEntity)

    @Query("UPDATE OwnedCryptoEntity SET volume = :volume WHERE cryptoName = :cryptoName")
    suspend fun updateOwnedCrypto(cryptoName: String, volume: Double)

    @Query("DELETE FROM OwnedCryptoEntity WHERE cryptoName = :name")
    suspend fun deleteOwnedCrypto(name: String)
}