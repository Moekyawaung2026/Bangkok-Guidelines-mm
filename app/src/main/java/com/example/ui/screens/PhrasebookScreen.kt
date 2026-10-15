package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhrasebookItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.BangkokUiState

@Composable
fun PhrasebookScreen(
    state: BangkokUiState,
    phrasebookItems: List<PhrasebookItem>,
    onSelectCategory: (String) -> Unit,
    onSpeakThai: (String) -> Unit,
    onShowLargeText: (String, String) -> Unit
) {
    val context = LocalContext.current
    val isBurmese = state.isBurmese

    val categories = listOf(
        "လမ်းမေးမြန်းခြင်း",
        "ဘတ်စ်ကား",
        "တက္ကစီ",
        "ဆိုင်ကယ်ကယ်ရီ",
        "ဆေးရုံနှင့် အရေးပေါ်",
        "ရဲစခန်းနှင့် စစ်ဆေးမှု",
        "အလုပ်ခွင်နှင့် စျေးရောင်းဝယ်"
    )

    val displayedPhrases = phrasebookItems.filter {
        state.selectedPhraseCategory.isBlank() || it.categoryMm == state.selectedPhraseCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Category Chips Row
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = state.selectedPhraseCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(cat) },
                        label = { Text(text = cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BangkokBlue,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.height(36.dp)
                    )
                }
            }
        }

        // Phrases List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(displayedPhrases) { phrase ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("phrase_card_${phrase.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Category Pill
                        Surface(
                            color = BurmeseGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = phrase.categoryMm,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Burmese Text
                        Text(
                            text = phrase.burmeseText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Thai Text (Large, High Contrast)
                        Text(
                            text = phrase.thaiText,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BangkokBlue
                        )

                        // Romanized Pronunciation
                        Text(
                            text = "အသံထွက်: ${phrase.romanizedPronunciation}",
                            fontSize = 13.sp,
                            color = TextMuted
                        )

                        if (phrase.usageTipMm.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "💡 အသုံးပြုပုံ: ${phrase.usageTipMm}",
                                fontSize = 12.sp,
                                color = BangkokBlueDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Action Buttons: Play Audio, Large Screen Flashcard, Copy
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Play TTS audio
                            Button(
                                onClick = { onSpeakThai(phrase.thaiText) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("btn_phrase_speak_${phrase.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = BangkokBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Play", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "အသံဖွင့်မည်", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            // Large Text Flashcard
                            OutlinedButton(
                                onClick = { onShowLargeText(phrase.thaiText, phrase.burmeseText) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("btn_phrase_large_${phrase.id}"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Outlined.Fullscreen, contentDescription = "Large Card", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "စာလုံးကြီးပြ", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            // Copy Button
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Thai Phrase", phrase.thaiText)
                                    clipboard.setPrimaryClip(clip)
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("btn_phrase_copy_${phrase.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = "Copy to clipboard",
                                    tint = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
