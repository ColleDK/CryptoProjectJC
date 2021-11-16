package com.example.cryptoprojectjetpackcompose.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.madrapps.plot.line.DataPoint
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.util.*

class CryptoViewModel: ViewModel() {
    // Observable states for the activities to use
    private val _cryptoList = mutableStateOf(mutableListOf<CryptoModel>())
    val cryptoList = _cryptoList

    // TODO Retry when failure
    fun getCryptos(){
        viewModelScope.launch {
            // Get the cryptos from the database
            _cryptoList.value = ServiceLocator.getDBRoom().cryptoDao().getCryptos().map { it.toModel() }.toMutableList()

            // Get the cryptos from the API and save in database
            val cryptoApiList = ServiceLocator.getCryptoRepository().getCryptos()
            // If its empty we dont want to override the old list
            if (cryptoApiList.isNotEmpty()) _cryptoList.value = cryptoApiList
            for(crypto in _cryptoList.value!!){
                ServiceLocator.getDBRoom().cryptoDao().insertCrypto(crypto.toEntity())
                getCryptoPic(crypto)
            }
        }
    }

    // When an activity only needs a single crypto with detailed info
    fun getSingleCrypto(id: String){
        viewModelScope.launch {
            // Replace the old list with the single crypto
            _cryptoList.value = mutableListOf(ServiceLocator.getCryptoRepository().getCrypto(id))
            // Get the crypto pic and prices for the year
            for (crypt in _cryptoList.value){
                getCryptoPic(crypt)
                getCryptoPrices(crypt.name)
            }
        }
    }

    fun getListOfCryptos(idList: List<String>){
        // Clear everything that is in the list
        _cryptoList.value.clear()
        for (id in idList) {
            viewModelScope.launch {
                _cryptoList.value.plus(ServiceLocator.getCryptoRepository().getCrypto(id))
                getCryptoPic(_cryptoList.value[_cryptoList.value.size-1])
            }
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


    private val _cryptoPrices = mutableStateOf(listOf<DataPoint>())
    val cryptoPrices = _cryptoPrices

    fun getCryptoPrices(id: String){
        viewModelScope.launch {
            val result = ServiceLocator.getCryptoRepository().getCryptoPrices((id.lowercase(Locale.getDefault())).replace(' ','-'))
            val newList = mutableListOf<DataPoint>()

            result.forEachIndexed{index, element ->
                newList.add(DataPoint(index.toFloat(), element.priceUsd.toFloat()))
            }

            _cryptoPrices.value = newList
        }
    }
}