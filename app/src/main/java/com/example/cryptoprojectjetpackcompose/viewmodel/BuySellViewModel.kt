package com.example.cryptoprojectjetpackcompose.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.madrapps.plot.line.DataPoint
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class BuySellViewModel: ViewModel() {
    private val _user = mutableStateOf(UserModel(10000.0, mutableSetOf(), mutableListOf()))
    val user = _user

    fun getUser(){
        viewModelScope.launch {
            _user.value = ServiceLocator.getUserRepository().getUser()
        }
    }

    private val _crypto = mutableStateOf(CryptoModel("","",0.0,0.0,0.0,null))
    val crypto = _crypto

    private val _cryptoPrices = mutableStateOf(mutableListOf<DataPoint>())
    val cryptoPrices = _cryptoPrices

    fun getCrypto(name: String){
        viewModelScope.launch {
            _crypto.value = ServiceLocator.getCryptoRepository().getCrypto(name)
            getCryptoPic(_crypto.value)
            getCryptoPrices(_crypto.value.name)
        }
    }

    fun getCryptoPic(crypto: CryptoModel){
        viewModelScope.launch {
            ServiceLocator.getRetrofitClientPic().getCryptoPic(crypto.symbol.toLowerCase()).enqueue(object: retrofit2.Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful){
                        // Replace the old state so it will update in the view
                        val current = _crypto.value
                        current.picture = BitmapFactory.decodeStream(response.body()?.byteStream())

                        _crypto.value = current
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("CryptoPicture", t.toString())
                }
            })
        }
    }

    fun getCryptoPrices(name: String){
        viewModelScope.launch {
            // Replace spaces with a hyphen
            val result = ServiceLocator.getCryptoRepository().getCryptoPrices(name.toLowerCase().replace(' ','-').replace('.','-'))
            val newList = mutableListOf<DataPoint>()

            result.forEachIndexed{index, element ->
                newList.add(DataPoint(index.toFloat(), element.priceUsd.toFloat()))
            }

            _cryptoPrices.value = newList

        }
    }



}