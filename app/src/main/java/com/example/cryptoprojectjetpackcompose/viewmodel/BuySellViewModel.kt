package com.example.cryptoprojectjetpackcompose.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.db.entity.TransactionEntity
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.TransactionModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.web.dto.CryptoPriceDto
import com.madrapps.plot.line.DataPoint
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.util.*

class BuySellViewModel: ViewModel() {
    private val _crypto = mutableStateOf(listOf<CryptoModel>())
    val crypto = _crypto

    fun getCrypto(id: String){
        viewModelScope.launch {
            _crypto.value = listOf(ServiceLocator.getCryptoRepository().getCrypto(id))
            for (crypt in _crypto.value){
                getCryptoPic(crypt)
                getCryptoPrices(crypt.name)
            }
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
                        // replace the old state so it will update the lazycolumn
                        val current = _crypto.value
                        val replacement = current.map { if (it == crypto) it.copy(picture = BitmapFactory.decodeStream(response.body()?.byteStream())) else it }

                        _crypto.value = replacement
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("CryptoPicture", t.toString())
                }
            })
        }
    }



    private val _user = mutableStateOf(listOf<UserModel>())
    val user = _user

    fun getUser(){
        viewModelScope.launch {
            _user.value = listOf(ServiceLocator.getUserRepository().getUser())
        }
    }


    private val _cryptoPrices = mutableStateOf(listOf<DataPoint>())
    val cryptoPrices = _cryptoPrices

    fun getCryptoPrices(id: String){
        viewModelScope.launch {
            val result = ServiceLocator.getCryptoRepository().getCryptoPrices(id.lowercase(Locale.getDefault()))
            val newList = mutableListOf<DataPoint>()

            result.forEachIndexed{index, element ->
                newList.add(DataPoint(index.toFloat(), element.priceUsd.toFloat()))
            }

            _cryptoPrices.value = newList
        }
    }

    fun buyCrypto(crypto: CryptoModel, payment: Double){
        if (payment > user.component1()[0].balance) throw Exception("You don't have enough money")
        viewModelScope.launch {
            val transaction = TransactionModel(cryptoSymbol = crypto.symbol, volume = (payment / crypto.priceUsd), price = crypto.priceUsd, timestamp = Date(), state = TransactionEntity.Companion.TransactionState.BOUGHT)
            ServiceLocator.getDBRoom().transactionDao().insertTransaction(transaction.toEntity())
            ServiceLocator.getUserRepository().updateUser(user.component1()[0])
        }

    }


}