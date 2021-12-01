package com.example.cryptoprojectjetpackcompose.views.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.viewmodel.MainViewModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.*
import java.lang.RuntimeException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InitStartScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        // Update the data on resume call
        ServiceLocator.getMainViewModelSL().getCryptos()
        ServiceLocator.getMainViewModelSL().getUser()
    }
}

// Initialize the screen with the data we want to get
@Composable
fun InitStartScreen(mainViewModel: MainViewModel = ServiceLocator.getMainViewModelSL()){
    // Get the data for the cryptos and the user
    Log.d("Screen state", "Init start screen")
    //mainViewModel.getCryptos()
    //mainViewModel.getUser()
    StartScreen(mainViewModel)
}


// Set the observers and create the list of cryptos
@Composable
fun StartScreen(mainViewModel: MainViewModel){
    val cryptoList = mainViewModel.cryptoList
    val user = mainViewModel.user

    CryptoList(cryptoList = cryptoList.value, user = user.value)
}

@Composable
fun CryptoList(cryptoList: MutableList<CryptoModel>, user: UserModel){
    val context = LocalContext.current
    Box() {
        // Background of the content
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colors.gradientTop,
                            MaterialTheme.colors.gradientBottom
                        )
                    )
                )
        )
        Column(Modifier.padding(top = 10.dp)) {
            // Header with user info
            Row(Modifier.fillMaxWidth()) {
                // If we click the header we want to direct to the user info page
                Button(
                    onClick = {
                        context.startActivity(
                            Intent(
                                context,
                                UserInfoActivity::class.java
                            )
                        )
                    },
                    Modifier
                        .fillMaxWidth(1f)
                        .padding(bottom = 10.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.buttonColor)
                ) {
                    var balance = user.balance
                    user.currentCryptos.forEach { balance += it.volume * ((cryptoList.find { it2 -> it2.name == it.cryptoName })?.priceUsd
                        ?: 0.0) }
                    Text(
                        text = "Points: ${"%.5f".format(balance)} USD",
                        textAlign = TextAlign.Center,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                }
            }
            LazyColumn(Modifier.fillMaxSize(1f)) {
                items(cryptoList) { item ->
                    CryptoItem(crypto = item)
                }
            }
        }
    }
}

// The adapter for each crypto
@Composable
fun CryptoItem(crypto: CryptoModel){
    val context = LocalContext.current
    Box(Modifier.padding(top = 10.dp)) {
        Box(modifier = Modifier
            .matchParentSize()
            .clip(shape = CircleShape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.itemColor,
                        MaterialTheme.colors.itemColor
                    )
                )
            )
        ) {

        }
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    // If we click an item in the list we want to go to the buy/sell page and the specific crypto is sent with the intent
                    context.startActivity(
                        Intent(
                            context,
                            BuySellActivity::class.java
                        ).putExtra("cryptoName", crypto.name)
                    )
                }), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
            // The picture of the crypto
            crypto.picture?.let { Image(bitmap = it.asImageBitmap(), contentDescription = "Image of ${crypto.name}",
                Modifier
                    .size(32.dp)
                    .padding(start = 5.dp)
                    .clip(CircleShape)) }
            // The name and symbol of the crypto
            Column(Modifier.padding(start = 10.dp)) {
                Text(text = crypto.name, color = Color.Black, style = TextStyle(fontWeight = FontWeight.Bold))
                Text(text = crypto.symbol, color = Color.Black, style = TextStyle(fontWeight = FontWeight.Bold))
            }
            // The price of the crypto
            Text(text = "%.3f".format(crypto.priceUsd), Modifier.padding(start = 20.dp), color = Color.Black, style = TextStyle(fontWeight = FontWeight.Bold), textAlign = TextAlign.Center)
            // The change in price in the recent 24 Hours
            Text(text = buildAnnotatedString
            {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = if (crypto.changePercent24Hr > 0) MaterialTheme.colors.textColorGreen else Color.Red)){
                    append("%.3f".format(crypto.changePercent24Hr))
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)){
                    append(" USD")
                }
            },
                Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .fillMaxWidth(1f), textAlign = TextAlign.End)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CryptoProjectJetpackComposeTheme {
        InitStartScreen()
    }
}