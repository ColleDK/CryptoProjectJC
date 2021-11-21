package com.example.cryptoprojectjetpackcompose.repository

import com.example.cryptoprojectjetpackcompose.db.DBRoom
import com.example.cryptoprojectjetpackcompose.db.entity.CryptoEntity
import com.example.cryptoprojectjetpackcompose.db.entity.OwnedCryptoEntity
import com.example.cryptoprojectjetpackcompose.model.OwnedCryptoModel

class OwnedCryptoRepository(
    private val dbRoom: DBRoom
    ) {

    // Get all the owned cryptos from the database
    suspend fun getOwnedCryptos(): MutableList<OwnedCryptoModel>{
        val data = dbRoom.ownedCryptoDao().getOwnedCryptos()
        val result = mutableListOf<OwnedCryptoModel>()

        data.forEach { result.add(it.toModel()) }

        return result
    }

    // Get a single owned crypto based on the name
    suspend fun getOwnedCrypto(name: String): OwnedCryptoModel{
        return dbRoom.ownedCryptoDao().getOwnedCrypto(name).toModel()
    }

    // Add a new ownedcrypto to the database
    suspend fun addOwnedCrypto(name: String, symbol: String, volume: Double){
        val entity = OwnedCryptoEntity(cryptoName = name, cryptoSymbol = symbol, volume = volume)
        dbRoom.ownedCryptoDao().insertOwnedCrypto(entity)
    }

    suspend fun addOwnedCrypto(ownedCryptoModel: OwnedCryptoModel){
        dbRoom.ownedCryptoDao().insertOwnedCrypto(ownedCryptoModel.toEntity())
    }

    suspend fun deleteOwnedCrypto(name: String){
        dbRoom.ownedCryptoDao().deleteOwnedCrypto(name)
    }

    // Update a owned crypto in the database. If volume is 0.0 then delete it from the database
    suspend fun updateOwnedCrypto(name: String, volume: Double){
        if (volume > 0.0) dbRoom.ownedCryptoDao().updateOwnedCrypto(cryptoName = name, volume = volume)
        else deleteOwnedCrypto(name)
    }

}