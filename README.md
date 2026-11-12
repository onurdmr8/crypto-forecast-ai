# CryptoForecast AI 🚀

Yapay zeka destekli kripto analiz ve tahmin Android uygulaması. (Jetpack Compose + AdMob Rewarded Ads + Google Play Billing)

## 📌 Özellikler

1. **Kripto Varlık Seçimi:**
   - BTC, ETH, SOL, BNB, XRP, ADA, AVAX gibi popüler varlıklar ve canlı fiyat/değişim takibi.

2. **Farklı Vade Seçenekleri (Forecast):**
   - 3 Günlük
   - 7 Günlük
   - 1 Aylık
   - 3 Aylık
   - 1 Yıllık

3. **Modeller & Gelir (Monetizasyon) Modeli:**
   - **Teknik Regresyon & Trend (Ücretsiz):** Basit hareketli ortalama ve trend analizi.
   - **Derin Öğrenme (LSTM Neural Net):** 🎬 *Ödüllü Reklam İzle & Aç!* (AdMob Rewarded Video izleyen kullanıcı analize anında erişir).
   - **Prophet AI & On-Chain (VIP):** 👑 *Google Play Abonelik / In-App Purchase* gerektirir. Balina hareketleri ve makro duygu analizi içerir.

4. **Görsel Tahmin Grafiği:**
   - Compose Canvas ile dinamik regresyon eğrisi, üst/alt güven bantları (confidence bands) ve hedef fiyat gösterimi.

5. **GitHub Actions CI/CD:**
   - Her `git push` sonrasında otomatik olarak Debug APK derlenir ve GitHub Actions sekmesinde indirilebilir hale gelir.

---

## 🛠️ Mimari & Kullanılan Teknolojiler

- **UI:** Jetpack Compose, Material 3, Dark Theme
- **Mimari:** MVVM (Model-View-ViewModel), StateFlow, Coroutines
- **Monetizasyon:**
  - `play-services-ads:23.0.0` (Google AdMob Rewarded Ads)
  - `billing-ktx:6.2.0` (Google Play In-App Billing)
- **Networking:** Retrofit 2, Gson, OkHttp
- **CI/CD:** GitHub Actions (Ubuntu, JDK 17, Gradle Wrapper)

---

## ⚠️ Yasal Uyarı (Disclaimer)
Bu uygulamadaki tüm tahmin ve analizler istatistiksel modeller ve makine öğrenimi simülasyonlarıdır. Kesinlikle yatırım tavsiyesi değildir.
