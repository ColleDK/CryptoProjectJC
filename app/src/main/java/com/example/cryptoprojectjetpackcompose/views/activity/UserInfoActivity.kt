package com.example.cryptoprojectjetpackcompose.views.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.model.CryptoModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.viewmodel.CryptoViewModel
import com.example.cryptoprojectjetpackcompose.viewmodel.UserViewModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.CryptoProjectJetpackComposeTheme
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
fun InitPortfolioScreen(userViewModel: UserViewModel = ServiceLocator.getUserViewModelSL(),
                        cryptoViewModel: CryptoViewModel = ServiceLocator.getCryptoViewModelSL()){
    //cryptoViewModel.getListOfCryptos()
    PortfolioScreen(userViewModel = userViewModel, cryptoViewModel = cryptoViewModel)
}

@Composable
fun PortfolioScreen(userViewModel: UserViewModel,
                    cryptoViewModel: CryptoViewModel){
    val user = userViewModel.user
    val cryptoList = cryptoViewModel.cryptoList

    PortfolioList(user = user.value, cryptoList = cryptoList.value)
}



@Composable
fun PortfolioList(user: UserModel, cryptoList: List<CryptoModel>){
    val context = LocalContext.current
    Column() {
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = {}, Modifier.fillMaxWidth(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent), enabled = false) {
                Text(text = "Points: " + user.balance.toString() + " USD", textAlign = TextAlign.Center)
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = {}, Modifier.fillMaxWidth(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent), enabled = false) {
                Text(text = "Your total current points are the sum of current value of all your currencies in USD", textAlign = TextAlign.Center)
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = {}, Modifier.fillMaxWidth(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent), enabled = false) {
                Text(text = "My Portfolio", textAlign = TextAlign.Center)
            }
        }
        LazyColumn(Modifier.fillMaxSize(1f)){
            items(cryptoList){ item ->
                CryptoItem(crypto = item)
            }
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