package com.example.cryptoprojectjetpackcompose.views.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.viewmodel.CryptoViewModel
import com.example.cryptoprojectjetpackcompose.viewmodel.UserViewModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.CryptoProjectJetpackComposeTheme
import java.util.*

class SellCryptoActivity : ComponentActivity() {
    private val screenState = MutableLiveData("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoProjectJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    // Have a screen state so that the view will update when it gets into foreground
                    val state = screenState.observeAsState()
                    Log.d("EXAMPLE", "Recomposing screen - ${state.value}")
                    InitSellCryptoScreen()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Save the time as the state so that it will always be different
        screenState.value = Date().toString()
    }
}

@Composable
fun InitSellCryptoScreen(userViewModel: UserViewModel = ServiceLocator.getUserViewModelSL(),
                         cryptoViewModel: CryptoViewModel = ServiceLocator.getCryptoViewModelSL()){
    // TODO Check if code is redundant since we got the info from last activity
    val context = LocalContext.current
    val intent = (context as Activity).intent
    val crypto = intent.getSerializableExtra("crypto") as CryptoModel

    cryptoViewModel.getSingleCrypto(crypto.name)

    SellCryptoScreen(userViewModel, cryptoViewModel)
}


@Composable
fun SellCryptoScreen(userViewModel: UserViewModel,
                     cryptoViewModel: CryptoViewModel){
    // set up observers for necessary data
    val cryptoList = cryptoViewModel.cryptoList
    val user = userViewModel.user

    CryptoSeller(cryptoList = cryptoList.value, user = listOf(user.value), userViewModel = userViewModel)
}

@Composable
fun CryptoSeller(cryptoList: List<CryptoModel>, user: List<UserModel>, userViewModel: UserViewModel){
    LazyColumn(
        Modifier
            .fillMaxSize(1f)
            .padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top){
        items(cryptoList){ item ->
            CryptoBuyerSellerTopBar(crypto = item)
        }
        items(cryptoList){ item ->
            CryptoSellerMiddle(crypto = item, userViewModel = userViewModel)
        }
        items(user){ item ->
            CryptoBuyerUserInfo(user = item)
        }
    }
}


// TODO Probably change the USDTEXT with a cryptoAmount and reconfigure calculations
@Composable
fun CryptoSellerMiddle(crypto: CryptoModel, userViewModel: UserViewModel){
    Column() {
        var cryptoAmount by rememberSaveable {
            mutableStateOf("")
        }
        Row(Modifier.align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(text = crypto.symbol)
            TextField(value = cryptoAmount, onValueChange = {cryptoAmount = it}, Modifier.padding(start = 10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        }
        Row(Modifier.align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "USD")
            Text(text = "%.3f".format((if (cryptoAmount == "") 0.0 else cryptoAmount.toDouble()) * crypto.priceUsd), Modifier.padding(start = 10.dp))
        }
        Button(onClick = { userViewModel.sellCrypto(crypto,cryptoAmount.toDouble())}, enabled = (cryptoAmount != ""),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(.7f)) {
            Text(text = "Sell")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview5() {
    CryptoProjectJetpackComposeTheme {
        InitSellCryptoScreen()
    }
}