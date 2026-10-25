package id.seismova.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF0B1728)
private val Ocean = Color(0xFF123B4A)
private val Teal = Color(0xFF42D6C3)
private val Mist = Color(0xFFF3F7F8)
private val Ink = Color(0xFF142333)
private val Muted = Color(0xFF617584)
private val Coral = Color(0xFFFF8A65)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("seismova", MODE_PRIVATE)
        setContent {
            val checked = remember { mutableStateListOf<String>().apply {
                addAll(prefs.getStringSet("checked_items", emptySet()) ?: emptySet())
            } }
            var page by rememberSaveable { mutableStateOf("home") }
            var completedLessons by rememberSaveable { mutableIntStateOf(prefs.getInt("lessons", 0)) }
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Ocean, onPrimary = Color.White, secondary = Teal,
                    background = Mist, surface = Color.White, onSurface = Ink
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = Mist) {
                    Scaffold(
                        containerColor = Mist,
                        bottomBar = {
                            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                                val items = listOf(
                                    Triple("home", "Beranda", Icons.Default.Home),
                                    Triple("learn", "Belajar", Icons.Default.MenuBook),
                                    Triple("simulate", "Simulasi", Icons.Default.Explore),
                                    Triple("profile", "Progres", Icons.Default.Person)
                                )
                                items.forEach { (key, label, icon) ->
                                    NavigationBarItem(
                                        selected = page == key,
                                        onClick = { page = key },
                                        icon = { Icon(icon, contentDescription = label) },
                                        label = { Text(label, fontSize = 11.sp) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Ocean,
                                            selectedTextColor = Ocean,
                                            indicatorColor = Teal.copy(alpha = .22f)
                                        )
                                    )
                                }
                            }
                        }
                    ) { padding ->
                        Box(Modifier.padding(padding)) {
                            when (page) {
                                "home" -> HomeScreen(
                                    checkedCount = checked.size,
                                    onNavigate = { page = it }
                                )
                                "learn" -> LearnScreen(
                                    completedLessons = completedLessons,
                                    onComplete = {
                                        completedLessons = (completedLessons + 1).coerceAtMost(5)
                                        prefs.edit().putInt("lessons", completedLessons).apply()
                                    }
                                )
                                "simulate" -> SimulationScreen()
                                "kit" -> KitScreen(
                                    checked = checked,
                                    onToggle = { item, value ->
                                        if (value) checked.add(item) else checked.remove(item)
                                        prefs.edit().putStringSet("checked_items", checked.toSet()).apply()
                                    }
                                )
                                "guide" -> GuideScreen()
                                "local" -> LocalScreen()
                                "profile" -> ProgressScreen(
                                    checkedCount = checked.size,
                                    lessons = completedLessons,
                                    onNavigate = { page = it }
                                )
                                else -> HomeScreen(checked.size) { page = it }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(checkedCount: Int, onNavigate: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(46.dp).clip(RoundedCornerShape(15.dp))
                        .background(Ocean), contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Waves, null, tint = Teal, modifier = Modifier.size(27.dp)) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("SEISMOVA", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Navy, letterSpacing = 1.sp)
                    Text("TSUNAMI PREPAREDNESS", fontSize = 9.sp, color = Muted, letterSpacing = 1.5.sp)
                }
                Icon(Icons.Default.WbSunny, null, tint = Ocean.copy(alpha = .7f))
            }
        }
        item {
            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
                    .background(Brush.linearGradient(listOf(Navy, Ocean, Color(0xFF176A70))))
                    .padding(22.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(color = Teal.copy(alpha = .17f), shape = CircleShape) {
                        Text("KENALI RISIKO • SIAPKAN DIRI", modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            color = Teal, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = .8.sp)
                    }
                    Text("Siap sebelum\nkeadaan darurat.", color = Color.White, fontSize = 29.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold)
                    Text("Pelajari tindakan yang tepat, latih keputusan, dan siapkan kebutuhan daruratmu.",
                        color = Color(0xFFD0E3E8), fontSize = 13.sp, lineHeight = 19.sp)
                    Button(
                        onClick = { onNavigate("learn") },
                        colors = ButtonDefaults.buttonColors(containerColor = Teal, contentColor = Navy),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Mulai belajar", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(17.dp))
                    }
                }
            }
        }
        item { SectionHeader("Jelajahi", "Pilih kebutuhanmu") }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FeatureCard("Belajar mitigasi", "Materi ringkas", Icons.Default.MenuBook, Teal, Modifier.weight(1f)) { onNavigate("learn") }
                    FeatureCard("Simulasi", "Latihan keputusan", Icons.Default.Explore, Color(0xFFFFD9C9), Modifier.weight(1f)) { onNavigate("simulate") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FeatureCard("Tas siaga", "$checkedCount item dicek", Icons.Default.Backpack, Color(0xFFDCE8FF), Modifier.weight(1f)) { onNavigate("kit") }
                    FeatureCard("Panduan gempa", "Langkah penting", Icons.Default.Shield, Color(0xFFE0EDE8), Modifier.weight(1f)) { onNavigate("guide") }
                }
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, Color(0xFFE3EBEE))
            ) {
                Row(Modifier.fillMaxWidth().clickable { onNavigate("local") }.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(48.dp).clip(RoundedCornerShape(15.dp)).background(Navy), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.LocationOn, null, tint = Teal, modifier = Modifier.size(25.dp))
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Kenali pesisir Tulungagung", fontWeight = FontWeight.Bold, color = Ink)
                        Spacer(Modifier.height(4.dp))
                        Text("Edukasi lokal dan persiapan keluarga", color = Muted, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, null, tint = Muted)
                }
            }
        }
        item {
            Text("Catatan keselamatan", color = Navy, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("SEISMOVA versi ini adalah aplikasi edukasi. Aplikasi tidak menampilkan status tsunami real-time dan bukan pengganti peringatan BMKG atau arahan petugas.",
                color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, color = Navy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Muted, fontSize = 12.sp)
    }
}

