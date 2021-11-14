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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.viewmodel.BuySellViewModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.CryptoProjectJetpackComposeTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType

class BuyCryptoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoProjectJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    InitBuyScreen()
                }
            }
        }
    }
}

@Composable
fun InitBuyScreen(viewModel: BuySellViewModel = BuySellViewModel()){
    // Get the crypto to be bought from the intent
    val context = LocalContext.current
    val intent = (context as Activity).intent
    val crypto = intent.getSerializableExtra("crypto") as CryptoModel

    // Get the data from the viewmodel
    viewModel.getCrypto(crypto.name)
    viewModel.getUser()

    BuyCryptoScreen(viewModel = viewModel)
}


@Composable
fun BuyCryptoScreen(viewModel: BuySellViewModel){
    // set up observers for necessary data
    val cryptoObserver = viewModel.crypto
    val user = viewModel.user

    CryptoBuyer(crypto = cryptoObserver.component1(), user = user.component1(), viewModel)
}

@Composable
fun CryptoBuyer(crypto: List<CryptoModel>, user: List<UserModel>, viewModel: BuySellViewModel){
    LazyColumn(
        Modifier
            .fillMaxSize(1f)
            .padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top){
        items(crypto){ item ->
            CryptoBuyerSellerItem(crypto = item)
        }
        items(crypto){ item ->
            CryptoBuyerMiddle(crypto = item, viewModel)
        }
        items(user){ item ->
            CryptoBuyerUserInfo(user = item)
        }
    }
}


@Composable
fun CryptoBuyerMiddle(crypto: CryptoModel, viewModel: BuySellViewModel){
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
        Button(onClick = { viewModel.buyCrypto(crypto,if (usdText == "") 0.0 else usdText.toDouble())},
            Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(.7f)) {
            Text(text = "Buy")
        }
    }
}

@Composable
fun CryptoBuyerUserInfo(user: UserModel){
    Text(text = "You can only buy cryptocurrency in USD\nYou have ${user.balance} USD")
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview4() {
    CryptoProjectJetpackComposeTheme {
        InitBuyScreen()
    }
}