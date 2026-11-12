package com.crypto.forecast.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.forecast.data.model.*
import com.crypto.forecast.data.monetization.AdMobManager
import com.crypto.forecast.data.monetization.BillingManager
import com.crypto.forecast.data.remote.CryptoForecastRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val adMobManager: AdMobManager,
    private val billingManager: BillingManager
) : ViewModel() {

    val cryptos = MutableStateFlow<List<CryptoItem>>(emptyList())
    val selectedCrypto = MutableStateFlow<CryptoItem?>(null)
    val availableModels = CryptoForecastRepository.availableModels
    val selectedModel = MutableStateFlow<ForecastModel>(availableModels.first())
    val selectedPeriod = MutableStateFlow<ForecastPeriod>(ForecastPeriod.SEVEN_DAYS)

    val forecastResult = MutableStateFlow<ForecastResult?>(null)
    val isLoading = MutableStateFlow<Boolean>(false)

    // Unlocked model ids via Rewarded Ads
    private val _unlockedModels = MutableStateFlow<Set<String>>(emptySet())
    val unlockedModels: StateFlow<Set<String>> = _unlockedModels

    val isVipUser: StateFlow<Boolean> = billingManager.isVipUser

    init {
        loadCryptos()
    }

    private fun loadCryptos() {
        val list = CryptoForecastRepository.getTopCryptos()
        cryptos.value = list
        if (list.isNotEmpty()) {
            selectedCrypto.value = list.first()
            triggerForecast()
        }
    }

    fun selectCrypto(crypto: CryptoItem) {
        selectedCrypto.value = crypto
        triggerForecast()
    }

    fun selectPeriod(period: ForecastPeriod) {
        selectedPeriod.value = period
        triggerForecast()
    }

    fun selectModel(model: ForecastModel, activity: Activity) {
        if (isModelAccessible(model)) {
            selectedModel.value = model
            triggerForecast()
        } else {
            // Need rewarded ad unlock
            if (model.accessType == AccessType.REWARDED_AD) {
                adMobManager.showRewardedAd(
                    activity = activity,
                    onUserEarnedReward = {
                        _unlockedModels.value = _unlockedModels.value + model.id
                        selectedModel.value = model
                        triggerForecast()
                    },
                    onAdDismissed = {}
                )
            }
        }
    }

    fun isModelAccessible(model: ForecastModel): Boolean {
        if (isVipUser.value) return true
        return when (model.accessType) {
            AccessType.FREE -> true
            AccessType.REWARDED_AD -> _unlockedModels.value.contains(model.id)
            AccessType.VIP_PURCHASE -> false
        }
    }

    fun triggerForecast() {
        val crypto = selectedCrypto.value ?: return
        val model = selectedModel.value
        val period = selectedPeriod.value

        isLoading.value = true
        viewModelScope.launch {
            val result = CryptoForecastRepository.calculateForecast(crypto, model, period)
            forecastResult.value = result
            isLoading.value = false
        }
    }

    fun toggleVipDemo() {
        billingManager.setVipStatusForDebug(!isVipUser.value)
    }
}