@Composable
private fun FeatureCard(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector,
                        tint: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(142.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color(0xFFE4ECEF))
    ) {
        Column(Modifier.fillMaxSize().padding(15.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Box(Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(tint), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Ocean, modifier = Modifier.size(23.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, fontWeight = FontWeight.Bold, color = Ink, fontSize = 14.sp)
                Text(subtitle, color = Muted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

private data class Lesson(val title: String, val subtitle: String, val body: String, val icon: String)

private val lessons = listOf(
    Lesson("Kenali tsunami", "Memahami proses dan risikonya", "Tsunami adalah rangkaian gelombang yang dapat dipicu oleh perpindahan massa air secara tiba-tiba, termasuk akibat gempa bawah laut. Tsunami tidak selalu berupa satu gelombang besar; beberapa gelombang dapat datang berurutan.", "🌊"),
    Lesson("Kenali tanda bahaya", "Jangan abaikan tanda alam", "Gempa kuat atau berlangsung lama di wilayah pesisir, perubahan muka air laut yang tidak biasa, atau suara gemuruh dari laut dapat menjadi tanda bahaya. Segera menuju tempat tinggi atau menjauh dari pantai sesuai arahan evakuasi; jangan menunggu aplikasi ini.", "⚠️"),
    Lesson("Evakuasi dengan aman", "Utamakan keselamatan", "Ikuti jalur evakuasi dan petunjuk petugas. Bergerak menjauhi pantai menuju tempat tinggi atau lokasi aman yang telah ditetapkan. Hindari kembali ke pantai sampai otoritas menyatakan aman.", "🧭"),
    Lesson("Bersiap bersama keluarga", "Buat rencana sebelum bencana", "Sepakati titik temu, cara menghubungi keluarga, kebutuhan anggota keluarga yang memerlukan bantuan, dan jalur evakuasi. Simpan dokumen penting dan kebutuhan dasar di tempat yang mudah dijangkau.", "🏠"),
    Lesson("Setelah evakuasi", "Tetap di tempat aman", "Jauhi garis pantai dan area yang terdampak. Ikuti informasi resmi dari BMKG, BNPB, BPBD, dan petugas setempat. Jangan menyebarkan informasi yang belum terverifikasi.", "🤝")
)

@Composable
private fun LearnScreen(completedLessons: Int, onComplete: () -> Unit) {
    var selected by rememberSaveable { mutableIntStateOf(-1) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Ruang belajar", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Navy)
            Text("Materi singkat untuk membangun kesiapsiagaan.", color = Muted, fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { completedLessons / 5f },
                modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                color = Teal, trackColor = Color(0xFFDCE7EA)
            )
            Spacer(Modifier.height(5.dp))
            Text("$completedLessons dari 5 materi ditandai selesai", fontSize = 11.sp, color = Muted)
        }
        items(lessons.indices.toList()) { i ->
            val lesson = lessons[i]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE3EBEE))
            ) {
                Column(Modifier.fillMaxWidth().clickable { selected = if (selected == i) -1 else i }.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(lesson.icon, fontSize = 27.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(lesson.title, fontWeight = FontWeight.Bold, color = Ink)
                            Text(lesson.subtitle, color = Muted, fontSize = 12.sp)
                        }
                        Icon(if (selected == i) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Ocean)
                    }
                    AnimatedVisibility(visible = selected == i, enter = fadeIn()) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Spacer(Modifier.height(6.dp))
                            Text(lesson.body, color = Ink, fontSize = 13.sp, lineHeight = 20.sp)
                            OutlinedButton(
                                onClick = { onComplete(); selected = -1 },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Teal)
                            ) { Text("Tandai selesai", color = Ocean) }
                        }
                    }
                }
            }
        }
        item {
            Text("Materi ini merupakan ringkasan edukasi awal. Verifikasi dan lengkapi dengan panduan resmi setempat sebelum digunakan sebagai materi publik.",
                color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}

private data class ScenarioOption(val label: String, val feedback: String, val safe: Boolean)

@Composable
private fun SimulationScreen() {
    var step by rememberSaveable { mutableIntStateOf(0) }
    var chosen by rememberSaveable { mutableIntStateOf(-1) }
    val options = listOf(
        ScenarioOption("Segera evakuasi menuju tempat tinggi melalui jalur yang aman.", "Pilihan tepat. Gempa kuat atau lama di wilayah pesisir dapat menjadi tanda bahaya tsunami. Segera evakuasi tanpa menunggu peringatan dari aplikasi ini.", true),
        ScenarioOption("Mendekati pantai untuk melihat kondisi air laut.", "Tidak aman. Jangan menuju pantai untuk memeriksa keadaan laut. Segera menjauh dan ikuti jalur evakuasi.", false),
        ScenarioOption("Menunggu notifikasi aplikasi edukasi ini.", "Tidak aman. Aplikasi edukasi ini tidak menerima peringatan real-time. Jangan menunda evakuasi demi notifikasi.", false)
    )
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Simulasi keputusan", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Navy)
        Text("Latihan singkat • Tidak menggantikan latihan resmi", color = Muted, fontSize = 12.sp)
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(Navy, Ocean))).padding(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.Explore, null, tint = Teal, modifier = Modifier.size(34.dp))
                Text("SKENARIO 01", color = Teal, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Text("Kamu berada di kawasan pesisir. Tiba-tiba terasa gempa kuat dan berlangsung cukup lama. Apa tindakanmu?", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
            }
        }
        Text("Pilih tindakanmu", color = Navy, fontWeight = FontWeight.Bold)
        options.forEachIndexed { index, option ->
            val selected = chosen == index
            Card(
                modifier = Modifier.fillMaxWidth().clickable { chosen = index },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = if (selected) Teal.copy(alpha = .14f) else Color.White),
                border = BorderStroke(1.dp, if (selected) Teal else Color(0xFFE3EBEE))
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selected, onClick = { chosen = index }, colors = RadioButtonDefaults.colors(selectedColor = Ocean))
                    Spacer(Modifier.width(8.dp))
                    Text(option.label, color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
                }
            }
        }
        if (chosen >= 0) {
            val result = options[chosen]
            Card(colors = CardDefaults.cardColors(containerColor = if (result.safe) Color(0xFFE1F5EF) else Color(0xFFFFE9E2)),
                shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (result.safe) "Pilihan tepat" else "Coba pahami risikonya", fontWeight = FontWeight.Bold,
                        color = if (result.safe) Color(0xFF176B5B) else Color(0xFF9B3D27))
                    Text(result.feedback, color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
                    OutlinedButton(onClick = { chosen = -1 }, shape = RoundedCornerShape(12.dp)) { Text("Coba lagi") }
                }
            }
        }
    }
}

