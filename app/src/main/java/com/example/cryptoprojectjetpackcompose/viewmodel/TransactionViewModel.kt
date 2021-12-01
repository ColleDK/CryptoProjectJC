package com.example.cryptoprojectjetpackcompose.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.UserModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class TransactionViewModel: ViewModel() {
    private val _user = mutableStateOf(UserModel(10000.0, mutableSetOf(), mutableListOf()))
    val user = _user

    fun getUser(){
        viewModelScope.launch {
            _user.value = ServiceLocator.getUserRepository().getUser()
            _user.value.transactions.forEach { getTransactionPics(it.cryptoSymbol) }
        }
    }

    // Create a hashmap between the transaction's crypto name and crypto picture
    private val _transactionPics = mutableStateMapOf<String, Bitmap>()
    val transactionPics = _transactionPics

    fun getTransactionPics(symbol: String) {
        // If the symbol is empty then the transaction is the starting 10000 points
        if (symbol == "") return
        try {
            viewModelScope.launch {
                ServiceLocator.getRetrofitClientPic().getCryptoPic(symbol.toLowerCase())
                    .enqueue(object : retrofit2.Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                // Replace the old state so it will update in the view
                                _transactionPics[symbol] =
                                    BitmapFactory.decodeStream(response.body()?.byteStream())
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.e("CryptoPicture", t.toString())
                        }
                    })
            }
        } catch (e: HttpException){
            Log.d("CryptoError", "HttpException: Http request didn't respond with 200 (OK)!!")
            Log.d("CryptoError", e.message!!)
        } catch (e: IOException){
            Log.d("CryptoError", "IOException: Network Error!!")
            Log.d("CryptoError", e.message!!)
        }
    }


}