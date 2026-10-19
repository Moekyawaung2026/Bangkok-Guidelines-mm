package com.example.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.*

/**
 * Quick visual destination preset designed for users with limited literacy.
 */
data class VisualVoiceDestination(
    val titleMm: String,
    val titleTh: String,
    val titleEn: String = "",
    val subtitleMm: String,
    val subtitleTh: String,
    val icon: ImageVector,
    val iconTint: Color,
    val bgTint: Color
)

/**
 * Floating Action Button for voice-to-text input on the home screen.
 * Features high-contrast Burmese Gold styling, large microphone icon,
 * and prominent readable labeling.
 */
@Composable
fun VoiceSearchFloatingActionButton(
    onClick: () -> Unit,
    isBurmese: Boolean = LocalIsBurmese.current,
    modifier: Modifier = Modifier
) {
    // Subtle breathing pulse animation to indicate voice readiness
    val infiniteTransition = rememberInfiniteTransition(label = "fab_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(TextDark.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = if (isBurmese) "အသံဖြင့်ရှာဖွေရန် မိုက်ခရိုဖုန်း" else "ไมโครโฟน ค้นหาด้วยเสียง",
                    modifier = Modifier.size(24.dp),
                    tint = TextDark
                )
            }
        },
        text = {
            Column {
                Text(
                    text = if (isBurmese) "အသံဖြင့်ရှာမည်" else "พูดเพื่อค้นหา",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextDark
                )
                Text(
                    text = if (isBurmese) "မိုက်ခရိုဖုန်းကို နှိပ်ပါ" else "แตะเพื่อพูด",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDark.copy(alpha = 0.8f)
                )
            }
        },
        containerColor = BurmeseGold,
        contentColor = TextDark,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp
        ),
        shape = RoundedCornerShape(28.dp),
        modifier = modifier
            .scale(pulseScale)
            .heightIn(min = 56.dp)
            .testTag("fab_voice_search")
    )
}

/**
 * Voice-to-Text Search Dialog designed specifically for users with limited literacy.
 * Integrates Android's speech recognition flow, live audio pulsation indicators,
 * and high-contrast visual destination shortcuts.
 */
