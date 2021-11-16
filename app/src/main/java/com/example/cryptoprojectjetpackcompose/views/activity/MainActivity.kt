package com.example.cryptoprojectjetpackcompose.views.activity

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
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
import androidx.lifecycle.MutableLiveData
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.viewmodel.CryptoViewModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.CryptoProjectJetpackComposeTheme
import com.example.cryptoprojectjetpackcompose.viewmodel.UserViewModel
import java.util.*

class MainActivity : ComponentActivity() {
    private val screenState = MutableLiveData("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        prefs.edit().putBoolean("instantiated", false).apply()*/
        setContent {
            // Have a screen state so that the view will update when it gets into foreground
            val state = screenState.observeAsState()
            Log.d("Screen state", "Recomposing screen - ${state.value}")
            InitStartScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        // Save the time as the state so that it will always be different
        screenState.value = Date().toString()
    }
}

// Initialize the screen with the data we want to get
@Composable
fun InitStartScreen(userViewModel: UserViewModel = ServiceLocator.getUserViewModelSL(),
                    cryptoViewModel: CryptoViewModel = ServiceLocator.getCryptoViewModelSL()){
    // Get the data for the cryptos and the user
    Log.d("Screen state", "Init start screen")
    cryptoViewModel.getCryptos()
    userViewModel.getUser()
    StartScreen(userViewModel, cryptoViewModel)
}


// Set the observers and create the list of cryptos
@Composable
fun StartScreen(userViewModel: UserViewModel,
                cryptoViewModel: CryptoViewModel){
    val cryptoList = cryptoViewModel.cryptoList
    val user = userViewModel.user

    CryptoList(cryptoList = cryptoList.value, user = user.value)
}

@Composable
fun CryptoList(cryptoList: MutableList<CryptoModel>, user: UserModel){
    val context = LocalContext.current
    Column() {
        // Header with user info
        Row(Modifier.fillMaxWidth()) {
            // If we click the header we want to direct to the user info page
            Button(onClick = { context.startActivity(Intent(context, UserInfoActivity::class.java)) }, Modifier.fillMaxWidth(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)) {
                Text(text = "Points: " + user.balance.toString() + " USD", textAlign = TextAlign.Center)
            }
        }
        // List of cryptos
        LazyColumn(Modifier.fillMaxSize(1f)){
            items(cryptoList){ item ->
                CryptoItem(crypto = item)
            }
        }
    }
}

// The adapter for each crypto
@Composable
fun CryptoItem(crypto: CryptoModel){
    val context = LocalContext.current
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