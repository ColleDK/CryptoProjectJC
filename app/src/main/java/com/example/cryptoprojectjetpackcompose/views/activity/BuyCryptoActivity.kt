package com.example.cryptoprojectjetpackcompose.views.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.viewmodel.BuyCryptoViewModel
import com.example.cryptoprojectjetpackcompose.viewmodel.SellCryptoViewModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.*

import java.util.*

class BuyCryptoActivity : ComponentActivity() {
    private val screenState = MutableLiveData("")

    @ExperimentalComposeUiApi
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

        // Update data at on resume call
        ServiceLocator.getBuyCryptoViewModelSL().getUser()
        ServiceLocator.getBuyCryptoViewModelSL().getCrypto((intent.getSerializableExtra("crypto") as CryptoModel).name)
    }

    override fun finish() {
        super.finish()
        ServiceLocator.getBuyCryptoViewModelSL().clearError()
    }
}

@ExperimentalComposeUiApi
@Composable
fun InitBuyScreen(buyCryptoViewModel: BuyCryptoViewModel = ServiceLocator.getBuyCryptoViewModelSL()){
    // Get the crypto to be bought from the intent
    val context = LocalContext.current
    val intent = (context as Activity).intent
    val crypto = intent.getSerializableExtra("crypto") as CryptoModel

    // Get the data from the viewmodel
    buyCryptoViewModel.getCrypto(crypto.name)
    buyCryptoViewModel.getUser()

    BuyCryptoScreen(buyCryptoViewModel)
}


@ExperimentalComposeUiApi
@Composable
fun BuyCryptoScreen(buyCryptoViewModel: BuyCryptoViewModel){
    // set up observers for necessary data
    val cryptoList = buyCryptoViewModel.crypto
    val user = buyCryptoViewModel.user

    CryptoBuyer(cryptoList = listOf(cryptoList.value), user = listOf(user.value), buyCryptoViewModel = buyCryptoViewModel)
}

// Whole layout for the activity
@ExperimentalComposeUiApi
@Composable
fun CryptoBuyer(cryptoList: List<CryptoModel>, user: List<UserModel>, buyCryptoViewModel: BuyCryptoViewModel){
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
        LazyColumn(
            Modifier
                .fillMaxSize(1f)
                .padding(top = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            items(cryptoList) { item ->
                // same top bar as the previous activity
                CryptoBuyerSellerTopBar(crypto = item)
            }
            items(cryptoList) { item ->
                CryptoBuyerMiddle(crypto = item, buyCryptoViewModel = buyCryptoViewModel)
            }
            items(user) { item ->
                CryptoBuyerUserInfo(user = item)
            }
        }
        // Set up the alert dialog box
        ObserveAlertBuy(buyCryptoViewModel = buyCryptoViewModel)
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
        Box(Modifier.padding(top = 10.dp, bottom = 20.dp).fillMaxWidth(1f)) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape = CircleShape)
                    .background(
                        color = MaterialTheme.colors.itemColor
                    )
            ) {}
            Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp).align(Alignment.Center)) {
                Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "USD",
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        fontSize = 15.sp)
                    // Make the input field only accept numbers
                    TextField(value = usdText,
                        onValueChange = {usdText = it},
                        Modifier.padding(start = 10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                }
                Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = crypto.symbol,
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        fontSize = 15.sp)
                    Text(text = "%.3f".format((if (usdText == "") 0.0 else usdText.toDouble()) / crypto.priceUsd),
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 10.dp))
                }
            }
        }

        // The button should not be enabled when the usd text is empty
        Button(onClick = { buyCryptoViewModel.buyCrypto(crypto, usdText.toDouble()) }, enabled = usdText != "",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
                .fillMaxWidth(.7f),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.buttonColor)) {
            Text(text = "Buy",
                color = Color.Black,
                style = TextStyle(fontWeight = FontWeight.Bold),)
        }
    }
}

@Composable
fun CryptoBuyerUserInfo(user: UserModel){
    Box(Modifier.padding(top = 10.dp, bottom = 20.dp).fillMaxWidth(1f)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape = CircleShape)
                .background(
                    color = MaterialTheme.colors.itemColor
                )
        ) {}
        Text(text = buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("You can only buy cryptocurrency in USD\nYou have ")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                append("${user.balance}")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(" USD")
            }
        }, modifier = Modifier.padding(start = 10.dp, end = 10.dp))
    }
}

@ExperimentalComposeUiApi
@Composable
fun ObserveAlertBuy(buyCryptoViewModel: BuyCryptoViewModel){
    // Dismissing the dialog
    val openDialog = remember { mutableStateOf(true)}
    // Observable state for the error handling
    val error = buyCryptoViewModel.error

    // Reset the dialog value when
    openDialog.value = true

    Log.d("SELL", "New alert incoming with status ${error.value.status} and opendialog")

    // Pass the error to the alert box
    error.value.message?.let { Alert(status = error.value.status, message = it, openValue = {openDialog.value}, onDismiss = {
        openDialog.value = false
    }) }
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview4() {
    CryptoProjectJetpackComposeTheme {
        InitBuyScreen()
    }
}