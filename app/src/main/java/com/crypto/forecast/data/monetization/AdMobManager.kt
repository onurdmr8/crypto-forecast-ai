package com.crypto.forecast.data.monetization

import android.app.Activity
import android.content.Context
import android.util.Log
import com.crypto.forecast.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdMobManager(private val context: Context) {
    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    private val adUnitId: String
        get() = context.getString(R.string.admob_rewarded_ad_id)

    init {
        loadRewardedAd()
    }

    fun loadRewardedAd(onLoaded: (() -> Unit)? = null) {
        if (isLoading || rewardedAd != null) return
        isLoading = true

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e("AdMobManager", "Failed to load rewarded ad: ${loadAdError.message}")
                    rewardedAd = null
                    isLoading = false
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d("AdMobManager", "Rewarded ad loaded successfully")
                    rewardedAd = ad
                    isLoading = false
                    onLoaded?.invoke()
                }
            }
        )
    }

    fun isAdAvailable(): Boolean = rewardedAd != null

    fun showRewardedAd(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onAdDismissed: () -> Unit
    ) {
        if (rewardedAd != null) {
            rewardedAd?.show(activity) { rewardItem ->
                Log.d("AdMobManager", "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onUserEarnedReward()
            }
            rewardedAd = null
            loadRewardedAd() // preload next ad
            onAdDismissed()
        } else {
            loadRewardedAd()
            onAdDismissed()
        }
    }
}
