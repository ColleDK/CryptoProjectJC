package com.example.cryptoprojectjetpackcompose.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class StartViewModel: ViewModel() {

    private val _cryptoList = MutableLiveData<MutableList<CryptoModel>>()
    val cryptoList: LiveData<MutableList<CryptoModel>> = _cryptoList

    fun getCryptos(){
        viewModelScope.launch {
            _cryptoList.value = ServiceLocator.getCryptoRepository().getCryptos()
            for(crypto in _cryptoList.value!!){
                getCryptoPics(crypto)
            }
        }
    }


    fun getCryptoPics(crypto: CryptoModel){
        viewModelScope.launch {
            ServiceLocator.getRetrofitClientPic().getCryptoPic(crypto.symbol.toLowerCase()).enqueue(object: retrofit2.Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful){
                        crypto.picture = BitmapFactory.decodeStream(response.body()?.byteStream())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("CryptoPicture", t.toString())
                }
            })
        }

    }


}