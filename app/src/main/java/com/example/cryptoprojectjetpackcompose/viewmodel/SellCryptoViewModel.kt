package com.example.cryptoprojectjetpackcompose.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.db.entity.TransactionEntity
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.TransactionModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.util.*

class SellCryptoViewModel: ViewModel() {
    private val _user = mutableStateOf(UserModel(10000.0, mutableSetOf(), mutableListOf()))
    val user = _user

    fun getUser(){
        viewModelScope.launch {
            _user.value = ServiceLocator.getUserRepository().getUser()
        }
    }

    private val _crypto = mutableStateOf(CryptoModel("","",0.0,0.0,0.0,null))
    val crypto = _crypto

    fun getCrypto(name: String){
        viewModelScope.launch {
            _crypto.value = ServiceLocator.getCryptoRepository().getCrypto(name)
            getCryptoPic(_crypto.value)
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

    fun sellCrypto(crypto: CryptoModel, amount: Double){
        // If negative amount
        if (amount < 0.0) return
        // If the user does not own the crypto return
        if (_user.value.currentCryptos.find { it.cryptoName == crypto.name } == null) return
        // If the amount is more than the owned volume
        if (_user.value.currentCryptos.find { it.cryptoName == crypto.name }!!.volume < amount) return

        viewModelScope.launch {
            val price = amount * crypto.priceUsd

            // Create a transaction
            val newTransaction = TransactionModel(cryptoName = crypto.name, cryptoSymbol = crypto.symbol, volume = amount, price = price, timestamp = Date(), state = TransactionEntity.Companion.TransactionState.SOLD)
            // Insert the transaction to the database
            ServiceLocator.getTransactionRepository().addTransaction(newTransaction)
            // Add the transaction to the user
            _user.value.transactions.plus(newTransaction)

            // Update the cryptos volume on the user
            _user.value.currentCryptos.find { it.cryptoName == crypto.name }!!.volume -= amount
            ServiceLocator.getOwnedCryptoRepository().updateOwnedCrypto(name = crypto.name, volume = _user.value.currentCryptos.find { it.cryptoName == crypto.name }!!.volume)

            // Update the user balance
            _user.value.balance += price
            ServiceLocator.getUserRepository().updateUser(_user.value)

            // Override the old value of the user
            val current = _user.value
            _user.value = current
        }
    }

}