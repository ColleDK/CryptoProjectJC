package com.example.cryptoprojectjetpackcompose.views.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.OwnedCryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.viewmodel.UserInfoViewModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.CryptoProjectJetpackComposeTheme
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.gradientBottom
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.gradientTop
import java.util.*

class UserInfoActivity : ComponentActivity() {
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
                    InitPortfolioScreen()
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
fun InitPortfolioScreen(userInfoViewModel: UserInfoViewModel = ServiceLocator.getUserInfoViewModelSL()){
    userInfoViewModel.getUser()
    PortfolioScreen(userInfoViewModel = userInfoViewModel)
}

@Composable
fun PortfolioScreen(userInfoViewModel: UserInfoViewModel){
    val user = userInfoViewModel.user
    val cryptoPics = userInfoViewModel.cryptoPics
    val cryptoPrices = userInfoViewModel.cryptoPrices
    Log.d("Portfolio", "Recomposing screen with data ${user.value} \t ${cryptoPics.toMap().toString()} \t${cryptoPrices.toMap().toString()}")
    PortfolioList(user = user.value, cryptoPrices = cryptoPrices.toMap(), cryptoPics = cryptoPics.toMap())
}



@Composable
fun PortfolioList(user: UserModel, cryptoPrices: Map<String, Double>, cryptoPics: Map<String, Bitmap>) {
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
        ) {

        }
        Column(Modifier.fillMaxSize(1f)) {
            Row(Modifier.fillMaxWidth()) {
                Button(
                    onClick = {},
                    Modifier.fillMaxWidth(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    enabled = false
                ) {
                    Text(
                        text = "Points: " + user.balance.toString() + " USD",
                        textAlign = TextAlign.Center
                    )
                }
            }
            Row(Modifier.fillMaxWidth()) {
                Button(
                    onClick = {},
                    Modifier.fillMaxWidth(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    enabled = false
                ) {
                    Text(
                        text = "Your total current points are the sum of current value of all your currencies in USD",
                        textAlign = TextAlign.Center
                    )
                }
            }
            Row(Modifier.fillMaxWidth()) {
                Button(
                    onClick = {},
                    Modifier.fillMaxWidth(1f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    enabled = false
                ) {
                    Text(text = "My Portfolio", textAlign = TextAlign.Center)
                }
            }
            LazyColumn(Modifier.fillMaxWidth(1f)) {
                items(user.currentCryptos.toList()) { item ->
                    PortfolioListItem(
                        cryptoPrices = cryptoPrices,
                        currentCrypto = item,
                        pictures = cryptoPics
                    )
                }
            }
            //TODO make button not disappear when lazycolumn gets initialized
            Button(
                onClick = {
                    context.startActivity(
                        Intent(
                            context,
                            TransactionActivity::class.java
                        )
                    )
                }, modifier = Modifier
                    .align(CenterHorizontally)
                    .fillMaxWidth(.7f)
                    .background(color = Color.Black)
            ) {
                Text(text = "Transactions", color = Color.Black)
            }
        }
    }
}

@Composable
fun PortfolioListItem(cryptoPrices: Map<String, Double>, currentCrypto: OwnedCryptoModel, pictures: Map<String, Bitmap>){
    Row(Modifier.padding(top = 10.dp)) {
        pictures[currentCrypto.cryptoSymbol]?.let { Image(bitmap = it.asImageBitmap(), contentDescription = "", modifier = Modifier.size(32.dp)) }
        Column() {
            Text(text = "${"%.3f".format(currentCrypto.volume)}x${"%.3f".format(cryptoPrices[currentCrypto.cryptoName])}")
            cryptoPrices[currentCrypto.cryptoName]?.let {Text(text = "${"%.3f".format(currentCrypto.volume*it)} USD")  }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    CryptoProjectJetpackComposeTheme {
        InitPortfolioScreen()
    }
}