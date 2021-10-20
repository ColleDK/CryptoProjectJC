package com.example.cryptoprojectjetpackcompose.repository

import com.example.cryptoprojectjetpackcompose.db.DBRoom
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.web.WebServiceCrypto
import com.example.cryptoprojectjetpackcompose.web.WebServiceCryptoPic

class CryptoRepository(
    private val dbRoom: DBRoom,
    private val retrofitClient: WebServiceCrypto,
    private val retrofitClientPic: WebServiceCryptoPic
) {

    suspend fun getCrypto(id: String): CryptoModel{
        return dbRoom.cryptoDao().getCrypto(id).toModel()
    }

    suspend fun loadCrypto(id: String){
        val model = retrofitClient.getCrypto(id).data.toModel()
        val dbModel = model.toEntity()
        dbRoom.cryptoDao().insertCrypto(dbModel)
    }

    suspend fun getCryptos(): MutableList<CryptoModel>{
        val list = retrofitClient.getCryptos().data
        val result = mutableListOf<CryptoModel>()

        for (dto in list){
            val model = dto.toModel()
            val dbModel = model.toEntity()
            dbRoom.cryptoDao().insertCrypto(dbModel)
            result.add(model)
        }
        return result
    }


}