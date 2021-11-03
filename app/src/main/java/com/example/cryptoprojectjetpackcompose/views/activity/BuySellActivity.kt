package com.example.cryptoprojectjetpackcompose.views.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.viewmodel.BuySellViewModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.CryptoProjectJetpackComposeTheme
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot

class BuySellActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoProjectJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    BuySellScreen()
                }
            }
        }
    }
}

@Composable
fun BuySellScreen(viewModel: BuySellViewModel = BuySellViewModel()){
    // Get the crypto that needs to be showed
    val context = LocalContext.current
    val intent = (context as Activity).intent
    val cryptoName = intent.getStringExtra("cryptoName")

    // set up observers for necessary data
    val cryptoObserver = viewModel.crypto
    val user = viewModel.user
    val cryptoPrices = viewModel.cryptoPrices

    // stop updating constantly
    if (cryptoObserver.component1().isEmpty()) {
        viewModel.getCrypto(cryptoName!!)
        viewModel.getUser()
    }

    CryptoBuyerSeller(crypto = cryptoObserver.value, user.value, listOf(cryptoPrices.value))
}

@Composable
fun CryptoBuyerSeller(crypto: List<CryptoModel>, user: List<UserModel>, cryptoPrices: List<List<DataPoint>>) {
    LazyColumn(
        Modifier
            .fillMaxSize(1f)
            .padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top){
        items(crypto){ item ->
            CryptoBuyerSellerItem(crypto = item)
        }
        items(user){ item ->
            CryptoBuyerSellerMiddle(user = item, currentCrypt = crypto[0])
        }
        items(cryptoPrices){ item ->
            ChartBuilder(cryptoPrices = item)
        }
    }
}

@Composable
fun CryptoBuyerSellerItem(crypto: CryptoModel){
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


@Composable
fun CryptoBuyerSellerMiddle(user: UserModel, currentCrypt: CryptoModel){
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        val supply = if (user.currentCryptos.isEmpty() || user.currentCryptos.indexOf(currentCrypt) == -1) 0.0 else user.currentCryptos.elementAt(user.currentCryptos.indexOf(currentCrypt)).supply
        Text(text = "You have $supply ${currentCrypt.symbol.toUpperCase()}")
        Text(text = "$supply x ${currentCrypt.priceUsd}")
        Text(text = "Value ${supply * currentCrypt.priceUsd}")
        Row(Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
            val localContext = LocalContext.current
            Button(onClick = { localContext.startActivity(Intent(localContext, BuyCryptoActivity::class.java).putExtra("crypto", currentCrypt)) }) {
                Text(text = "Buy")
            }
            Button(onClick = { localContext.startActivity(Intent(localContext, SellCryptoActivity::class.java).putExtra("crypto", currentCrypt)) }) {
                Text(text = "Sell")
            }
        }
    }
}

@Composable
fun ChartBuilder(cryptoPrices: List<DataPoint>){
    // https://github.com/Madrapps/plot/blob/main/sample/src/main/java/com/madrapps/sample/linegraph/LineGraph1.kt
    // dont create graph if no data
    if (cryptoPrices.isEmpty()) return
    LineGraph(plot = LinePlot(
        listOf(LinePlot.Line(
            cryptoPrices,
            LinePlot.Connection{ start, end ->
                var color: Color
                if (start.y < end.y) color = Color.Red else color = Color.Green
                drawLine(color = color, start = start, end = end, strokeWidth = 4.dp.toPx())
            },
            LinePlot.Intersection{ center, _ ->
                val px = 2.dp.toPx()
                val topLeft = Offset(center.x - px, center.y - px)
                drawRect(Color.Black, topLeft, Size(px, px), alpha = 0.2f)
            }, LinePlot.Highlight(color = Color.Yellow)
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
        BuySellScreen()
    }
}