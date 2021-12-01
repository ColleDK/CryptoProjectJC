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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.MutableLiveData
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.cryptoprojectjetpackcompose.R
import com.example.cryptoprojectjetpackcompose.ResultCommand
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.viewmodel.SellCryptoViewModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.*
import java.util.*

class SellCryptoActivity : ComponentActivity() {
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
                    InitSellCryptoScreen()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Save the time as the state so that it will always be different
        screenState.value = Date().toString()

        // Update data at on resume call
        ServiceLocator.getSellCryptoViewModelSL().getUser()
        ServiceLocator.getSellCryptoViewModelSL().getCrypto((intent.getSerializableExtra("crypto") as CryptoModel).name)
    }

    override fun finish() {
        super.finish()
        ServiceLocator.getSellCryptoViewModelSL().clearError()
    }
}

@ExperimentalComposeUiApi
@Composable
fun InitSellCryptoScreen(sellCryptoViewModel: SellCryptoViewModel = ServiceLocator.getSellCryptoViewModelSL()){
    val context = LocalContext.current
    val intent = (context as Activity).intent
    val crypto = intent.getSerializableExtra("crypto") as CryptoModel

    sellCryptoViewModel.getCrypto(crypto.name)
    sellCryptoViewModel.getUser()

    SellCryptoScreen(sellCryptoViewModel)
}


@ExperimentalComposeUiApi
@Composable
fun SellCryptoScreen(sellCryptoViewModel: SellCryptoViewModel){
    // set up observers for necessary data
    val crypto = sellCryptoViewModel.crypto
    val user = sellCryptoViewModel.user

    Log.d("SellScreen", "Recomposing screen with $crypto")

    CryptoSeller(cryptoList = listOf(crypto.value), user = listOf(user.value), sellCryptoViewModel = sellCryptoViewModel)
}

@ExperimentalComposeUiApi
@Composable
fun CryptoSeller(cryptoList: List<CryptoModel>, user: List<UserModel>, sellCryptoViewModel: SellCryptoViewModel){
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
                CryptoBuyerSellerTopBar(crypto = item)
            }
            items(cryptoList) { item ->
                CryptoSellerMiddle(crypto = item, sellCryptoViewModel = sellCryptoViewModel)
            }
            items(user) { item ->
                CryptoSellerUserInfo(user = item, cryptoSymbol = cryptoList[0].symbol)
            }
        }
        // Set up the alert dialog box
        ObserveAlert(sellCryptoViewModel = sellCryptoViewModel)
    }
}


@Composable
fun CryptoSellerMiddle(crypto: CryptoModel, sellCryptoViewModel: SellCryptoViewModel){
    Column() {
        var cryptoAmount by rememberSaveable {
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
                    Text(text = crypto.symbol,
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        fontSize = 15.sp)
                    TextField(
                        value = cryptoAmount,
                        onValueChange = { cryptoAmount = it },
                        Modifier.padding(start = 10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                }
                Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "USD",
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        fontSize = 15.sp)
                    Text(
                        text = "%.3f".format((if (cryptoAmount == "") 0.0 else cryptoAmount.toDouble()) * crypto.priceUsd),
                        color = Color.Black,
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
            }
        }


        Button(onClick = { sellCryptoViewModel.sellCrypto(crypto,cryptoAmount.toDouble())}, enabled = (cryptoAmount != ""),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
                .fillMaxWidth(.7f), colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.buttonColor)) {
            Text(text = "Sell",
                color = Color.Black,
                style = TextStyle(fontWeight = FontWeight.Bold),)
        }
    }
}

@Composable
fun CryptoSellerUserInfo(user: UserModel, cryptoSymbol: String){
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
                append("You can only sell cryptocurrency to USD\nYou have ")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                append("${user.currentCryptos.find { it.cryptoSymbol == cryptoSymbol }?.volume}")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(" $cryptoSymbol")
            }
        }, modifier = Modifier.padding(start = 10.dp, end = 10.dp))
    }
}

@ExperimentalComposeUiApi
@Composable
fun ObserveAlert(sellCryptoViewModel: SellCryptoViewModel){
    // Dismissing the dialog
    val openDialog = remember { mutableStateOf(true)}
    // Observable state for the error handling
    val error = sellCryptoViewModel.error

    // Reset the dialog value when
    openDialog.value = true

    Log.d("SELL", "New alert incoming with status ${error.value.status} and opendialog")

    // Pass the error to the alert box
    error.value.message?.let { Alert(status = error.value.status, message = it, openValue = {openDialog.value}, onDismiss = {
        openDialog.value = false
    }) }
}

@ExperimentalComposeUiApi
@Composable
fun Alert(status: ResultCommand.Status, message: String, openValue: () -> Boolean, onDismiss: () -> Unit){
    // Open the dialog box if not dismissed
    if (openValue()) {
        when (status) {
            // Sold a crypto successfully -> create a dialog box with an animation and finish activity when done
            ResultCommand.Status.SUCCESS -> {
                val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.success))
                val activity = (LocalContext.current as Activity)
                AlertDialog(onDismissRequest = {
                    onDismiss()
                    // End the activity when you sold the crypto
                    activity.finish()},
                    properties = DialogProperties(usePlatformDefaultWidth = false),

                title = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth(1f)) {
                        LottieAnimation(composition, modifier = Modifier.size(200.dp))
                    }
                }, text = {
                        Text(
                            text = message,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            modifier = Modifier.fillMaxWidth(1f)
                        )
                    },
                    buttons = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(all = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    onDismiss()
                                    // End the activity when you sold the crypto
                                    activity.finish()
                                }, colors = ButtonDefaults.buttonColors(MaterialTheme.colors.buttonColor)
                            ) {
                                Text("Finish", color = Color.Black, style = TextStyle(fontWeight = FontWeight.Bold))
                            }
                        }
                    },
                    backgroundColor = Color.White
                )
            }

            // Error has occurred somewhere so we notice the user
            ResultCommand.Status.ERROR -> {
                val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.error))
                AlertDialog(onDismissRequest = { onDismiss() },
                    properties = DialogProperties(usePlatformDefaultWidth = false),

                    title = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth(1f)) {
                            LottieAnimation(composition, modifier = Modifier.size(200.dp))
                        }
                    }, text = {
                        Text(
                            text = message,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            style = TextStyle(fontWeight = FontWeight.Bold),
                            modifier = Modifier.fillMaxWidth(1f)
                        )
                    },
                    buttons = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(all = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    onDismiss()
                                }, colors = ButtonDefaults.buttonColors(MaterialTheme.colors.buttonColor)
                            ) {
                                Text("Dismiss", color = Color.Black, style = TextStyle(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                )
            }

            // Default value of status so do nothing rn
            else -> Log.d("Loading", "Loading")
        }
    }
}


@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview5() {
    CryptoProjectJetpackComposeTheme {
        InitSellCryptoScreen()
    }
}