package com.example.cryptoprojectjetpackcompose.repository

import android.util.Log
import com.example.cryptoprojectjetpackcompose.db.DBRoom
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.web.WebServiceCrypto
import com.example.cryptoprojectjetpackcompose.web.WebServiceCryptoPic
import com.example.cryptoprojectjetpackcompose.web.dto.CryptoPriceDto

class CryptoRepository(
    private val dbRoom: DBRoom,
    private val retrofitClient: WebServiceCrypto,
) {

    // get a single crypto from the database
    suspend fun getCrypto(id: String): CryptoModel{
        return dbRoom.cryptoDao().getCrypto(id).toModel()
    }

    // get a single crypto from the api and insert into the database
    suspend fun loadCrypto(id: String){
        val model = retrofitClient.getCrypto(id).data.toModel()
        val dbModel = model.toEntity()
        dbRoom.cryptoDao().insertCrypto(dbModel)
    }

    // get all cryptos from the api and insert into the database
    suspend fun getCryptos(): MutableList<CryptoModel>{
        try{
            val list = retrofitClient.getCryptos().data
            val result = mutableListOf<CryptoModel>()

            for (dto in list){
                val model = dto.toModel()
                val dbModel = model.toEntity()
                dbRoom.cryptoDao().insertCrypto(dbModel)
                result.add(model)
            }
            return result
        } catch (e: Exception){
            Log.d("CryptoError", e.message!!)
        }
        return mutableListOf()
    }

    // Get the yearly price points for a single crypto
    suspend fun getCryptoPrices(id: String): List<CryptoPriceDto> {
        try{
            return retrofitClient.getCryptoPrices(id).data
        } catch (e: Exception){
            Log.d("CryptoError", e.message!!)
        }
        return listOf()
    }

}