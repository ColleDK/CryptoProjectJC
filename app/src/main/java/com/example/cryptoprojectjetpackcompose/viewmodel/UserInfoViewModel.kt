/*
package com.example.cryptoprojectjetpackcompose.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class UserInfoViewModel: ViewModel() {
    private val _cryptoList = mutableStateOf(listOf<CryptoModel>())
    val cryptoListInfo = _cryptoList

    fun getUserCryptoPics(cryptoList: List<CryptoModel>){
        for (crypto in cryptoList){
            Log.d("CryptoPicture", crypto.toString())
            viewModelScope.launch {
                ServiceLocator.getRetrofitClientPic().getCryptoPic(crypto.symbol.toLowerCase()).enqueue(object: retrofit2.Callback<ResponseBody>{
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful){
                            // replace the old state so it will update the lazycolumn
                            val current = _cryptoList.value
                            val replacement = current.map { if (it.name == crypto.name) it.copy(picture = BitmapFactory.decodeStream(response.body()?.byteStream())) else it  }
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
}*/
