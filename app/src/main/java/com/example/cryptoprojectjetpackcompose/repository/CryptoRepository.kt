package com.example.cryptoprojectjetpackcompose.repository

import android.util.Log
import com.example.cryptoprojectjetpackcompose.db.DBRoom
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.web.WebServiceCrypto
import com.example.cryptoprojectjetpackcompose.web.WebServiceCryptoPic
import com.example.cryptoprojectjetpackcompose.web.dto.CryptoPriceDto
import retrofit2.HttpException
import java.io.IOException

class CryptoRepository(
    private val dbRoom: DBRoom,
    private val retrofitClient: WebServiceCrypto,
) {

    // get a single crypto from the database
    suspend fun getCrypto(name: String): CryptoModel{
        return dbRoom.cryptoDao().getCrypto(name).toModel()
    }

    // get a single crypto from the api and insert into the database
    suspend fun loadCrypto(id: String){
        val model = retrofitClient.getCrypto(id).data.toModel()
        val dbModel = model.toEntity()
        dbRoom.cryptoDao().insertCrypto(dbModel)
    }

    // get all cryptos from the api and insert into the database'
    // TODO hent fra DB først
    suspend fun getCryptos(): MutableList<CryptoModel>{
        try{
            // receive the cryptos from the API
            val list = retrofitClient.getCryptos().data
            val result = mutableListOf<CryptoModel>()

            // Make all DTO into entities for DB and models for view
            for (dto in list){
                val model = dto.toModel()
                val dbModel = model.toEntity()
                dbRoom.cryptoDao().insertCrypto(dbModel)
                result.add(model)
            }
            return result
        } catch (e: HttpException){
            Log.d("CryptoError", "HttpException: Http request didn't respond with 200 (OK)!!")
            Log.d("CryptoError", e.message!!)
        } catch (e: IOException){
            Log.d("CryptoError", "IOException: Network Error!!")
            Log.d("CryptoError", e.message!!)
        }
        return getCryptosFromDB()
    }

    // Get the yearly price points for a single crypto
    suspend fun getCryptoPrices(id: String): List<CryptoPriceDto> {
        try{
            Log.d("CryptoPrices", "getCryptoPrices: Getting crypto prices with id=$id")
            return retrofitClient.getCryptoPrices(id).data
        } catch (e: HttpException){
            Log.d("CryptoError", "HttpException: Http request didn't respond with 200 (OK)!!")
            Log.d("CryptoError", e.message!!)
        } catch (e: IOException){
            Log.d("CryptoError", "IOException: Network Error!!")
            Log.d("CryptoError", e.message!!)
        }
        return listOf()
    }

    suspend fun getCryptosFromDB() : MutableList<CryptoModel>{
        val cryptos = dbRoom.cryptoDao().getCryptos()
        val result = mutableListOf<CryptoModel>()
        cryptos.forEach { result.add(it.toModel()) }
        return result
    }

}