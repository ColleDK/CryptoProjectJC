package com.example.cryptoprojectjetpackcompose.views.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.CryptoProjectJetpackComposeTheme
import com.example.cryptoprojectjetpackcompose.viewmodel.StartViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { InitStartScreen() }
    }
}

@Composable
fun InitStartScreen(viewModel: StartViewModel = StartViewModel()){
    viewModel.getCryptos()
    viewModel.getUser()
    StartScreen(viewModel)
}


@Composable
fun StartScreen(viewModel: StartViewModel){
    val list = viewModel.cryptoList.observeAsState()
    val user = viewModel.user.observeAsState()

    list.value?.let { user.value?.let { it1 -> CryptoList(cryptoList = it, user = it1) } }
}

@Composable
fun CryptoList(cryptoList: MutableList<CryptoModel>, user: UserModel){
    Log.i("MainActivity", "CryptoList update}")
    val context = LocalContext.current
    Column() {
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = { context.startActivity(Intent(context, UserInfoActivity::class.java)) }, Modifier.fillMaxWidth(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)) {
                Text(text = "Points: " + user.balance.toString() + " USD", textAlign = TextAlign.Center)
            }
        }
        LazyColumn(Modifier.fillMaxSize(1f)){
            items(cryptoList){ item ->
                CryptoItem(crypto = item)
            }
        }
    }
}

@Composable
fun CryptoItem(crypto: CryptoModel){
    val context = LocalContext.current
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                context.startActivity(
                    Intent(
                        context,
                        BuySellActivity::class.java
                    ).putExtra("cryptoName", crypto.name)
                )
            }), Arrangement.Start) {
        crypto.picture?.let { Image(bitmap = it.asImageBitmap(), contentDescription = "",
            Modifier
                .size(32.dp)
                .align(Alignment.CenterVertically)) }
        Column() {
            Text(text = crypto.name)
            Text(text = crypto.symbol)
        }
        Text(text = "%.3f".format(crypto.priceUsd), Modifier.padding(10.dp))
        Text(text = "%.3f".format(crypto.changePercent24Hr), Modifier.padding(10.dp), color = if (crypto.changePercent24Hr > 0) Color.Green else Color.Red)
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CryptoProjectJetpackComposeTheme {
        InitStartScreen()
    }
}