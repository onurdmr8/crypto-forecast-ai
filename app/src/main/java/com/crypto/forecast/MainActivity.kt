package com.crypto.forecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.crypto.forecast.data.monetization.AdMobManager
import com.crypto.forecast.data.monetization.BillingManager
import com.crypto.forecast.ui.screens.MainScreen
import com.crypto.forecast.ui.theme.CryptoForecastTheme
import com.crypto.forecast.ui.viewmodel.MainViewModel
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {

    private lateinit var adMobManager: AdMobManager
    private lateinit var billingManager: BillingManager
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Google Mobile Ads SDK
        MobileAds.initialize(this) {}

        adMobManager = AdMobManager(this)
        billingManager = BillingManager(this)
        viewModel = MainViewModel(adMobManager, billingManager)

        setContent {
            CryptoForecastTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
