package com.example.cryptoprojectjetpackcompose

import android.content.Context
import android.preference.PreferenceManager
import com.example.cryptoprojectjetpackcompose.db.DBRoom
import com.example.cryptoprojectjetpackcompose.repository.CryptoRepository
import com.example.cryptoprojectjetpackcompose.repository.UserRepository
import com.example.cryptoprojectjetpackcompose.web.WebServiceCrypto
import com.example.cryptoprojectjetpackcompose.web.WebServiceCryptoPic
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {
    private lateinit var applicationContext: Context

    fun init(applicationContext: Context){
        this.applicationContext = applicationContext
    }

    private val dbRoom by lazy {
        DBRoom.build(applicationContext)
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.coincap.io/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebServiceCrypto::class.java)

    }

    private val retrofitPic by lazy {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        Retrofit.Builder()
            .baseUrl("https://static.coincap.io/assets/icons/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(WebServiceCryptoPic::class.java)
    }

    private val cryptoRepo by lazy {
        CryptoRepository(dbRoom,
                        retrofit)
    }

    private val userRepo by lazy {
        UserRepository(
            dbRoom,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
    }


    fun getDBRoom() = this.dbRoom
    fun getRetrofitClient() = this.retrofit
    fun getRetrofitClientPic() = this.retrofitPic
    fun getCryptoRepository() = this.cryptoRepo
    fun getUserRepository() = this.userRepo

}