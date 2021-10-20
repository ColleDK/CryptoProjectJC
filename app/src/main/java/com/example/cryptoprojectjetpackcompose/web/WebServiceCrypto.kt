package com.example.cryptoprojectjetpackcompose.web

import com.example.cryptoprojectjetpackcompose.web.wrapper.WrapperCrypto
import com.example.cryptoprojectjetpackcompose.web.wrapper.WrapperCryptos
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface WebServiceCrypto {

    @Headers(
        "Accept: application/json",
        "Content-type: application/json",
        "Authorization:Bearer 7886b97c-0e4e-4e2c-9870-4b78ddaab437")
    @GET("assets/{id}")
    suspend fun getCrypto(@Path("id")id: String): WrapperCrypto

    @Headers( "Accept: application/json",
        "Content-type: application/json",
        "Authorization:Bearer 7886b97c-0e4e-4e2c-9870-4b78ddaab437")
    @GET("assets/")
    suspend fun getCryptos(): WrapperCryptos


}