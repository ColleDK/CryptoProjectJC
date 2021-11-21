package com.example.cryptoprojectjetpackcompose.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.db.entity.TransactionEntity
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.OwnedCryptoModel
import com.example.cryptoprojectjetpackcompose.model.TransactionModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.util.*

class BuyCryptoViewModel: ViewModel() {
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

    fun buyCrypto(crypto: CryptoModel, price: Double){
        // If negative price
        if (price < 0.0) return
        // Make sure the user has enough money
        // TODO throw exception or create message the view can use
        if (price > _user.value.balance) return
        viewModelScope.launch {
            // Get the volume of the crypto to be bought
            val volume = price / crypto.priceUsd

            // Create a transaction
            val newTransaction = TransactionModel(
                cryptoName = crypto.name,
                volume = volume,
                price = price,
                timestamp = Date(),
                state = TransactionEntity.Companion.TransactionState.BOUGHT
            )
            // Insert the transaction to the database
            ServiceLocator.getTransactionRepository().addTransaction(newTransaction)
            // Add the transaction to the user
            _user.value.transactions.plus(newTransaction)


            // Check if the user already owns this crypto
            val userOwnCrypto = _user.value.currentCryptos.find { it.cryptoName == crypto.name }
            if (userOwnCrypto == null) {
                // Create new owned crypto
                val newOwnedCrypto = OwnedCryptoModel(cryptoName = crypto.name, cryptoSymbol = crypto.symbol, volume = volume)
                // Add the new crypto to the user
                _user.value.currentCryptos.plus(newOwnedCrypto)
                // Insert the new crypto into the database
                ServiceLocator.getOwnedCryptoRepository()
                    .addOwnedCrypto(ownedCryptoModel = newOwnedCrypto)
            } else {
                // Update the volume of the owned crypto
                _user.value.currentCryptos.find { it.cryptoName == crypto.name }!!.volume += volume
                ServiceLocator.getOwnedCryptoRepository().updateOwnedCrypto(
                    name = crypto.name,
                    volume = _user.value.currentCryptos.find { it.cryptoName == crypto.name }!!.volume
                )
            }


            // Override the old value of the user
            val current = _user.value
            // Update the user
            current.balance -= price
            ServiceLocator.getUserRepository().updateUser(current)
            _user.value = current
        }
    }

}