package com.crypto.forecast.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crypto.forecast.data.model.*
import com.crypto.forecast.ui.components.ForecastChart
import com.crypto.forecast.ui.theme.*
import com.crypto.forecast.ui.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity

    val cryptos by viewModel.cryptos.collectAsState()
    val selectedCrypto by viewModel.selectedCrypto.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val forecastResult by viewModel.forecastResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isVipUser by viewModel.isVipUser.collectAsState()

    var showVipDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CryptoForecast AI",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Yapay Zeka Destekli Kripto Tahmin",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { showVipDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isVipUser) GoldVip else AccentPurple
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "VIP",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isVipUser) "PRO AKTİF" else "PRO'YA GEÇ",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // 1. Kripto Para Seçim Listesi (BTC, ETH, SOL vb.)
            Text(
                text = "Kripto Varlık Seçin",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(cryptos) { crypto ->
                    val isSelected = crypto.id == selectedCrypto?.id
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) AccentPurple.copy(alpha = 0.25f) else CardBackground,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) AccentPurple else CardBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectCrypto(crypto) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Column {
                            Text(
                                text = crypto.symbol,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "$${String.format("%.2f", crypto.currentPriceUsd)}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            val changeColor = if (crypto.change24hPercent >= 0) BullGreen else BearRed
                            val changeSign = if (crypto.change24hPercent >= 0) "+" else ""
                            Text(
                                text = "$changeSign${crypto.change24hPercent}%",
                                fontSize = 11.sp,
                                color = changeColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Tahmin Periyodu Seçimi (3G, 7G, 1A, 3A, 1Y)
            Text(
                text = "Tahmin Vadesi",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ForecastPeriod.values().forEach { period ->
                    val isSelected = period == selectedPeriod
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) AccentCyan.copy(alpha = 0.2f) else CardBackground,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) AccentCyan else CardBorder,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.selectPeriod(period) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = period.label,
                            color = if (isSelected) AccentCyan else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Tahmin Modelleri (Ücretsiz, Reklamlı, VIP)
            Text(
                text = "Yapay Zeka & Analiz Modeli",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                viewModel.availableModels.forEach { model ->
                    val isSelected = model.id == selectedModel.id
                    val isAccessible = viewModel.isModelAccessible(model)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (model.accessType == AccessType.VIP_PURCHASE && !isAccessible) {
                                    showVipDialog = true
                                } else if (activity != null) {
                                    viewModel.selectModel(model, activity)
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) CardBackground else Color(0xFF13171F)
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (isSelected) AccentPurple else CardBorder
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = model.name,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = model.accuracyScore,
                                        color = BullGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = model.description,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }

                            // Model Durumu Rozeti
                            when {
                                isAccessible -> {
                                    Text(
                                        text = if (isSelected) "Seçili" else "Kullan",
                                        color = if (isSelected) AccentPurple else TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                model.accessType == AccessType.REWARDED_AD -> {
                                    Button(
                                        onClick = { activity?.let { viewModel.selectModel(model, it) } },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "İzle",
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Reklamla Aç", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                model.accessType == AccessType.VIP_PURCHASE -> {
                                    Button(
                                        onClick = { showVipDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldVip),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "VIP",
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "PRO", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Tahmin Grafiği ve Sonuç Kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorder)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = AccentCyan)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "AI Modeli hesaplanıyor...",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else if (forecastResult != null) {
                        val res = forecastResult!!
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "${selectedCrypto?.symbol} Hedef Fiyat (${res.period.label})",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "$${String.format("%.2f", res.targetPrice)}",
                                    color = TextPrimary,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                val changeColor = if (res.expectedChangePercent >= 0) BullGreen else BearRed
                                val changeSign = if (res.expectedChangePercent >= 0) "+" else ""
                                Text(
                                    text = "$changeSign${String.format("%.2f", res.expectedChangePercent)}%",
                                    color = changeColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = res.sentiment,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Grafik Çizimi
                        ForecastChart(
                            points = res.points,
                            lineColor = if (res.expectedChangePercent >= 0) BullGreen else BearRed
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "⚠️ Bu tahminler yatırım tavsiyesi değildir. İstatistiksel simülasyon ve makine öğrenimi modellerine dayanır.",
                            fontSize = 10.sp,
                            color = TextSecondary.copy(alpha = 0.7f),
                            lineHeight = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // VIP Abonelik / Satın Alma Dialogu
    if (showVipDialog) {
        AlertDialog(
            onDismissRequest = { showVipDialog = false },
            containerColor = CardBackground,
            title = {
                Text(
                    text = "👑 PRO Avantajları",
                    color = GoldVip,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "• Tüm Reklamlar Kaldırılır\n• Prophet & Whale On-Chain Modeline Sınırsız Erişim\n• 1 Yıllık Uzun Vade Tahminler\n• Anlık Balina Alım/Satım Sinyalleri",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aylık: 149.99 TL / ay",
                        color = AccentCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.toggleVipDemo()
                        showVipDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldVip)
                ) {
                    Text(
                        text = if (isVipUser) "Aboneliği İptal Et (Test)" else "Abone Ol (Test Et)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showVipDialog = false }) {
                    Text(text = "Kapat", color = TextSecondary)
                }
            }
        )
    }
}
