package com.example.cryptoprojectjetpackcompose.views.activity
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.cryptoprojectjetpackcompose.R
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptoprojectjetpackcompose.ServiceLocator
import com.example.cryptoprojectjetpackcompose.db.entity.TransactionEntity
import com.example.cryptoprojectjetpackcompose.model.TransactionModel
import com.example.cryptoprojectjetpackcompose.model.UserModel
import com.example.cryptoprojectjetpackcompose.viewmodel.TransactionViewModel
import com.example.cryptoprojectjetpackcompose.views.activity.ui.theme.*

class TransactionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoProjectJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    InitTransactionScreen()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Update data on resume call
        ServiceLocator.getTransactionViewModelSL().getUser()
    }
}

@Composable
fun InitTransactionScreen(transactionViewModel: TransactionViewModel = ServiceLocator.getTransactionViewModelSL()){
    // Get the data from the database
    transactionViewModel.getUser()
    TransactionScreen(transactionViewModel = transactionViewModel)
}

@Composable
fun TransactionScreen(transactionViewModel: TransactionViewModel){
    // Create an observer on the data
    val user = transactionViewModel.user
    val transactionPics = transactionViewModel.transactionPics
    TransactionBody(user = user.value, transactionPics = transactionPics)
}

@Composable
fun TransactionBody(user: UserModel, transactionPics: Map<String, Bitmap>){
    // Wrapper for background and content
    Box() {
        // Background of the content
        Box(modifier = Modifier
            .matchParentSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.gradientTop,
                        MaterialTheme.colors.gradientBottom
                    )
                )
            )) {

        }
        Column(Modifier.fillMaxSize(1f)) {
            // Create the topbar
            Box(
                Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth(1f)
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colors.buttonColor
                    )
            ) {
                Text(
                    text = "Transactions",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(1f),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                )
            }
            LazyColumn(Modifier.fillMaxWidth(1f)) {
                items(user.transactions.toList().sortedByDescending { it.timestamp }) {
                    if (it.state == TransactionEntity.Companion.TransactionState.INSTALLATION) InitialTransactionItem(initialTransaction = it)
                    else TransactionItem(transaction = it, transactionPics = transactionPics)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionModel, transactionPics: Map<String, Bitmap>) {
    // Don't show the installation
    if (transaction.state == TransactionEntity.Companion.TransactionState.INSTALLATION) return
    Box(
        Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth(1f)
            .clip(CircleShape)
            .background(
                color = MaterialTheme.colors.itemColor
            )
    ) {
        Row(
            Modifier
                .fillMaxWidth(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            transactionPics[transaction.cryptoSymbol]?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clip(CircleShape)
                        .size(32.dp)
                )
            }
            Column() {
                // The state of the transaction i.e. bought/sold/installation
                Text(
                    text = "${transaction.state}",
                    color = if (transaction.state == TransactionEntity.Companion.TransactionState.SOLD) Color.Red else Color.Cyan,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${"%.3f".format(transaction.volume)} ${transaction.cryptoSymbol} for ${
                        "%.3f".format(
                            transaction.price
                        )
                    } USD",
                    color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                Text(text = "${transaction.timestamp}",
                    color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun InitialTransactionItem(initialTransaction: TransactionModel){
    Box(
        Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth(1f)
            .clip(CircleShape)
            .background(
                color = MaterialTheme.colors.itemColor
            )
    ){

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.money),
                contentDescription = "",
                modifier = Modifier.padding(start = 10.dp).clip(CircleShape).size(32.dp)
            )
            Column() {
                // The state of the transaction i.e. bought/sold/installation
                Text(
                    text = "${initialTransaction.state}",
                    color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "%.3f".format(initialTransaction.price),
                    color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
                Text(text = "${initialTransaction.timestamp}",
                    color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
        }
    }

}



@Preview(showBackground = true)
@Composable
fun DefaultPreview6() {
    CryptoProjectJetpackComposeTheme {
        InitTransactionScreen()
    }
}