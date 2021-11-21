package com.example.cryptoprojectjetpackcompose.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class MainViewModel: ViewModel() {
    private val _user = mutableStateOf(UserModel(10000.0, mutableSetOf(), mutableListOf()))
    val user = _user

    fun getUser(){
        viewModelScope.launch {
            _user.value = ServiceLocator.getUserRepository().getUser()
        }
    }


    private val _cryptoList = mutableStateOf(mutableListOf<CryptoModel>())
    val cryptoList = _cryptoList

    fun getCryptos(){
        viewModelScope.launch {
            _cryptoList.value = ServiceLocator.getCryptoRepository().getCryptos()
            _cryptoList.value.forEach { getCryptoPic(it) }
        }
    }

    // TODO Retry when failure
    fun getCryptoPic(crypto: CryptoModel){
        viewModelScope.launch {
            ServiceLocator.getRetrofitClientPic().getCryptoPic(crypto.symbol.toLowerCase()).enqueue(object: retrofit2.Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful){
                        // If successful we override the current list with the new one
                        Log.d("CryptoPicture", "onResponse: success ${crypto.name}")
                        val current = _cryptoList.value
                        val replacement = current.map { if (it == crypto) it.copy(picture = BitmapFactory.decodeStream(response.body()?.byteStream())) else it } as MutableList<CryptoModel>
                        _cryptoList.value = replacement
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("CryptoPicture", t.toString())
                }
            })
        }
    }



}