private val kitItems = listOf(
    "Air minum dan makanan tahan lama",
    "Obat pribadi dan perlengkapan P3K",
    "Senter dan baterai cadangan",
    "Peluit",
    "Power bank dan kabel pengisi daya",
    "Salinan dokumen penting dalam wadah tahan air",
    "Pakaian ganti dan perlengkapan kebersihan",
    "Uang tunai secukupnya",
    "Kebutuhan bayi, lansia, atau anggota keluarga yang memerlukan bantuan"
)

@Composable
private fun KitScreen(checked: List<String>, onToggle: (String, Boolean) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Tas siaga", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Navy)
            Text("Checklist kebutuhan dasar untuk persiapan keluarga.", color = Muted, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Ocean), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("PROGRES PERSIAPAN", color = Teal, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(Modifier.height(7.dp))
                    Text("${checked.size} / ${kitItems.size} item", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { checked.size / kitItems.size.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                        color = Teal, trackColor = Color.White.copy(alpha = .18f)
                    )
                }
            }
        }
        items(kitItems) { item ->
            Card(colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color(0xFFE3EBEE))) {
                Row(
                    Modifier.fillMaxWidth().clickable { onToggle(item, !checked.contains(item)) }.padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = checked.contains(item), onCheckedChange = { onToggle(item, it) },
                        colors = CheckboxDefaults.colors(checkedColor = Ocean))
                    Spacer(Modifier.width(7.dp))
                    Text(item, modifier = Modifier.weight(1f), color = Ink, fontSize = 13.sp, lineHeight = 18.sp)
                }
            }
        }
        item {
            Text("Sesuaikan isi tas dengan kebutuhan keluarga, kondisi kesehatan, dan panduan resmi BNPB/BPBD.",
                color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun GuideScreen() {
    val steps = listOf(
        Triple("1", "Saat gempa berlangsung", "Jika berada di dalam bangunan, lindungi kepala dan leher, berlindung di bawah meja kokoh bila tersedia, dan jauhi kaca atau benda yang dapat jatuh. Jangan menggunakan lift."),
        Triple("2", "Setelah guncangan berhenti", "Perhatikan bahaya sekitar dan keluar dengan hati-hati jika jalur aman. Waspadai bangunan rusak, kabel, dan reruntuhan."),
        Triple("3", "Jika berada di kawasan pesisir", "Jika gempa kuat atau lama terasa, segera evakuasi menjauhi pantai menuju tempat tinggi atau lokasi evakuasi yang telah ditetapkan. Jangan menunggu sirene atau pesan aplikasi."),
        Triple("4", "Selama evakuasi", "Ikuti rute resmi dan arahan petugas. Bantu anak-anak, lansia, penyandang disabilitas, dan orang yang membutuhkan bantuan jika aman dilakukan."),
        Triple("5", "Setelah tiba di tempat aman", "Tetap di lokasi aman sampai otoritas menyatakan keadaan aman. Gelombang berikutnya dapat datang setelah gelombang pertama.")
    )
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Text("Panduan saat gempa", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Navy)
            Text("Baca langkah-langkah ini sebelum keadaan darurat.", color = Muted, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE8DF)), shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.padding(15.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Warning, null, tint = Color(0xFFB64D2D))
                    Spacer(Modifier.width(10.dp))
                    Text("Di pesisir, jangan menunggu konfirmasi aplikasi jika gempa kuat atau lama terasa. Segera ikuti prosedur evakuasi tsunami setempat.",
                        color = Color(0xFF79321F), fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 18.sp)
                }
            }
        }
        items(steps) { (number, title, body) ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Box(Modifier.size(38.dp).clip(CircleShape).background(Ocean), contentAlignment = Alignment.Center) {
                    Text(number, color = Teal, fontWeight = FontWeight.Bold)
                }
                Card(Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, Color(0xFFE3EBEE))) {
                    Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(title, fontWeight = FontWeight.Bold, color = Ink)
                        Text(body, color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
                    }
                }
            }
        }
        item {
            Text("Panduan ini bersifat umum. Lengkapi dengan arahan resmi BPBD/BMKG dan prosedur lokasi setempat.",
                color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun LocalScreen() {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text("Pesisir Tulungagung", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Navy)
        Text("Kenali lingkungan, siapkan rencana, dan verifikasi informasi lokal.", color = Muted, fontSize = 13.sp)
        Box(Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Navy, Ocean, Color(0xFF2C8D8A)))),
            contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Icon(Icons.Default.Waves, null, tint = Teal, modifier = Modifier.size(44.dp))
                Text("PESISIR SELATAN", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text("TULUNGAGUNG • JAWA TIMUR", color = Color(0xFFCBE3E7), fontSize = 10.sp, letterSpacing = 1.sp)
            }
        }
        InfoCard("Sebelum bencana", "Cari tahu apakah rumah, sekolah, atau tempat kerja berada di zona rawan. Kenali jalur dan titik evakuasi resmi dari pemerintah daerah. Susun rencana keluarga dan latihan bersama.") 
        InfoCard("Saat berada di pantai", "Pahami tanda bahaya tsunami dan jalur untuk segera menjauh dari pantai menuju tempat tinggi atau lokasi aman yang ditetapkan. Jangan mendekati pantai untuk melihat kondisi air.") 
        InfoCard("Verifikasi informasi", "Gunakan informasi dari BMKG, BNPB, BPBD, pemerintah daerah, dan petugas resmi. Halaman ini belum memuat peta evakuasi tervalidasi atau informasi status bahaya real-time.") 
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, Color(0xFFE3EBEE))) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Data lokal yang perlu dilengkapi", fontWeight = FontWeight.Bold, color = Ink)
                Text("• Peta bahaya resmi dan tahun penerbitannya\n• Jalur evakuasi dan titik kumpul yang diverifikasi BPBD\n• Lokasi rambu dan tempat evakuasi\n• Kontak resmi yang telah dikonfirmasi", color = Muted, fontSize = 12.sp, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFE3EBEE))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Ink)
            Text(body, color = Muted, fontSize = 12.sp, lineHeight = 19.sp)
        }
    }
}

