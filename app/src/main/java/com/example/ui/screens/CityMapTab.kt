package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.GameState
import com.example.data.repository.GameRepository
import com.example.ui.pixel.RetroButton
import com.example.ui.pixel.RetroCard
import com.example.ui.theme.*

data class DistrictInfo(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val primaryActivities: List<String>,
    val borderColor: Color
)

@Composable
fun CityMapTab(
    gameState: GameState,
    onTravelTo: (String) -> Unit,
    onQuickAction: (String) -> Unit,
    onOpenNpcModal: (String) -> Unit
) {
    val districts = listOf(
        DistrictInfo(
            id = "home",
            name = "Kosan & Hunian Pribadi",
            icon = "🏠",
            description = "Tempat tinggalmu untuk tidur, memulihkan energi, dan melepas lelah bermain game retro.",
            primaryActivities = listOf("Tidur ke Hari Berikutnya", "Main Game Retro", "Bersantai"),
            borderColor = PixelEmerald
        ),
        DistrictInfo(
            id = "office",
            name = "Kawasan Bisnis & Perkantoran",
            icon = "🏢",
            description = "Pusat karier profesional, kantor kerja, bursa saham, dan tempat mengejar promosi.",
            primaryActivities = listOf("Bekerja Shift Normal", "Lembur Proyek", "Minta Promosi"),
            borderColor = PixelCyan
        ),
        DistrictInfo(
            id = "cafe",
            name = "Kafe 'Pixel Beans' & Taman",
            icon = "☕",
            description = "Pusat sosialita dan relaksasi. Tempat nongkrong Aoi, Kaede, dan musisi jalanan Ryo.",
            primaryActivities = listOf("Ngopi Santai", "Temui Aoi & Kaede", "Meditasi di Taman"),
            borderColor = PixelOrange
        ),
        DistrictInfo(
            id = "hospital",
            name = "Klinik Pemulihan & Jiwa",
            icon = "🏥",
            description = "Pusat kesehatan dan klinik konseling jiwa dipimpin oleh dr. Kenji Morikawa.",
            primaryActivities = listOf("Konseling Terapi Jiwa", "Periksa Kesehatan", "Beli Vitamin"),
            borderColor = PixelPink
        ),
        DistrictInfo(
            id = "campus",
            name = "Universitas & Akademi Pelatihan",
            icon = "🎓",
            description = "Tingkatkan logika coding, kepemimpinan bisnis, dan keahlian seni untuk promosi.",
            primaryActivities = listOf("Kursus Coding IT", "Kelas Bisnis", "Workshop Seni"),
            borderColor = PixelPurple
        ),
        DistrictInfo(
            id = "market",
            name = "Pasar Swalayan & Butik Hadiah",
            icon = "🏪",
            description = "Toko serba ada menjual makanan, kopi instan, payung cuaca, hingga cincin lamaran.",
            primaryActivities = listOf("Beli Makanan", "Beli Payung", "Beli Cincin Lamaran"),
            borderColor = PixelGold
        ),
        DistrictInfo(
            id = "hall",
            name = "Balai Pernikahan & Kota",
            icon = "💒",
            description = "Tempat melangsungkan janji suci pernikahan bersama kekasih impianmu.",
            primaryActivities = listOf("Gelar Resepsi Pernikahan", "Lihat Catatan Sipil"),
            borderColor = Color(0xFFFF4081)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        // Pixel City Panorama Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .border(2.dp, PixelGold)
        ) {
            Image(
                painter = painterResource(id = R.drawable.pixel_city_banner_1791636035158),
                contentDescription = "Pemandangan Kota Pikselia",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(PixelDarkBg.copy(alpha = 0.85f))
                    .border(1.dp, PixelGold)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🗺️ PETA TERBUKA KOTA PIKSELIA",
                    style = MaterialTheme.typography.labelSmall,
                    color = PixelGold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Current Location Banner
        val currentLoc = districts.find { it.id == gameState.currentLocationId } ?: districts.first()
        RetroCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = PixelSurfaceVariant,
            borderColor = currentLoc.borderColor
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currentLoc.icon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LOKASI SAAT INI: ${currentLoc.name.uppercase()}",
                        style = MaterialTheme.typography.titleSmall,
                        color = PixelGold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentLoc.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = PixelTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "DISTRIK KOTA YANG BISA DIKUNJUNGI:",
            style = MaterialTheme.typography.labelSmall,
            color = PixelTextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Districts list
        districts.forEach { district ->
            val isHere = district.id == gameState.currentLocationId
            RetroCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                backgroundColor = if (isHere) PixelSurfaceVariant else PixelSurface,
                borderColor = if (isHere) PixelGold else PixelBorderHighlight
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = district.icon, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = district.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = if (isHere) PixelGold else PixelTextPrimary
                            )
                        }

                        if (isHere) {
                            Box(
                                modifier = Modifier
                                    .background(PixelEmerald)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "KAMU DI SINI",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Black
                                )
                            }
                        } else {
                            RetroButton(
                                onClick = { onTravelTo(district.id) },
                                text = "Kunjungi",
                                buttonColor = district.borderColor,
                                textColor = Color.Black,
                                testTag = "travel_to_${district.id}"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = district.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = PixelTextMuted
                    )

                    // NPCs present in this district
                    val npcsHere = gameState.npcs.filter {
                        if (it.id == gameState.player.spouseNpcId && district.id == "home") true
                        else it.defaultLocation == district.id
                    }
                    if (npcsHere.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Warga:",
                                style = MaterialTheme.typography.labelSmall,
                                color = PixelTextSecondary
                            )
                            npcsHere.forEach { npc ->
                                Row(
                                    modifier = Modifier
                                        .clickable { onOpenNpcModal(npc.id) }
                                        .background(PixelSurfaceVariant)
                                        .border(1.dp, Color(npc.avatarShirtColor))
                                        .padding(horizontal = 5.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = GameRepository.getNpcDrawableRes(npc.id)),
                                        contentDescription = npc.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = npc.name.split(" ").first(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PixelTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Quick activity buttons if player is currently in this district
                    if (isHere) {
                        Text(
                            text = "Aktivitas Tersedia:",
                            style = MaterialTheme.typography.labelSmall,
                            color = PixelEmerald
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            when (district.id) {
                                "home" -> {
                                    RetroButton(
                                        onClick = { onQuickAction("tidur") },
                                        text = "Tidur",
                                        icon = "🛏️",
                                        buttonColor = PixelEmerald,
                                        textColor = Color.Black,
                                        modifier = Modifier.weight(1f),
                                        testTag = "action_sleep"
                                    )
                                    RetroButton(
                                        onClick = { onQuickAction("game_retro") },
                                        text = "Main Game",
                                        icon = "🕹️",
                                        buttonColor = PixelCyan,
                                        textColor = Color.White,
                                        modifier = Modifier.weight(1f),
                                        testTag = "action_retro_game"
                                    )
                                }
                                "office" -> {
                                    RetroButton(
                                        onClick = { onQuickAction("kerja_shift") },
                                        text = "Bekerja",
                                        icon = "💼",
                                        buttonColor = PixelGold,
                                        textColor = Color.Black,
                                        modifier = Modifier.weight(1f),
                                        testTag = "action_work"
                                    )
                                    RetroButton(
                                        onClick = { onQuickAction("kerja_lembur") },
                                        text = "Lembur",
                                        icon = "⚡",
                                        buttonColor = PixelOrange,
                                        textColor = Color.Black,
                                        modifier = Modifier.weight(1f),
                                        testTag = "action_overtime"
                                    )
                                }
                                "cafe" -> {
                                    RetroButton(
                                        onClick = { onQuickAction("ngopi") },
                                        text = "Ngopi Santai",
                                        icon = "☕",
                                        buttonColor = PixelOrange,
                                        textColor = Color.Black,
                                        modifier = Modifier.weight(1f),
                                        testTag = "action_coffee"
                                    )
                                    RetroButton(
                                        onClick = { onQuickAction("meditasi") },
                                        text = "Meditasi Taman",
                                        icon = "🌸",
                                        buttonColor = PixelEmerald,
                                        textColor = Color.Black,
                                        modifier = Modifier.weight(1f),
                                        testTag = "action_meditate"
                                    )
                                }
                                "hospital" -> {
                                    RetroButton(
                                        onClick = { onQuickAction("terapi_jiwa") },
                                        text = "Terapi Jiwa",
                                        icon = "🧠",
                                        buttonColor = PixelPink,
                                        textColor = Color.White,
                                        modifier = Modifier.weight(1f),
                                        testTag = "action_therapy"
                                    )
                                }
                                "campus" -> {
                                    RetroButton(
                                        onClick = { onQuickAction("kursus_coding") },
                                        text = "Kursus IT",
                                        icon = "💻",
                                        buttonColor = PixelCyan,
                                        textColor = Color.White,
                                        modifier = Modifier.weight(1f),
                                        testTag = "action_course_it"
                                    )
                                    RetroButton(
                                        onClick = { onQuickAction("kursus_bisnis") },
                                        text = "Kursus Bisnis",
                                        icon = "📊",
                                        buttonColor = PixelGold,
                                        textColor = Color.Black,
                                        modifier = Modifier.weight(1f),
                                        testTag = "action_course_biz"
                                    )
                                }
                                "market" -> {
                                    RetroButton(
                                        onClick = { onQuickAction("buka_pasar") },
                                        text = "Buka Toko",
                                        icon = "🛍️",
                                        buttonColor = PixelGold,
                                        textColor = Color.Black,
                                        modifier = Modifier.weight(1f),
                                        testTag = "action_open_shop"
                                    )
                                }
                                "hall" -> {
                                    RetroButton(
                                        onClick = { onQuickAction("pernikahan") },
                                        text = "Periksa Syarat Nikah",
                                        icon = "💍",
                                        buttonColor = PixelPink,
                                        textColor = Color.White,
                                        modifier = Modifier.weight(1f),
                                        testTag = "action_wedding_check"
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
