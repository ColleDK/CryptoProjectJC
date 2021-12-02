package com.example.cryptoprojectjetpackcompose.web

import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.web.wrapper.WrapperCrypto
import com.example.cryptoprojectjetpackcompose.web.wrapper.WrapperCryptoPrices
import com.example.cryptoprojectjetpackcompose.web.wrapper.WrapperCryptos
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface WebServiceCrypto {
    @GET("assets/{id}")
    suspend fun getCrypto(@Path("id")id: String,
                          @Header("Accept") accept: String = "application/json",
                          @Header("Content-type") contentType: String = "application/json",
                          @Header("Authorization") authorization: String = ServiceLocator.getAPIKey()
    ): WrapperCrypto

    @GET("assets/")
    suspend fun getCryptos(@Header("Accept") accept: String = "application/json",
                           @Header("Content-type") contentType: String = "application/json",
                           @Header("Authorization") authorization: String = ServiceLocator.getAPIKey()
    ): WrapperCryptos

    @GET("assets/{id}/history?interval=d1")
    suspend fun getCryptoPrices(@Path("id")id: String,
                                @Header("Accept") accept: String = "application/json",
                                @Header("Content-type") contentType: String = "application/json",
                                @Header("Authorization") authorization: String = ServiceLocator.getAPIKey()): WrapperCryptoPrices


}