@Composable
private fun ProgressScreen(checkedCount: Int, lessons: Int, onNavigate: (String) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Progres kesiapsiagaan", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Navy)
        Text("Langkah kecil untuk menjadi lebih siap.", color = Muted, fontSize = 13.sp)
        Card(colors = CardDefaults.cardColors(containerColor = Ocean), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.EmojiEvents, null, tint = Teal, modifier = Modifier.size(34.dp))
                Text("Terus belajar, terus bersiap.", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Progres tersimpan di perangkat ini.", color = Color(0xFFC9DDE3), fontSize = 12.sp)
            }
        }
        ProgressRow("Materi ditandai selesai", "$lessons / 5", lessons / 5f)
        ProgressRow("Checklist tas siaga", "$checkedCount / ${kitItems.size}", checkedCount / kitItems.size.toFloat())
        Button(onClick = { onNavigate("kit") }, modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Ocean), shape = RoundedCornerShape(14.dp)) {
            Icon(Icons.Default.Backpack, null)
            Spacer(Modifier.width(8.dp))
            Text("Lanjutkan checklist")
        }
        OutlinedButton(onClick = { onNavigate("learn") }, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Teal)) {
            Icon(Icons.Default.MenuBook, null)
            Spacer(Modifier.width(8.dp))
            Text("Lanjutkan belajar", color = Ocean)
        }
        Text("Progres hanya menunjukkan aktivitas dalam aplikasi, bukan sertifikasi kesiapsiagaan atau bukti bahwa lokasi sudah aman.",
            color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun ProgressRow(title: String, value: String, progress: Float) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFE3EBEE))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, modifier = Modifier.weight(1f), color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(value, color = Ocean, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                color = Teal, trackColor = Color(0xFFE2EBEE))
        }
    }
}
