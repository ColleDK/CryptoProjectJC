package com.example.cryptoprojectjetpackcompose.views.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.viewmodel.BuySellViewModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.CryptoProjectJetpackComposeTheme

class SellCryptoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoProjectJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    SellCryptoScreen()
                }
            }
        }
    }
}


@Composable
fun SellCryptoScreen(viewModel: BuySellViewModel = BuySellViewModel()){
    val context = LocalContext.current
    val intent = (context as Activity).intent
    val crypto = intent.getSerializableExtra("crypto") as CryptoModel

    // set up observers for necessary data
    val cryptoObserver = viewModel.crypto
    val user = viewModel.user

    // stop updating constantly
    if (cryptoObserver.component1().isEmpty()) {
        viewModel.getCrypto(crypto.name)
        viewModel.getUser()
    }

}

@Composable
fun CryptoSeller(crypto: List<CryptoModel>, user: List<UserModel>, viewModel: BuySellViewModel){
    LazyColumn(
        Modifier
            .fillMaxSize(1f)
            .padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top){
        items(crypto){ item ->
            CryptoBuyerSellerItem(crypto = item)
        }
        items(crypto){ item ->
            CryptoSellerMiddle(crypto = item, viewModel)
        }
        items(user){ item ->
            CryptoBuyerUserInfo(user = item)
        }
    }
}


@Composable
fun CryptoSellerMiddle(crypto: CryptoModel, viewModel: BuySellViewModel){
    Column() {
        var usdText by rememberSaveable {
            mutableStateOf("")
        }
        Row(Modifier.align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "USD")
            TextField(value = usdText, onValueChange = {usdText = it}, Modifier.padding(start = 10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        }
        Row(Modifier.align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(text = crypto.symbol)
            Text(text = "%.3f".format((if (usdText == "") 0.0 else usdText.toDouble()) / crypto.priceUsd), Modifier.padding(start = 10.dp))
        }
        Button(onClick = { viewModel.sellCrypto(crypto,if (usdText == "") 0.0 else usdText.toDouble())},
            Modifier
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
        SellCryptoScreen()
    }
}