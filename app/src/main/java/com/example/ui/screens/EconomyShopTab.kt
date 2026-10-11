package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameState
import com.example.data.model.ShopItem
import com.example.data.repository.GameRepository
import com.example.ui.pixel.RetroButton
import com.example.ui.pixel.RetroCard
import com.example.ui.theme.*

@Composable
fun EconomyShopTab(
    gameState: GameState,
    onTradeStock: (String, Int, Boolean) -> Unit,
    onDepositBank: (Long) -> Unit,
    onWithdrawBank: (Long) -> Unit,
    onBuyShopItem: (ShopItem) -> Unit,
    onUseItem: (String) -> Unit,
    onBuyProperty: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Bursa Saham") }
    val categories = listOf("Bursa Saham", "Toko & Hadiah", "Properti Rumah", "Tas & Inventori")
    val catalog = GameRepository.getShopCatalog()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        // Finance Summary Card
        RetroCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = PixelSurfaceVariant,
            borderColor = PixelMoneyColor
        ) {
            Text(
                text = "💰 KEUANGAN & SALDO ANDA",
                style = MaterialTheme.typography.titleSmall,
                color = PixelGold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Uang Tunai (Cash):", style = MaterialTheme.typography.labelSmall, color = PixelTextSecondary)
                    Text(
                        text = "Rp ${String.format("%,d", gameState.player.money)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = PixelMoneyColor
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Tabungan Bank (Bunga 0.1%/hr):", style = MaterialTheme.typography.labelSmall, color = PixelTextSecondary)
                    Text(
                        text = "Rp ${String.format("%,d", gameState.player.bankSavings)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = PixelCyan
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RetroButton(
                    onClick = { onDepositBank(500_000L) },
                    text = "Setor Rp 500rb",
                    buttonColor = PixelCyan,
                    textColor = Color.White,
                    modifier = Modifier.weight(1f),
                    testTag = "deposit_bank_btn"
                )
                RetroButton(
                    onClick = { onWithdrawBank(500_000L) },
                    text = "Tarik Rp 500rb",
                    buttonColor = PixelGold,
                    textColor = Color.Black,
                    modifier = Modifier.weight(1f),
                    testTag = "withdraw_bank_btn"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Switcher Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = cat == selectedCategory
                RetroButton(
                    onClick = { selectedCategory = cat },
                    text = cat,
                    buttonColor = if (isSelected) PixelGold else PixelSurfaceVariant,
                    textColor = if (isSelected) Color.Black else PixelTextSecondary,
                    modifier = Modifier.weight(1f),
                    testTag = "tab_${cat.replace(" ", "_")}"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedCategory) {
            "Bursa Saham" -> {
                Text(
                    text = "📈 PASAR MODAL & SAHAM DINAMIS (Fluktuasi Harian):",
                    style = MaterialTheme.typography.labelSmall,
                    color = PixelTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                gameState.stocks.forEach { stock ->
                    val isPositive = stock.changePercent >= 0
                    RetroCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        backgroundColor = PixelSurface,
                        borderColor = if (isPositive) PixelEmerald else PixelStressColor
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${stock.code} - ${stock.name}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = PixelTextPrimary
                                )
                                Text(
                                    text = "Harga: Rp ${String.format("%,d", stock.price)} / lembar",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PixelGold
                                )
                                Text(
                                    text = "Dimiliki: ${stock.ownedShares} lembar (Nilai: Rp ${String.format("%,d", stock.ownedShares * stock.price)})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PixelTextSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(if (isPositive) PixelEmerald else PixelStressColor)
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${if (isPositive) "+" else ""}${String.format("%.1f", stock.changePercent)}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RetroButton(
                                onClick = { onTradeStock(stock.code, 10, true) },
                                text = "Beli 10 Lembar",
                                buttonColor = PixelEmerald,
                                textColor = Color.Black,
                                modifier = Modifier.weight(1f),
                                testTag = "buy_stock_${stock.code}"
                            )
                            RetroButton(
                                onClick = { onTradeStock(stock.code, 10, false) },
                                text = "Jual 10 Lembar",
                                buttonColor = PixelStressColor,
                                textColor = Color.White,
                                enabled = stock.ownedShares >= 10,
                                modifier = Modifier.weight(1f),
                                testTag = "sell_stock_${stock.code}"
                            )
                        }
                    }
                }
            }

            "Toko & Hadiah" -> {
                Text(
                    text = "🏪 KATALOG BARANG & HADIAH SPESIAL:",
                    style = MaterialTheme.typography.labelSmall,
                    color = PixelTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                catalog.forEach { item ->
                    RetroCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        backgroundColor = PixelSurface,
                        borderColor = PixelBorderHighlight
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item.icon, fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = PixelTextPrimary
                                )
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PixelTextMuted
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Harga: Rp ${String.format("%,d", item.basePrice)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PixelMoneyColor
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            RetroButton(
                                onClick = { onBuyShopItem(item) },
                                text = "Beli",
                                buttonColor = PixelGold,
                                textColor = Color.Black,
                                testTag = "buy_item_${item.id}"
                            )
                        }
                    }
                }
            }

            "Properti Rumah" -> {
                Text(
                    text = "🏡 KEPEMILIKAN & SEWA PROPERTI:",
                    style = MaterialTheme.typography.labelSmall,
                    color = PixelTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                gameState.residences.forEach { res ->
                    val isCurrent = res.id == gameState.player.currentResidenceId
                    RetroCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        backgroundColor = if (isCurrent) PixelSurfaceVariant else PixelSurface,
                        borderColor = if (isCurrent) PixelGold else PixelBorderHighlight
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = res.icon, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = res.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (isCurrent) PixelGold else PixelTextPrimary
                                )
                                Text(
                                    text = if (res.isOwned) "Status: Properti Milik Sendiri (Bebas Sewa)" else "Biaya Sewa: Rp ${String.format("%,d", res.dailyRent)} / hari",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (res.isOwned) PixelEmerald else PixelTextSecondary
                                )
                                Text(
                                    text = "Kenyamanan Tidur: +${res.comfortBonus} Pemulihan Energi",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PixelGold
                                )
                            }

                            if (!res.isOwned && res.purchasePrice > 0) {
                                RetroButton(
                                    onClick = { onBuyProperty(res.id) },
                                    text = "Beli Hak Milik",
                                    buttonColor = PixelGold,
                                    textColor = Color.Black,
                                    testTag = "buy_property_${res.id}"
                                )
                            } else if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .background(PixelEmerald)
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(text = "DITEMPATI", style = MaterialTheme.typography.labelSmall, color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }

            "Tas & Inventori" -> {
                Text(
                    text = "🎒 ISI TAS ANDA SAAT INI:",
                    style = MaterialTheme.typography.labelSmall,
                    color = PixelTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                val inventoryList = gameState.player.inventory.filter { it.value > 0 }
                if (inventoryList.isEmpty()) {
                    RetroCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Tas masih kosong. Kunjungi Pasar Swalayan untuk berbelanja!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PixelTextMuted
                        )
                    }
                } else {
                    inventoryList.forEach { (itemId, count) ->
                        val item = catalog.find { it.id == itemId }
                        if (item != null) {
                            RetroCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = item.icon, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "${item.name} (x$count)",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = PixelTextPrimary
                                            )
                                            Text(
                                                text = item.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = PixelTextMuted
                                            )
                                        }
                                    }

                                    if (item.energyRestore > 0 || item.healthRestore > 0 || item.stressReduce > 0) {
                                        RetroButton(
                                            onClick = { onUseItem(itemId) },
                                            text = "Gunakan",
                                            buttonColor = PixelEmerald,
                                            textColor = Color.Black,
                                            testTag = "use_item_$itemId"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
