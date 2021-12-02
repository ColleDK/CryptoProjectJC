package com.example.cryptoprojectjetpackcompose.web

import com.example.cryptoprojectjetpackcompose.ServiceLocator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

interface WebServiceCryptoPic {

    @GET("{id}@2x.png")
    fun getCryptoPic(@Path("id")id: String,@Header("Content-type") contentType: String = "image/png; charset=binary", @Header("Authorization") authorization: String = ServiceLocator.getAPIKey()): Call<ResponseBody>


}