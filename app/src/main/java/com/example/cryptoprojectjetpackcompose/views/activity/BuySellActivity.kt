package com.example.cryptoprojectjetpackcompose.views.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.viewmodel.CryptoViewModel
import com.example.cryptoprojectjetpackcompose.viewmodel.UserViewModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.CryptoProjectJetpackComposeTheme
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import java.util.*

class BuySellActivity : ComponentActivity() {
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
                    InitBuySellScreen()
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
fun InitBuySellScreen(userViewModel: UserViewModel = ServiceLocator.getUserViewModelSL(),
                      cryptoViewModel: CryptoViewModel = ServiceLocator.getCryptoViewModelSL()){
    // Get the crypto that needs to be showed
    val context = LocalContext.current
    val intent = (context as Activity).intent
    val cryptoName = intent.getStringExtra("cryptoName")

    // Get the crypto we want to observe
    cryptoViewModel.getSingleCrypto(cryptoName!!)
    BuySellScreen(userViewModel, cryptoViewModel)
}

@Composable
fun BuySellScreen(userViewModel: UserViewModel,
                  cryptoViewModel: CryptoViewModel){
    // set up observers for necessary data
    val cryptoList = cryptoViewModel.cryptoList
    val cryptoPrices = cryptoViewModel.cryptoPrices
    val user = userViewModel.user

    CryptoBuyerSeller(cryptoList = cryptoList.value, listOf(user.value), listOf(cryptoPrices.value))
}

// The whole activity composable
@Composable
fun CryptoBuyerSeller(cryptoList: List<CryptoModel>, user: List<UserModel>, cryptoPrices: List<List<DataPoint>>) {
    LazyColumn(
        Modifier
            .fillMaxSize(1f)
            .padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top){
        items(cryptoList){ item ->
            CryptoBuyerSellerTopBar(crypto = item)
        }
        items(user){ item ->
            CryptoBuyerSellerMiddle(user = item, currentCrypt = cryptoList[0])
        }
        items(cryptoPrices){ item ->
            ChartBuilder(cryptoPrices = item)
        }
    }
}

// The top bar includes the picture of the crypto, the name and symbol and the current price of the crypto
@Composable
fun CryptoBuyerSellerTopBar(crypto: CryptoModel){
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth()) {
            crypto.picture?.let { Image(bitmap = it.asImageBitmap(), contentDescription = "",
                Modifier
                    .size(64.dp)
                    .align(Alignment.CenterVertically)) }
            Column() {
                Text(text = crypto.name + " (" + crypto.symbol + ")")
                Text(text = "$" + "%.3f".format(crypto.priceUsd))
            }
        }

    }
}

// The middle part includes the sell and buy button as well as the number of said cryptos owned with the value of that number
@Composable
fun CryptoBuyerSellerMiddle(user: UserModel, currentCrypt: CryptoModel){
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // Check for the amount of current crypto the user owns
        val cryptoInSet = user.currentCryptos.find { it.cryptoName == currentCrypt.name }
        val supply = cryptoInSet?.volume ?: 0.0

        // Text for the amount of supply owned by the user
        Text(text = "You have $supply ${currentCrypt.symbol.toUpperCase()}")
        Text(text = "$supply x ${currentCrypt.priceUsd}")
        Text(text = "Value ${supply * currentCrypt.priceUsd}")

        // Buy and sell buttons
        Row(Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
            val localContext = LocalContext.current
            // If the users balance is 0 then we dont want them to have the ability to buy the crypto
            Button(onClick = { localContext.startActivity(Intent(localContext, BuyCryptoActivity::class.java).putExtra("crypto", currentCrypt)) }, enabled = user.balance > 0.0) {
                Text(text = "Buy")
            }

            // If the user does not own any of the current crypto then we dont want them to have the ability to sell it
            Log.d("IsEnabled", (supply != 0.0).toString())
            Log.d("IsEnabled", "Supply $supply")
            Button(onClick = { localContext.startActivity(Intent(localContext, SellCryptoActivity::class.java).putExtra("crypto", currentCrypt)) }, enabled = supply != 0.0) {
                Text(text = "Sell")
            }
        }
    }
}


// Source: https://github.com/Madrapps/plot/blob/main/sample/src/main/java/com/madrapps/sample/linegraph/LineGraph1.kt
// Creating the price chart for the crypto we are observing
@Composable
fun ChartBuilder(cryptoPrices: List<DataPoint>){
    // dont create graph if no data
    if (cryptoPrices.isEmpty()) return
    LineGraph(plot = LinePlot(
        listOf(LinePlot.Line(
            cryptoPrices,
            LinePlot.Connection{ start, end ->
                val color: Color = if (start.y < end.y) Color.Red else Color.Green
                drawLine(color = color, start = start, end = end, strokeWidth = 4.dp.toPx())
            },
            LinePlot.Intersection{ center, _ ->
                val px = 2.dp.toPx()
                val topLeft = Offset(center.x - px, center.y - px)
                drawRect(Color.Black, topLeft, Size(px, px), alpha = 0.2f)
            }, LinePlot.Highlight(color = Color.Yellow), LinePlot.AreaUnderLine(color = Color.Blue, 0.1f)
        )) , grid = LinePlot.Grid(color = Color.Gray, steps = cryptoPrices.size), yAxis = LinePlot.YAxis(steps = 10, roundToInt = false)
        ), modifier = Modifier
        .fillMaxWidth()
        .height(400.dp),
        onSelection = { xline, points ->
            // TODO
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    CryptoProjectJetpackComposeTheme {
        InitBuySellScreen()
    }
}