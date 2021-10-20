package com.example.cryptoprojectjetpackcompose.web

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface WebServiceCryptoPic {

    @Headers(
        "Content-type: image/png; charset=binary",
        "Authorization:Bearer 7886b97c-0e4e-4e2c-9870-4b78ddaab437")
    @GET("{id}@2x.png")
    fun getCryptoPic(@Path("id")id: String): Call<ResponseBody>


}