package com.example.cryptoprojectjetpackcompose.views.activity

import android.os.Bundle
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
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.viewmodel.StartViewModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.CryptoProjectJetpackComposeTheme

class UserInfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoProjectJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    PortfolioScreen()
                }
            }
        }
    }
}

@Composable
fun PortfolioScreen(viewModel: StartViewModel = StartViewModel()){
    val user = viewModel.user.observeAsState()

    viewModel.getUser()

    user.value?.let { it -> PortfolioList(user = it) }
}



@Composable
fun PortfolioList(user: UserModel){
    val context = LocalContext.current
    Column() {
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = {}, Modifier.fillMaxWidth(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)) {
                Text(text = "Points: " + user.balance.toString() + " USD", textAlign = TextAlign.Center)
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = {}, Modifier.fillMaxWidth(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)) {
                Text(text = "Your total current points are the sum of current value of all your currencies in USD", textAlign = TextAlign.Center)
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = {}, Modifier.fillMaxWidth(1f), colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)) {
                Text(text = "My Portfolio", textAlign = TextAlign.Center)
            }
        }

        LazyColumn(Modifier.fillMaxSize(1f)){
            items(user.currentCryptos){ item ->
                CryptoItem(crypto = item)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    CryptoProjectJetpackComposeTheme {
        PortfolioScreen()
    }
}