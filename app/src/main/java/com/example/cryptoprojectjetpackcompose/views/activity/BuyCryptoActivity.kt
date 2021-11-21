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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.CryptoProjectJetpackComposeTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.MutableLiveData
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.viewmodel.BuyCryptoViewModel

import java.util.*

class BuyCryptoActivity : ComponentActivity() {
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
                    InitBuyScreen()
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
fun InitBuyScreen(buyCryptoViewModel: BuyCryptoViewModel = ServiceLocator.getBuyCryptoViewModelSL()){
    // TODO Figure if this code is redundant since we already loaded the data last activity
    // Get the crypto to be bought from the intent
    val context = LocalContext.current
    val intent = (context as Activity).intent
    val crypto = intent.getSerializableExtra("crypto") as CryptoModel

    // Get the data from the viewmodel
    buyCryptoViewModel.getCrypto(crypto.name)
    buyCryptoViewModel.getUser()

    BuyCryptoScreen(buyCryptoViewModel)
}


@Composable
fun BuyCryptoScreen(buyCryptoViewModel: BuyCryptoViewModel){
    // set up observers for necessary data
    val cryptoList = buyCryptoViewModel.crypto
    val user = buyCryptoViewModel.user

    CryptoBuyer(cryptoList = listOf(cryptoList.value), user = listOf(user.value), buyCryptoViewModel = buyCryptoViewModel)
}

// Whole layout for the activity
@Composable
fun CryptoBuyer(cryptoList: List<CryptoModel>, user: List<UserModel>, buyCryptoViewModel: BuyCryptoViewModel){
    LazyColumn(
        Modifier
            .fillMaxSize(1f)
            .padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top){
        items(cryptoList){ item ->
            // same top bar as the previous activity
            CryptoBuyerSellerTopBar(crypto = item)
        }
        items(cryptoList){ item ->
            CryptoBuyerMiddle(crypto = item, buyCryptoViewModel = buyCryptoViewModel)
        }
        items(user){ item ->
            CryptoBuyerUserInfo(user = item)
        }
    }
}

// This part includes the input field for the amount of dollar the user wants to buy for, the textfield where the dollars converted to crypto is and the buy button
@Composable
fun CryptoBuyerMiddle(crypto: CryptoModel, buyCryptoViewModel: BuyCryptoViewModel){
    Column() {
        // A remember state of the input text
        var usdText by rememberSaveable {
            mutableStateOf("")
        }
        Row(Modifier.align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "USD")
            // Make the input field only accept numbers
            TextField(value = usdText, onValueChange = {usdText = it}, Modifier.padding(start = 10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        }
        Row(Modifier.align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(text = crypto.symbol)
            Text(text = "%.3f".format((if (usdText == "") 0.0 else usdText.toDouble()) / crypto.priceUsd), Modifier.padding(start = 10.dp))
        }
        // The button should not be enabled when the usd text is empty
        Button(onClick = { buyCryptoViewModel.buyCrypto(crypto, usdText.toDouble())}, enabled = usdText != "",
            modifier = Modifier
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