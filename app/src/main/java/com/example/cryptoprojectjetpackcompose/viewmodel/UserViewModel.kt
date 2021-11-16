package com.example.cryptoprojectjetpackcompose.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import kotlinx.coroutines.launch

/**
 * Class that will hold the information about the user and passed on by service locator
 */
class UserViewModel : ViewModel(){
    private val _user = mutableStateOf(UserModel(10000.0, mutableSetOf(), mutableListOf()))
    val user = _user

    fun getUser(){
        viewModelScope.launch {
            _user.value = ServiceLocator.getUserRepository().getUser()
        }
    }

    // TODO Buying a crypto
    fun buyCrypto(crypto: CryptoModel, amount: Double){


    }

    // TODO Selling a crypto
    fun sellCrypto(crypto: CryptoModel, amount: Double){

    }



}