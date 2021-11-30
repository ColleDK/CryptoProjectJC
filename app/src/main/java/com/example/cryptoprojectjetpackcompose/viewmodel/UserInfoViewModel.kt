package com.example.cryptoprojectjetpackcompose.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class UserInfoViewModel: ViewModel() {
    private val _user = mutableStateOf(UserModel(10000.0, mutableSetOf(), mutableListOf()))
    val user = _user

    fun getUser(){
        viewModelScope.launch {
            _user.value = ServiceLocator.getUserRepository().getUser()
            _user.value.currentCryptos.forEach {
                getOwnedCryptoPics(it.cryptoSymbol)
                getCryptoPrices(it.cryptoName)
            }
        }
    }

    // Create a hashmap between the crypto symbol and the bitmap
    private val _cryptoPics = mutableStateMapOf<String, Bitmap>()
    val cryptoPics = _cryptoPics

    fun getOwnedCryptoPics(symbol: String){
        viewModelScope.launch {
            ServiceLocator.getRetrofitClientPic().getCryptoPic(symbol.toLowerCase()).enqueue(object: retrofit2.Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful){
                        // Replace the old state so it will update in the view
                        _cryptoPics[symbol] = BitmapFactory.decodeStream(response.body()?.byteStream())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("CryptoPicture", t.toString())
                }
            })
        }
    }

    private val _cryptoPrices = mutableStateMapOf<String, Double>()
    val cryptoPrices = _cryptoPrices

    fun getCryptoPrices(name: String){
        viewModelScope.launch {
            Log.d("UserInfo", "Currently getting $name")
            val result = ServiceLocator.getRetrofitClient().getCrypto(name.toLowerCase().replace(' ','-').replace('.','-')).data
            // Replace the old state so it will update in the view
            _cryptoPrices[name] = result.priceUsd
            for (cryp in _cryptoPrices.values){
                Log.d("EXAMPLE", cryp.toString())
            }
        }
    }
}