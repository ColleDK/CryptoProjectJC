package com.example.cryptoprojectjetpackcompose.views.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.cryptoprojectjetpackcompose.R
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.CryptoProjectJetpackComposeTheme
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.gradientBottom
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.gradientTop

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoProjectJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    SplashScreen()
                }
            }
        }
    }
}


@Composable
fun SplashScreen(){
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


        Column(Modifier.fillMaxSize(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Cryptocurrency Investor", modifier = Modifier.fillMaxWidth(1f), color = Color.Black, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp), textAlign = TextAlign.Center)
            val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.cryptoloader))
            LottieAnimation(composition = composition)
        }


        // Create a timer for starting the main activity after 1 second
        Handler(Looper.getMainLooper()).postDelayed(
            {
                context.startActivity(
                    Intent(
                        context,
                        MainActivity::class.java
                    )
                )
            }, 1000
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview7() {
    CryptoProjectJetpackComposeTheme {
        SplashScreen()
    }
}