@Composable
fun VoiceSearchDialog(
    onDismiss: () -> Unit,
    onSpeechResult: (String) -> Unit,
    onSpeakPrompt: ((String) -> Unit)? = null,
    isBurmese: Boolean = LocalIsBurmese.current
) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var statusMessage by remember {
        mutableStateOf(
            if (isBurmese) "သွားလိုသောနေရာကို အသံထွက်၍ ပြောပါ" else "พูดชื่อสถานที่ที่ต้องการไป"
        )
    }

    // Android Speech Recognition Activity Result Launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spokenMatches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val topMatch = spokenMatches?.firstOrNull()?.trim()
            if (!topMatch.isNullOrEmpty()) {
                recognizedText = topMatch
                statusMessage = if (isBurmese) "ကြားသိပါသည်: \"$topMatch\"" else "ได้ยินว่า: \"$topMatch\""
                onSpeechResult(topMatch)
                onDismiss()
            } else {
                statusMessage = if (isBurmese) "ထပ်မံပြောဆိုပေးပါ" else "กรุณาลองพูดอีกครั้ง"
            }
        }
    }

    // Audio recording runtime permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startSpeechRecognition(context, isBurmese) { intent ->
                isListening = true
                speechRecognizerLauncher.launch(intent)
            }
        } else {
            statusMessage = if (isBurmese) "မိုက်ခရိုဖုန်း ခွင့်ပြုချက် လိုအပ်ပါသည်" else "ต้องการสิทธิ์การใช้ไมโครโฟน"
        }
    }

    fun triggerListening() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            startSpeechRecognition(context, isBurmese) { intent ->
                isListening = true
                speechRecognizerLauncher.launch(intent)
            }
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Initial voice prompt on launch
    LaunchedEffect(Unit) {
        val promptText = if (isBurmese) "သွားလိုသောနေရာကို ပြောပါ" else "พูดชื่อสถานที่ที่ต้องการไป"
        onSpeakPrompt?.invoke(promptText)
        triggerListening()
    }

    // Animated listening ripple
    val infiniteTransition = rememberInfiniteTransition(label = "mic_waves")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_scale"
    )

    // Visual pictorial presets for limited-literacy assistance
    val visualPresets = remember {
        listOf(
            VisualVoiceDestination(
                titleMm = "ဆယမ် (Siam)",
                titleTh = "สยาม (Siam)",
                titleEn = "Siam",
                subtitleMm = "BTS မိုးပျံရထား ဘူတာကြီး",
                subtitleTh = "สถานีกลาง BTS",
                icon = Icons.Default.Train,
                iconTint = Color(0xFF00823B),
                bgTint = Color(0xFFE8F5E9)
            ),
            VisualVoiceDestination(
                titleMm = "မဟာချိုင် (Mahachai)",
                titleTh = "มหาชัย (Mahachai)",
                titleEn = "Mahachai",
                subtitleMm = "ပင်လယ်စာစျေး / စက်ရုံဇုန်",
                subtitleTh = "ตลาดมหาชัย / โรงงาน",
                icon = Icons.Default.Storefront,
                iconTint = Color(0xFFC2410C),
                bgTint = Color(0xFFFFEDE7)
            ),
            VisualVoiceDestination(
                titleMm = "အောင်ဆန်းရတနာ (Victory)",
                titleTh = "อนุสาวรีย์ชัยฯ",
                titleEn = "Victory Monument",
                subtitleMm = "ဘတ်စ်ကား / BTS ဗဟို",
                subtitleTh = "จุดต่อรถเมล์และ BTS",
                icon = Icons.Default.DirectionsTransit,
                iconTint = Color(0xFF003399),
                bgTint = Color(0xFFE3F2FD)
            ),
            VisualVoiceDestination(
                titleMm = "မိုချစ် ၂ (Mo Chit 2)",
                titleTh = "หมอชิต 2 (Mo Chit)",
                titleEn = "Mo Chit 2",
                subtitleMm = "အဝေးပြေး ကားဂိတ်ကြီး",
                subtitleTh = "สถานีขนส่งหมอชิต",
                icon = Icons.Default.DirectionsBus,
                iconTint = Color(0xFF8B5A00),
                bgTint = Color(0xFFFEF3C7)
            ),
            VisualVoiceDestination(
                titleMm = "သုဝဏ္ဏဘူမိလေဆိပ်",
                titleTh = "สนามบินสุวรรณภูมิ",
                titleEn = "Suvarnabhumi Airport",
                subtitleMm = "ARL လေဆိပ်ရထား",
                subtitleTh = "Suvarnabhumi Airport",
                icon = Icons.Default.FlightTakeoff,
                iconTint = Color(0xFF9F1239),
                bgTint = Color(0xFFFFECEE)
            ),
            VisualVoiceDestination(
                titleMm = "စမွတ်ပရာကန် (Samut)",
                titleTh = "สมุทรปราการ",
                titleEn = "Samut Prakan",
                subtitleMm = "စက်ရုံများ / BTS အစိမ်းလိုင်း",
                subtitleTh = "นิคมอุตสาหกรรม",
                icon = Icons.Default.Factory,
                iconTint = Color(0xFF4C1D95),
                bgTint = Color(0xFFF3E8FF)
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(16.dp)
                .testTag("dialog_voice_search"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BurmeseGold,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = TextDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isBurmese) "အသံဖြင့် နေရာရှာဖွေခြင်း" else "ค้นหาด้วยเสียงพูด",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Large Animated Microphone Centerpiece
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(110.dp)
                        .clickable { triggerListening() }
                        .testTag("btn_mic_listen")
                ) {
                    // Outer pulsating wave
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(waveScale)
                            .clip(CircleShape)
                            .background(
                                if (isListening) BurmeseGold.copy(alpha = 0.35f)
                                else BangkokBlue.copy(alpha = 0.2f)
                            )
                    )

                    // Core Mic Button
                    Surface(
                        shape = CircleShape,
                        color = if (isListening) BurmeseGold else BangkokBlue,
                        shadowElevation = 6.dp,
                        modifier = Modifier.size(76.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                                contentDescription = if (isListening) "Listening" else "Tap to Speak",
                                tint = if (isListening) TextDark else Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Prominent instructions & status text
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isListening) BurmeseGold else MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isBurmese)
                        "မိုက်ခရိုဖုန်းကို နှိပ်ပြီး နေရာအမည် ပြောပါ သို့မဟုတ် အောက်ပါပုံများကို ရွေးပါ"
                    else
                        "แตะที่ไมโครโฟนเพื่อพูด หรือแตะที่สถานที่ยอดนิยมด้านล่าง",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(12.dp))

                // Visual One-Tap Destinations (Literacy Assistance)
                Text(
                    text = if (isBurmese) "လူသုံးအများဆုံး နေရာများ (အလွယ်တကူနှိပ်နိုင်ပါသည်):" else "สถานที่ยอดนิยม (แตะเพื่อค้นหาทันที):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                ) {
                    items(visualPresets) { preset ->
                        OutlinedCard(
                            onClick = {
                                val destination = if (isBurmese) preset.titleMm else preset.titleTh
                                onSpeechResult(destination)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(preset.iconTint.copy(alpha = 0.4f))
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .testTag("btn_visual_preset_${preset.titleEn.lowercase().replace(" ", "_")}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(preset.bgTint),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = preset.icon,
                                        contentDescription = null,
                                        tint = preset.iconTint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isBurmese) preset.titleMm else preset.titleTh,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isBurmese) preset.subtitleMm else preset.subtitleTh,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
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

/**
 * Creates and launches the Android speech recognition intent.
 */
private fun startSpeechRecognition(
    context: Context,
    isBurmese: Boolean,
    onLaunch: (Intent) -> Unit
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, if (isBurmese) "my-MM" else "th-TH")
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, if (isBurmese) "my-MM" else "th-TH")
        putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            if (isBurmese) "သွားလိုသောနေရာကို အသံထွက်၍ ပြောပါ..." else "พูดชื่อสถานที่ที่ต้องการไป..."
        )
    }

    // Check if device has an activity to handle speech recognition
    val packageManager = context.packageManager
    if (intent.resolveActivity(packageManager) != null) {
        onLaunch(intent)
    }
}
