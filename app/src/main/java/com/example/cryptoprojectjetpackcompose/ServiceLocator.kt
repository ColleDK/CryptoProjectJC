package com.example.cryptoprojectjetpackcompose

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.preference.PreferenceManager
import com.example.cryptoprojectjetpackcompose.db.DBRoom
import com.example.cryptoprojectjetpackcompose.repository.CryptoRepository
import com.example.cryptoprojectjetpackcompose.repository.OwnedCryptoRepository
import com.example.cryptoprojectjetpackcompose.repository.TransactionRepository
import com.example.cryptoprojectjetpackcompose.repository.UserRepository
import com.example.cryptoprojectjetpackcompose.viewmodel.*
import com.example.cryptoprojectjetpackcompose.web.WebServiceCrypto
import com.example.cryptoprojectjetpackcompose.web.WebServiceCryptoPic
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {
    private lateinit var applicationContext: Context

    fun init(applicationContext: Context){
        this.applicationContext = applicationContext
    }

    private val dbRoom by lazy {
        DBRoom.build(applicationContext)
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.coincap.io/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WebServiceCrypto::class.java)

    }

    private val retrofitPic by lazy {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        Retrofit.Builder()
            .baseUrl("https://static.coincap.io/assets/icons/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(WebServiceCryptoPic::class.java)
    }

    private val cryptoRepo by lazy {
        CryptoRepository(getDBRoom(),
                        getRetrofitClient())
    }

    private val userRepo by lazy {
        UserRepository(
            getDBRoom(),
            PreferenceManager.getDefaultSharedPreferences(applicationContext),
            transactionRepository = getTransactionRepository(),
            ownedCryptoRepository = getOwnedCryptoRepository()
        )
    }

    private val ownedCryptoRepo by lazy {
        OwnedCryptoRepository(
            dbRoom = getDBRoom()
        )
    }

    private val transactionRepo by lazy {
        TransactionRepository(
            dbRoom = getDBRoom()
        )
    }

    private val mainViewModel by lazy {
        MainViewModel()
    }

    private val buySellViewModel by lazy {
        BuySellViewModel()
    }

    private val buyCryptoViewModel by lazy {
        BuyCryptoViewModel()
    }

    private val sellCryptoViewModel by lazy {
        SellCryptoViewModel()
    }

    private val userInfoViewModel by lazy {
        UserInfoViewModel()
    }

    private val transactionViewModel by lazy {
        TransactionViewModel()
    }




    // Database
    fun getDBRoom() = this.dbRoom

    // Retrofit
    fun getRetrofitClient() = this.retrofit
    fun getRetrofitClientPic() = this.retrofitPic

    // Repositories
    fun getCryptoRepository() = this.cryptoRepo
    fun getUserRepository() = this.userRepo
    fun getTransactionRepository() = this.transactionRepo
    fun getOwnedCryptoRepository() = this.ownedCryptoRepo

    // Viewmodels
    fun getMainViewModelSL() = this.mainViewModel
    fun getBuySellViewModelSL() = this.buySellViewModel
    fun getBuyCryptoViewModelSL() = this.buyCryptoViewModel
    fun getSellCryptoViewModelSL() = this.sellCryptoViewModel
    fun getUserInfoViewModelSL() = this.userInfoViewModel
    fun getTransactionViewModelSL() = this.transactionViewModel

    // Connectivity
    // https://stackoverflow.com/questions/57284582/networkinfo-has-been-deprecated-by-api-29
    fun getNetworkAvailable(): Boolean{
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
}