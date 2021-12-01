package com.example.cryptoprojectjetpackcompose.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoprojectjetpackcompose.ResultCommand
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.db.entity.TransactionEntity
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.OwnedCryptoModel
import com.example.cryptoprojectjetpackcompose.model.TransactionModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.util.*

class BuyCryptoViewModel: ViewModel() {
    private val _user = mutableStateOf(UserModel(10000.0, mutableSetOf(), mutableListOf()))
    val user = _user
    private val _error = mutableStateOf(ResultCommand(status = ResultCommand.Status.LOADING))
    val error = _error

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
        try {
            viewModelScope.launch {
                ServiceLocator.getRetrofitClientPic().getCryptoPic(crypto.symbol.toLowerCase())
                    .enqueue(object : retrofit2.Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                // Replace the old state so it will update in the view
                                val current = _crypto.value
                                current.picture =
                                    BitmapFactory.decodeStream(response.body()?.byteStream())

                                _crypto.value = current
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.e("CryptoPicture", t.toString())
                        }
                    })
            }
        }  catch (e: HttpException){
            Log.d("CryptoError", "HttpException: Http request didn't respond with 200 (OK)!!")
            Log.d("CryptoError", e.message!!)
            _error.value = ResultCommand(message = "HTTP Error ${e.message}", status = ResultCommand.Status.ERROR)
        } catch (e: IOException){
            Log.d("CryptoError", "IOException: Network Error!!")
            Log.d("CryptoError", e.message!!)
            _error.value = ResultCommand(message = "IO Error ${e.message}", status = ResultCommand.Status.ERROR)

        }
    }

    fun clearError(){
        _error.value = ResultCommand(status = ResultCommand.Status.LOADING)
    }

    fun buyCrypto(crypto: CryptoModel, price: Double){
        // If negative price
        if (price < 0.0) {
            _error.value = ResultCommand(message = "You can't buy a negative amount of ${crypto.name}", status = ResultCommand.Status.ERROR)
            return
        }
        // Make sure the user has enough money
        if (price > _user.value.balance) {
            _error.value = ResultCommand(message = "You don't have enough money to buy ${price / crypto.priceUsd} of ${crypto.name} for $price USD", status = ResultCommand.Status.ERROR)
            return
        }
        viewModelScope.launch {
            // Get the volume of the crypto to be bought
            val volume = price / crypto.priceUsd

            // Create a transaction
            val newTransaction = TransactionModel(
                cryptoName = crypto.name,
                cryptoSymbol = crypto.symbol,
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

            _error.value = ResultCommand(message = "Congratz you just bought $volume ${crypto.name} for $price USD", status = ResultCommand.Status.SUCCESS)

        }
    }

}