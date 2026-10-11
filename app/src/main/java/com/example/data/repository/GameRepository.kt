package com.example.data.repository

import android.content.Context
import com.example.data.local.GameDatabase
import com.example.data.local.GameSaveEntity
import com.example.data.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(context: Context) {
    private val dao = GameDatabase.getDatabase(context).gameSaveDao()
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val adapter = moshi.adapter(GameState::class.java)

    suspend fun saveGameState(state: GameState): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = adapter.toJson(state)
            val entity = GameSaveEntity(
                id = 1,
                savedTimestamp = System.currentTimeMillis(),
                playerName = state.player.name,
                day = state.day,
                careerTitle = getCareerTitle(state.player.currentCareerId, state.player.careerLevel),
                money = state.player.money,
                stateJson = json
            )
            dao.saveGame(entity)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loadGameState(): GameState? = withContext(Dispatchers.IO) {
        try {
            val entity = dao.getSavedGame() ?: return@withContext null
            adapter.fromJson(entity.stateJson)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun clearSavedGame(): Unit = withContext(Dispatchers.IO) {
        dao.clearSave()
    }

    companion object {
        fun getNpcDrawableRes(npcId: String): Int {
            return when (npcId) {
                "aoi", "citra" -> com.example.R.drawable.npc_citra_1791676955665
                "ren", "arka" -> com.example.R.drawable.npc_arka_1791676979253
                "reiko", "maya" -> com.example.R.drawable.npc_maya_1791677001860
                "kenji", "rayhan" -> com.example.R.drawable.npc_rayhan_1791677026431
                "ryo", "rio" -> com.example.R.drawable.npc_rio_1791677089449
                "yuki" -> com.example.R.drawable.npc_yuki_1791689317040
                "daiki" -> com.example.R.drawable.npc_daiki_1791689368017
                "sakura" -> com.example.R.drawable.npc_sakura_1791690306734
                "haruto" -> com.example.R.drawable.npc_haruto_1791690322409
                "kaede" -> com.example.R.drawable.npc_kaede_1791690343297
                else -> com.example.R.drawable.ic_pixel_life_1791636011395
            }
        }

        fun getFestivals(): List<CityFestival> = listOf(
            CityFestival(
                id = "festival_semi",
                name = "Pesta Bunga & Harmoni Akustik",
                season = Season.SEMI,
                startDayInSeason = 1,
                endDayInSeason = 7,
                locationId = "cafe",
                locationName = "Kafe & Taman Bunga",
                icon = "🌸",
                headline = "Kelopak Bunga Bermekaran di Bawah Alunan Melodi Akustik!",
                lore = "Festival tahunan menyambut musim semi di mana Aoi membagikan bibit bunga langka dan Ryo menggelar konser panggung terbuka di taman kota.",
                activities = listOf(
                    FestivalActivity(
                        id = "semi_bunga",
                        title = "Merangkai Buket Bunga Mawar Langka",
                        description = "Rangkai kelopak mawar mekar bersama Aoi di taman kafe.",
                        icon = "💐",
                        cost = 0L,
                        energyCost = 15,
                        happinessBonus = 30,
                        stressReduction = 20,
                        lovePointsBonus = 25,
                        rewardItemKey = "bunga_mawar",
                        partnerNpcId = "aoi"
                    ),
                    FestivalActivity(
                        id = "semi_konser",
                        title = "Konser Akustik Panggung Taman bareng Ryo",
                        description = "Petik senar gitar dan bernyanyi bersama Ryo di hadapan warga kota!",
                        icon = "🎸",
                        cost = 0L,
                        energyCost = 20,
                        happinessBonus = 35,
                        stressReduction = 30,
                        lovePointsBonus = 25,
                        rewardMoney = 400_000L,
                        partnerNpcId = "ryo"
                    ),
                    FestivalActivity(
                        id = "semi_kencan",
                        title = "Piknik Romantis Bunga Berguguran",
                        description = "Gelar tikar piknik di bawah pohon sakura bersama pasangan atau gebetan tercinta.",
                        icon = "🥂",
                        cost = 150_000L,
                        energyCost = 15,
                        happinessBonus = 45,
                        stressReduction = 30,
                        lovePointsBonus = 35,
                        requiresPartner = true
                    )
                )
            ),
            CityFestival(
                id = "festival_panas",
                name = "Festival Kembang Api Malam & Lampion Bintang",
                season = Season.PANAS,
                startDayInSeason = 8,
                endDayInSeason = 14,
                locationId = "hall",
                locationName = "Balai Kota & Alun-Alun Danau",
                icon = "🎆",
                headline = "Langit Malam Pikselia Menyala Terang dengan Ribuan Kembang Api!",
                lore = "Malam paling dinanti seluruh warga kota! Pasangan bergandengan tangan menatap langit, stan jajanan malam berjejer harum, dan lampion diterbangkan ke angkasa.",
                activities = listOf(
                    FestivalActivity(
                        id = "panas_kembang_api",
                        title = "Menatap Pesta Kembang Api Spektakuler",
                        description = "Kembang api warna-warni meledak megah di langit malam berbintang. Momen romantis tak terlupakan!",
                        icon = "🎆",
                        cost = 100_000L,
                        energyCost = 10,
                        happinessBonus = 50,
                        stressReduction = 45,
                        lovePointsBonus = 40,
                        requiresPartner = true
                    ),
                    FestivalActivity(
                        id = "panas_kuliner",
                        title = "Jelajah Stan Jajanan Pasar Malam",
                        description = "Cicipi takoyaki hangat gurih, es serut lampion manis, dan kudapan festival.",
                        icon = "🍡",
                        cost = 65_000L,
                        energyCost = 10,
                        happinessBonus = 30,
                        stressReduction = 20
                    ),
                    FestivalActivity(
                        id = "panas_lotre",
                        title = "Undian Lotre Lampion Keberuntungan",
                        description = "Beli tiket lotre festival, uji peruntunganmu untuk memenangkan hadiah uang tunai!",
                        icon = "🏮",
                        cost = 50_000L,
                        energyCost = 5,
                        happinessBonus = 30,
                        stressReduction = 10,
                        rewardMoney = 1_000_000L
                    )
                )
            ),
            CityFestival(
                id = "festival_gugur",
                name = "Pekan Raya Inovasi & Pasar Malam Rakyat",
                season = Season.GUGUR,
                startDayInSeason = 15,
                endDayInSeason = 21,
                locationId = "market",
                locationName = "Pasar Swalayan & Kawasan Bisnis",
                icon = "🍂",
                headline = "Diskon Akbar Bazar Rakyat dan Pameran Investasi Emas!",
                lore = "Musim gugur membawa berkah panen dan perputaran ekonomi pesat. Stan bazar menggelar promo besar-besaran dan seminar finansial dipadati pengunjung.",
                activities = listOf(
                    FestivalActivity(
                        id = "gugur_investasi",
                        title = "Masterclass Investasi bersama Reiko Fujiwara",
                        description = "Bahas tren portofolio pasar modal dan trik rahasia diversifikasi saham.",
                        icon = "📊",
                        cost = 100_000L,
                        energyCost = 15,
                        happinessBonus = 30,
                        stressReduction = 15,
                        lovePointsBonus = 25,
                        rewardMoney = 600_000L,
                        partnerNpcId = "reiko"
                    ),
                    FestivalActivity(
                        id = "gugur_diskon",
                        title = "Borong Paket Hadiah Diskon 50%",
                        description = "Beli kotak cokelat premium dan bingkisan festival dengan harga miring!",
                        icon = "🍫",
                        cost = 75_000L,
                        energyCost = 10,
                        happinessBonus = 30,
                        stressReduction = 20,
                        rewardItemKey = "cokelat_premium"
                    ),
                    FestivalActivity(
                        id = "gugur_lelang",
                        title = "Lelang Karya Seni Antik Pikselia",
                        description = "Ikuti pelelangan karya lukis dan perhiasan berharga untuk koleksimu.",
                        icon = "🖼️",
                        cost = 200_000L,
                        energyCost = 10,
                        happinessBonus = 35,
                        stressReduction = 15
                    )
                )
            ),
            CityFestival(
                id = "festival_dingin",
                name = "Winter Hackathon & Pekan Pemulihan Jiwa",
                season = Season.DINGIN,
                startDayInSeason = 22,
                endDayInSeason = 28,
                locationId = "campus",
                locationName = "Universitas & Klinik dr. Kenji",
                icon = "❄️",
                headline = "Tantangan Kreativitas Terbesar dan Kehangatan Jiwa di Musim Dingin!",
                lore = "Udara dingin menusuk bukan halangan! Ren menantang developer dalam Game Jam 24 jam, sementara dr. Kenji membuka klinik terapi pemulihan batin massal gratis.",
                activities = listOf(
                    FestivalActivity(
                        id = "dingin_hackathon",
                        title = "Game Jam 24 Jam bersama Ren Takahashi",
                        description = "Bikin prototipe game retro seru semalaman bareng Ren dan raih trofi juara!",
                        icon = "💻",
                        cost = 0L,
                        energyCost = 25,
                        happinessBonus = 40,
                        stressReduction = 0,
                        lovePointsBonus = 25,
                        rewardMoney = 1_500_000L,
                        partnerNpcId = "ren"
                    ),
                    FestivalActivity(
                        id = "dingin_terapi",
                        title = "Sesi Meditasi & Terapi Cahaya dr. Kenji",
                        description = "Klinik dibuka gratis! Mandi uap hangat dan meditasi menenangkan beban pikiran.",
                        icon = "🕯️",
                        cost = 0L,
                        energyCost = 0,
                        happinessBonus = 45,
                        stressReduction = 55,
                        lovePointsBonus = 25,
                        partnerNpcId = "kenji"
                    ),
                    FestivalActivity(
                        id = "dingin_kado",
                        title = "Tukar Kado Musim Dingin Rahasia Warga",
                        description = "Bungkus kado misterius dan terima konsol game retro dari sahabat kotamu!",
                        icon = "🎁",
                        cost = 80_000L,
                        energyCost = 10,
                        happinessBonus = 35,
                        stressReduction = 20,
                        rewardItemKey = "gadget_retro"
                    )
                )
            )
        )

        fun getCurrentFestival(day: Int): CityFestival {
            val dayOfYear = ((day - 1) % 28) + 1
            val festivals = getFestivals()
            return when (dayOfYear) {
                in 1..7 -> festivals[0]
                in 8..14 -> festivals[1]
                in 15..21 -> festivals[2]
                else -> festivals[3]
            }
        }

        fun getAvailableCareers(): List<CareerPath> = listOf(
            CareerPath(
                id = "tech",
                name = "Teknologi & Software",
                industry = "IT & Digital",
                icon = "💻",
                description = "Karier rekayasa perangkat lunak, coding, dan startup teknologi masa depan.",
                levels = listOf(
                    CareerLevel(1, "Intern Junior Developer", 180_000L, 15, 10, 10, 20, 15),
                    CareerLevel(2, "Fullstack Developer", 350_000L, 35, 20, 25, 25, 22),
                    CareerLevel(3, "Senior Tech Lead", 750_000L, 55, 40, 45, 30, 28),
                    CareerLevel(4, "VP of Engineering", 1_500_000L, 75, 60, 60, 35, 35),
                    CareerLevel(5, "CTO & Co-Founder Unicorn", 3_200_000L, 90, 80, 80, 40, 40)
                )
            ),
            CareerPath(
                id = "culinary",
                name = "Kuliner & Pengusaha Kafe",
                industry = "Hospitality & F&B",
                icon = "☕",
                description = "Mulai dari barista peracik kopi hingga pemilik jaringan bistro terkemuka.",
                levels = listOf(
                    CareerLevel(1, "Barista Paruh Waktu", 150_000L, 10, 20, 15, 20, 12),
                    CareerLevel(2, "Kepala Barista & Roaster", 280_000L, 25, 35, 30, 25, 18),
                    CareerLevel(3, "Manajer Kafe 'Pixel Beans'", 580_000L, 40, 50, 45, 30, 25),
                    CareerLevel(4, "Pemilik Artisan Bistro", 1_200_000L, 60, 70, 65, 35, 32),
                    CareerLevel(5, "Raja Franchise Restoran Mewah", 2_800_000L, 75, 90, 85, 38, 36)
                )
            ),
            CareerPath(
                id = "medical",
                name = "Kesehatan & Psikiatri",
                industry = "Kesehatan & Mental Care",
                icon = "🩺",
                description = "Mengobati fisik dan menyembuhkan luka batin masyarakat kota.",
                levels = listOf(
                    CareerLevel(1, "Asisten Medis Klinik", 200_000L, 25, 15, 10, 22, 18),
                    CareerLevel(2, "Perawat Klinis Senior", 380_000L, 40, 30, 20, 26, 25),
                    CareerLevel(3, "Dokter Umum Praktek", 800_000L, 60, 45, 35, 32, 30),
                    CareerLevel(4, "Spesialis Kesehatan Jiwa", 1_650_000L, 80, 65, 50, 35, 35),
                    CareerLevel(5, "Direktur Pusat Pemulihan Jiwa", 3_400_000L, 95, 85, 70, 38, 38)
                )
            ),
            CareerPath(
                id = "creative",
                name = "Seni & Industri Kreatif",
                industry = "Desain & Hiburan",
                icon = "🎨",
                description = "Mengekspresikan imajinasi melalui lukisan pixel, game art, dan pameran agung.",
                levels = listOf(
                    CareerLevel(1, "Ilustrator Lepas", 140_000L, 10, 15, 30, 18, 12),
                    CareerLevel(2, "Desainer Grafis Agensi", 300_000L, 20, 25, 50, 22, 18),
                    CareerLevel(3, "Art Director Studio", 650_000L, 35, 45, 70, 28, 24),
                    CareerLevel(4, "Kurator Galeri Nasional", 1_350_000L, 50, 65, 85, 32, 28),
                    CareerLevel(5, "Maestro Seni Kontemporer", 3_000_000L, 70, 85, 98, 35, 30)
                )
            ),
            CareerPath(
                id = "finance",
                name = "Finansial & Investasi",
                industry = "Perbankan & Saham",
                icon = "📊",
                description = "Mengelola portofolio modal, merger perusahaan, dan likuiditas moneter.",
                levels = listOf(
                    CareerLevel(1, "Staf Administrasi Bank", 190_000L, 20, 15, 10, 20, 15),
                    CareerLevel(2, "Analis Pasar Modal", 390_000L, 40, 30, 20, 26, 25),
                    CareerLevel(3, "Senior Wealth Advisor", 820_000L, 60, 55, 30, 32, 32),
                    CareerLevel(4, "Partner Investasi Modal Ventura", 1_700_000L, 80, 75, 50, 36, 38),
                    CareerLevel(5, "Managing Director Hedge Fund", 3_600_000L, 95, 90, 70, 40, 42)
                )
            )
        )

        fun getCareerTitle(careerId: String, level: Int): String {
            val career = getAvailableCareers().find { it.id == careerId } ?: return "Wiraswasta"
            val lvl = career.levels.find { it.level == level } ?: career.levels.firstOrNull()
            return lvl?.title ?: "Karyawan"
        }

        fun getInitialNpcs(): List<NPC> = listOf(
            NPC(
                id = "aoi",
                name = "Aoi Tachibana",
                role = "Seniman Florist & Pemilik Toko Bunga",
                age = 23,
                bio = "Gadis berhati lembut yang mencintai tanaman, estetika bunga sakura, dan aroma hujan sore.",
                defaultLocation = "cafe",
                heartPoints = 20,
                relationshipStage = RelationshipStage.KENALAN,
                favoriteGiftId = "bunga_mawar",
                avatarShirtColor = 0xFFE91E63,
                dialogueOptions = listOf(
                    "Bunga sakura dan mawar mekar sangat indah jika dirawat penuh kasih sayang.",
                    "Cuaca hari ini membuatku ingin melukis pemandangan kota di teras kafe.",
                    "Bagaimana perasaanmu hari ini, Koi? Jika lelah, jangan lupa istirahat sejenak."
                )
            ),
            NPC(
                id = "ren",
                name = "Ren Takahashi",
                role = "Game Developer & Tech Enthusiast",
                age = 24,
                bio = "Programmer cerdas yang menghabiskan hari menyusun kode game retro dan menyeruput latte.",
                defaultLocation = "campus",
                heartPoints = 15,
                relationshipStage = RelationshipStage.KENALAN,
                favoriteGiftId = "gadget_retro",
                avatarShirtColor = 0xFF2196F3,
                dialogueOptions = listOf(
                    "Aku baru saja menyelesaikan bug rendering pada engine pixel art buatanku!",
                    "Kopi hitam tanpa gula adalah bahan bakar terbaik untuk mengetik seribu baris kode.",
                    "Hidup itu seperti game RPG: status mental dan energimu harus selalu dijaga seimbang, Koi."
                )
            ),
            NPC(
                id = "reiko",
                name = "Reiko Fujiwara",
                role = "Konsultan Bisnis & Investor Muda",
                age = 25,
                bio = "Wanita karier berkelas dan dinamis yang lihai membaca pergerakan pasar saham dan peluang emas.",
                defaultLocation = "office",
                heartPoints = 15,
                relationshipStage = RelationshipStage.KENALAN,
                favoriteGiftId = "cokelat_premium",
                avatarShirtColor = 0xFF9C27B0,
                dialogueOptions = listOf(
                    "Pasar modal hari ini bergejolak. Saat orang lain panik, itulah waktu terbaik berinvestasi.",
                    "Karier impian tidak dibangun dalam semalam, tapi butuh disiplin dan visi jangka panjang.",
                    "Senang mengobrol denganmu di sela jadwalku yang padat, Koi. Kamu punya ambisi yang menarik."
                )
            ),
            NPC(
                id = "kenji",
                name = "dr. Kenji Morikawa",
                role = "Psikiater & Terapis Kesehatan Mental",
                age = 27,
                bio = "Dokter tenang dengan tutur kata lembut, berdedikasi mendengarkan dan memulihkan jiwa yang lelah.",
                defaultLocation = "hospital",
                heartPoints = 20,
                relationshipStage = RelationshipStage.KENALAN,
                favoriteGiftId = "buku_filsafat",
                avatarShirtColor = 0xFF009688,
                dialogueOptions = listOf(
                    "Tubuh dan pikiran saling terhubung. Jangan biarkan stres menumpuk tanpa ventilasi emosi.",
                    "Terkadang, langkah paling berani dalam hidup adalah berani berhenti sejenak untuk bernapas.",
                    "Klinik ini selalu terbuka jika kamu butuh ruang aman untuk bercerita tanpa dihakimi, Koi."
                )
            ),
            NPC(
                id = "ryo",
                name = "Ryo Yamato",
                role = "Musisi Jalanan & Barista Santai",
                age = 23,
                bio = "Pemetik gitar akustik berjiwa bebas yang percaya bahwa kebahagiaan sejati ada di hal-hal sederhana.",
                defaultLocation = "cafe",
                heartPoints = 20,
                relationshipStage = RelationshipStage.KENALAN,
                favoriteGiftId = "kopi_spesial",
                avatarShirtColor = 0xFFFF9800,
                dialogueOptions = listOf(
                    "Dengar ritme kota ini, kawan. Bahkan deru hujan punya melodi tersendiri jika kamu meresapinya.",
                    "Uang memang penting, tapi jangan sampai kamu lupa tertawa bersama orang yang kamu sayangi.",
                    "Malam nanti ada festival musik kecil di alun-alun kota. Mau datang bersamaku, Koi?"
                )
            ),
            NPC(
                id = "yuki",
                name = "Yuki Hoshino",
                role = "Pustakawati & Peneliti Sastra Klasik",
                age = 22,
                bio = "Gadis pemalu berkacamata yang mencintai buku tua, teh hangat, dan keindahan rasi bintang malam.",
                defaultLocation = "campus",
                heartPoints = 20,
                relationshipStage = RelationshipStage.KENALAN,
                favoriteGiftId = "teh_matcha",
                avatarShirtColor = 0xFF3F51B5,
                dialogueOptions = listOf(
                    "Setiap buku di perpustakaan ini menyimpan dunia rahasia yang menunggu untuk dibaca.",
                    "Membaca buku diiringi secangkir teh hangat adalah caraku menenangkan pikiran yang bising.",
                    "Aku melihatmu sering mampir ke kampus, Koi... Mau membaca bersama di sudut tenang ini?"
                )
            ),
            NPC(
                id = "daiki",
                name = "Daiki Sato",
                role = "Master Chef Kedai Ramen & Kuliner",
                age = 24,
                bio = "Pemuda periang dengan bandana hachimaki yang mendedikasikan hidupnya menyajikan semangkuk ramen terlezat di kota.",
                defaultLocation = "market",
                heartPoints = 20,
                relationshipStage = RelationshipStage.KENALAN,
                favoriteGiftId = "ramen_lezat",
                avatarShirtColor = 0xFFE53935,
                dialogueOptions = listOf(
                    "Rahasia semangkuk ramen sempurna adalah kaldu rebusan 12 jam dan cinta sang koki!",
                    "Perut yang kenyang dan hangat adalah kunci menghadapi hari yang dingin dan penuh tantangan!",
                    "Oi Koi! Nanti mampir ke kedai ya, aku buatkan ramen porsi spesial khusus untukmu!"
                )
            ),
            NPC(
                id = "sakura",
                name = "Sakura Shimizu",
                role = "Desainer Fashion & Model Kimono Modern",
                age = 22,
                bio = "Desainer muda bertalenta yang memadukan keanggunan motif sakura klasik dengan estetika busana modern kota.",
                defaultLocation = "market",
                heartPoints = 20,
                relationshipStage = RelationshipStage.KENALAN,
                favoriteGiftId = "parfum_sakura",
                avatarShirtColor = 0xFFE91E63,
                dialogueOptions = listOf(
                    "Gaya berbusana mencerminkan bagaimana kita menghargai diri kita sendiri dan orang lain.",
                    "Aku sedang merancang koleksi baru bermotif kelopak bunga musim semi. Bagaimana menurutmu, Koi?",
                    "Senyummu selalu ramah, Koi. Energi positif seperti itu yang membuat hari terasa lebih cerah!"
                )
            ),
            NPC(
                id = "haruto",
                name = "Haruto Kazama",
                role = "Pelatih Kendo & Instruktur Kebugaran",
                age = 25,
                bio = "Pemuda atletis berdisiplin tinggi dengan kepribadian hangat, selalu memotivasi warga untuk hidup sehat dan bersemangat.",
                defaultLocation = "hall",
                heartPoints = 20,
                relationshipStage = RelationshipStage.KENALAN,
                favoriteGiftId = "matras_kendo",
                avatarShirtColor = 0xFF1976D2,
                dialogueOptions = listOf(
                    "Disiplin bukan tentang membatasi diri, melainkan membebaskan potensi terbaik yang kita miliki!",
                    "Setelah berolahraga keras dan berkeringat, rasanya semua stres dan beban pikiran menguap.",
                    "Ayo lari pagi keliling taman bersamaku besok, Koi! Stamina yang kuat adalah pondasi karier sukses."
                )
            ),
            NPC(
                id = "kaede",
                name = "Kaede Minami",
                role = "Fotografer Jalanan & Jurnalis Kota",
                age = 23,
                bio = "Gadis periang bertopi baret yang berkeliling mengabadikan momen-momen hangat di setiap sudut Kota Pikselia.",
                defaultLocation = "cafe",
                heartPoints = 20,
                relationshipStage = RelationshipStage.KENALAN,
                favoriteGiftId = "kamera_roll",
                avatarShirtColor = 0xFF4CAF50,
                dialogueOptions = listOf(
                    "Kamera menangkap detik-detik berharga yang sering terlewatkan saat orang terburu-buru.",
                    "Pencahayaan di taman saat senja hari ini sungguh magis! Mau aku potret dirimu, Koi?",
                    "Kota Pikselia punya sejuta cerita unik, dan kisah perjalananmu adalah salah satu favoritku untuk diliput!"
                )
            )
        )

        fun getInitialResidences(): List<Residence> = listOf(
            Residence(
                id = "kost",
                name = "Kosan Petak Sederhana",
                type = "Kamar Kost",
                dailyRent = 50_000L,
                purchasePrice = 0L,
                isOwned = true, // Tempat awal pemain
                comfortBonus = 10,
                icon = "🚪"
            ),
            Residence(
                id = "apartemen",
                name = "Apartemen Studio Nyaman",
                type = "Apartemen Modern",
                dailyRent = 180_000L,
                purchasePrice = 45_000_000L,
                isOwned = false,
                comfortBonus = 25,
                icon = "🏢"
            ),
            Residence(
                id = "rumah_mewah",
                name = "Rumah Impian 2 Lantai & Taman",
                type = "Rumah Pribadi",
                dailyRent = 0L,
                purchasePrice = 180_000_000L,
                isOwned = false,
                comfortBonus = 50,
                icon = "🏡"
            )
        )

        fun getInitialStocks(): List<StockShare> = listOf(
            StockShare("PIXTECH", "Pixel Tech Indo", 4_500L, +1.8f, 0),
            StockShare("COZYFOOD", "Cozy Nusantara F&B", 2_200L, -0.6f, 0),
            StockShare("MEDIPHARMA", "Medika Farma Sehat", 3_800L, +3.2f, 0)
        )

        fun getShopCatalog(): List<ShopItem> = listOf(
            ShopItem("kopi", "Kopi Hitam Mantap", "Kebutuhan", 25_000L, "Kopi nikmat untuk memulihkan energi seketika.", "☕", 0, 30, -5, 0),
            ShopItem("nasi_kotak", "Nasi Bento Lengkap", "Kebutuhan", 35_000L, "Makanan bento bergizi mengenyangkan perut lapar.", "🍱", 0, 45, -5, 10),
            ShopItem("ramen_lezat", "Mangkuk Ramen Tradisional", "Kebutuhan", 45_000L, "Ramen kuah lezat buatan Daiki Sato.", "🍜", 25, 50, -10, 15),
            ShopItem("vitamin", "Multivitamin & Madu", "Kesehatan", 60_000L, "Meningkatkan daya tahan tubuh dari cuaca buruk.", "💊", 0, 15, -5, 25),
            ShopItem("payung", "Payung Pixel Pelindung", "Kebutuhan", 80_000L, "Melindungi dari hujan deras dan badai petir.", "☂️", 0, 0, 0, 0),
            ShopItem("bunga_mawar", "Buket Bunga Mawar & Sakura", "Hadiah", 120_000L, "Hadiah romantis kesukaan Aoi Tachibana.", "🌹", 20, 0, -10, 0),
            ShopItem("cokelat_premium", "Kotak Cokelat Premium", "Hadiah", 150_000L, "Cokelat lezat kesukaan Reiko Fujiwara.", "🍫", 20, 10, -15, 0),
            ShopItem("gadget_retro", "Konsol Mini Game Retro", "Hadiah", 280_000L, "Konsol game saku kesukaan Ren Takahashi.", "🎮", 25, 0, -25, 0),
            ShopItem("buku_filsafat", "Buku Filsafat & Kehidupan", "Hadiah", 110_000L, "Buku berbobot kesukaan dr. Kenji & Yuki.", "📚", 20, 0, -10, 0),
            ShopItem("teh_matcha", "Kotak Teh Hijau Matcha Asli", "Hadiah", 95_000L, "Teh aromatik Jepang kesukaan Yuki Hoshino.", "🍵", 25, 15, -15, 0),
            ShopItem("kopi_spesial", "Biji Kopi Sangrai Eksklusif", "Hadiah", 130_000L, "Biji kopi aromatik kesukaan Ryo Yamato.", "🫘", 20, 10, -10, 0),
            ShopItem("parfum_sakura", "Parfum Sakura & Sutra", "Hadiah", 160_000L, "Parfum elegan kesukaan Sakura Shimizu.", "🌸", 25, 0, -15, 0),
            ShopItem("matras_kendo", "Handuk Olahraga & Suplemen", "Hadiah", 105_000L, "Perlengkapan latihan kesukaan Haruto Kazama.", "🥋", 20, 15, -10, 5),
            ShopItem("kamera_roll", "Roll Film Kamera Klasik", "Hadiah", 140_000L, "Roll film analog kesukaan Kaede Minami.", "📷", 25, 0, -15, 0),
            ShopItem("cincin_lamaran", "Cincin Berlian Lamaran", "Spesial", 15_000_000L, "Cincin impian untuk melamar kekasih pujaan hatimu!", "💍", 60, 0, -30, 0)
        )

        fun getDilemmas(): List<NarrativeDilemma> = listOf(
            NarrativeDilemma(
                id = "dilemma_lembur",
                title = "Lembur Proyek Mendesak vs Kencan Romantis",
                description = "Malam ini atasanmu menuntut revisi aplikasi mendadak dengan imbalan bonus besar. Di saat yang sama, gebetan/pasanganmu sudah menunggumu untuk makan malam romantis di kafe.",
                category = "Karier & Asmara",
                choices = listOf(
                    NarrativeChoice(
                        id = "pilih_lembur",
                        text = "Prioritaskan Lembur Kantor",
                        statImpactDesc = "+Uang Rp 600.000, +Stres 25, -Hubungan dengan Pasangan",
                        moneyChange = 600_000L,
                        stressChange = 25,
                        happinessChange = -15,
                        relationshipChange = -12,
                        careerPerfChange = +15,
                        outcomeStory = "Kamu begadang di kantor hingga larut. Proyek berhasil rampung dan bos memberimu bonus, namun pasanganmu mengirim pesan bernada kecewa."
                    ),
                    NarrativeChoice(
                        id = "pilih_kencan",
                        text = "Tinggalkan Kantor & Temui Pasangan",
                        statImpactDesc = "+Kebahagiaan 25, +Hubungan +18, -Stres 15",
                        moneyChange = 0L,
                        stressChange = -15,
                        happinessChange = 25,
                        relationshipChange = +18,
                        careerPerfChange = -5,
                        outcomeStory = "Kamu memilih menepati janji. Senyuman hangat di wajah pasanganmu menghapus segala kepenatan kerja. Hubungan kalian semakin rekat!"
                    ),
                    NarrativeChoice(
                        id = "pilih_kompromi",
                        text = "Ajak Pasangan Mengopi Bersama Sambil Kamu Bekerja",
                        statImpactDesc = "+Uang Rp 300.000, +Karisma, Hubungan Netral",
                        moneyChange = 300_000L,
                        stressChange = 10,
                        happinessChange = 10,
                        relationshipChange = +8,
                        careerPerfChange = +8,
                        outcomeStory = "Dengan gaya persuasif, kamu mengundang pasanganmu ngopi di coworking kafe. Sambil kamu mengetik, kalian bercengkrama hangat."
                    )
                )
            ),
            NarrativeDilemma(
                id = "dilemma_kucing",
                title = "Kucing Liar Kehujanan di Gang Kota",
                description = "Saat pulang di bawah langit mendung berangin kencang, kamu mendengar suara anak kucing mengeong kedinginan di bawah kardus basah.",
                category = "Moral & Empati",
                choices = listOf(
                    NarrativeChoice(
                        id = "adopsi_kucing",
                        text = "Bawa Pulang & Beri Makanan",
                        statImpactDesc = "-Uang Rp 50.000 (beli pakan), +Kebahagiaan 20, -Stres 20",
                        moneyChange = -50_000L,
                        stressChange = -20,
                        happinessChange = 20,
                        relationshipChange = +5,
                        careerPerfChange = 0,
                        outcomeStory = "Kamu mengeringkan bulunya dan memberinya makan di kosan. Dengkuran manjanya membawa kedamaian batin luar biasa di hatimu."
                    ),
                    NarrativeChoice(
                        id = "bawa_ke_klinik",
                        text = "Antar ke Klinik dr. Kenji",
                        statImpactDesc = "-Uang Rp 120.000, Hubungan dengan dr. Kenji & Aoi +15",
                        moneyChange = -120_000L,
                        stressChange = -10,
                        happinessChange = 15,
                        relationshipNpcId = "kenji",
                        relationshipChange = 15,
                        careerPerfChange = 0,
                        outcomeStory = "dr. Kenji dan Aoi yang kebetulan ada di sana sangat tersentuh melihat ketulusan hatimu. Reputasimu di kota semakin harum!"
                    ),
                    NarrativeChoice(
                        id = "abaikan",
                        text = "Lewati Saja Karena Terburu-buru",
                        statImpactDesc = "Tidak ada perubahan biaya, Sedikit rasa bersalah",
                        moneyChange = 0L,
                        stressChange = 5,
                        happinessChange = -5,
                        relationshipChange = 0,
                        careerPerfChange = 0,
                        outcomeStory = "Kamu melangkah cepat menuju rumah. Rasa gundah sempat terlintas, namun kamu meyakinkan dirimu bahwa hidup memang harus fokus."
                    )
                )
            ),
            NarrativeDilemma(
                id = "dilemma_saham_bocor",
                title = "Bisikan Bocoran Saham 'Orang Dalam'",
                description = "Seorang kenalan di kawasan bisnis membocorkan bahwa saham PIXTECH akan melompat besok pagi karena merger rahasia. Apakah kamu ingin bertaruh spekulasi besar?",
                category = "Finansial & Integritas",
                choices = listOf(
                    NarrativeChoice(
                        id = "spekulasi_penuh",
                        text = "Pertaruhkan Tabunganmu (Risiko Tinggi)",
                        statImpactDesc = "Bisa untung besar Rp 1.500.000 atau rugi jika berita palsu, +Stres 20",
                        moneyChange = 1_200_000L,
                        stressChange = 20,
                        happinessChange = 15,
                        relationshipChange = 0,
                        careerPerfChange = 0,
                        outcomeStory = "Rumor tersebut terbukti separuh benar! Nilai saham melonjak dan kamu berhasil meraup cuan kilat, meski jantungmu sempat berdebar kencang."
                    ),
                    NarrativeChoice(
                        id = "investasi_bijak",
                        text = "Tolak Spekulasi, Tetap Berinvestasi Sehat",
                        statImpactDesc = "Ketenangan batin, Tabungan aman, -Stres 10",
                        moneyChange = 0L,
                        stressChange = -10,
                        happinessChange = 10,
                        relationshipChange = 0,
                        careerPerfChange = 5,
                        outcomeStory = "Kamu memilih integritas dan tidur nyenyak malam ini. Kamu tahu kekayaan sejati dibangun lewat konsistensi, bukan jalan pintas."
                    )
                )
            )
        )

        fun createInitialGameState(): GameState {
            val initialNpcs = getInitialNpcs()
            val initialStocks = getInitialStocks()
            val initialResidences = getInitialResidences()
            val initialStory = listOf(
                StoryLog(
                    day = 1,
                    timeLabel = "Pagi",
                    eventText = "Koi tiba di Kota Pikselia. Tinggal di kosan petak sederhana, siap mengejar karier dan cinta sejati!",
                    tag = "Awal Kisah"
                )
            )

            return GameState(
                day = 1,
                timeOfDay = TimeOfDay.PAGI,
                weather = WeatherType.CERAH,
                inflationRate = 0.04f,
                currentLocationId = "home",
                player = PlayerStats(
                    name = "Koi",
                    avatar = PlayerAvatar(0, 0, 0, 1, 0),
                    age = 22,
                    money = 2_500_000L,
                    bankSavings = 4_000_000L,
                    energy = 100,
                    health = 95,
                    stress = 15,
                    happiness = 85,
                    intelligence = 20,
                    charisma = 20,
                    creativity = 20,
                    currentCareerId = "tech",
                    careerLevel = 1,
                    currentResidenceId = "kost",
                    spouseNpcId = null,
                    hasUmbrella = true,
                    inventory = mapOf("kopi" to 2, "vitamin" to 1)
                ),
                npcs = initialNpcs,
                stocks = initialStocks,
                residences = initialResidences,
                activeDilemma = null,
                storyLogs = initialStory,
                crtScanlineEnabled = true,
                currentBannerMessage = "Hari 1: Langit cerah menyapa Kota Pikselia. Selamat berpetualang, Koi!"
            )
        }
    }
}
