package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.ChatUiState
import com.example.ui.viewmodel.ChatViewModel

@Composable
fun AiAssistantScreen(
    chatViewModel: ChatViewModel,
    onShowDriverClick: (String, String) -> Unit
) {
    val state by chatViewModel.uiState.collectAsState()
    val isBurmese = state.isBurmese
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // AI Policy & Uncertainty Banner
        Surface(
            color = BurmeseGold.copy(alpha = 0.2f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = TextDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBurmese) "AI လမ်းညွှန်: အသက်သာဆုံးလမ်းကြောင်း၊ ယာဉ်ခနှင့် ဒရိုင်ဘာအားပြရန် ထိုင်းစာတမ်းများ" else "ผู้ช่วย AI: เส้นทางประหยัด ค่าโดยสาร และประโยคบอกคนขับ",
                        fontSize = 11.sp,
                        color = TextDark,
                        fontWeight = FontWeight.Medium
                    )
                }

                TextButton(
                    onClick = { chatViewModel.clearChat() },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(text = if (isBurmese) "ရှင်းမည်" else "ล้าง", fontSize = 11.sp, color = BangkokBlue, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.messages) { message ->
                ChatMessageItem(
                    message = message,
                    isBurmese = isBurmese,
                    onSpeakThai = { chatViewModel.speakThai(it) },
                    onShowFlashcard = { thaiText ->
                        onShowDriverClick(thaiText, "ယာဉ်မောင်းအား ပြသရန် ထိုင်းစာသား")
                    }
                )
            }

            if (state.isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = BangkokBlue,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isBurmese) "AI မှ လမ်းကြောင်းနှင့် ယာဉ်ခ တွက်ချက်နေပါသည်..." else "กำลังวิเคราะห์เส้นทาง...",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            if (state.errorMessage != null) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_error_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = state.errorMessage ?: "",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            TextButton(
                                onClick = { chatViewModel.retryLastMessage() }
                            ) {
                                Text(
                                    text = if (isBurmese) "ပြန်စမ်းမည်" else "ลองใหม่",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Suggested Questions Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.suggestedPrompts) { question ->
                SuggestionChip(
                    onClick = { chatViewModel.sendMessage(question) },
                    label = { Text(text = question, fontSize = 12.sp) },
                    shape = RoundedCornerShape(16.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.height(34.dp)
                )
            }
        }

        // Bottom Input Row
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.imePadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.inputQuery,
                    onValueChange = { chatViewModel.onQueryChanged(it) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_ai_message"),
                    placeholder = {
                        Text(
                            text = if (isBurmese) "ဘယ်သွားချင်လဲ မေးပါ..." else "พิมพ์คำถามการเดินทาง...",
                            fontSize = 14.sp
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BangkokBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (state.inputQuery.isNotBlank()) {
                            chatViewModel.sendMessage(state.inputQuery)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BangkokBlue)
                        .testTag("btn_send_ai_message")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    isBurmese: Boolean,
    onSpeakThai: (String) -> Unit,
    onShowFlashcard: (String) -> Unit
) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(BangkokBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsTransit,
                    contentDescription = null,
                    tint = BurmeseGold,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = if (isUser) BangkokBlue else MaterialTheme.colorScheme.surface,
            contentColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 2.dp,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Uncertainty Label Badge for AI Responses
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (message.isOfflineFallback) SuccessGreen.copy(alpha = 0.15f) else BangkokBlue.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (message.isOfflineFallback) "🛡 အော့ဖ်လိုင်းစနစ် (Offline)" else "✨ Gemini 3.5 Flash",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (message.isOfflineFallback) SuccessGreen else BangkokBlue,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "ခန့်မှန်းချက်",
                            fontSize = 10.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Main Text Content
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Normal
                )

                // Optional Driver Phrase Action Bar
                if (!isUser && !message.thaiPhraseForDriver.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = BurmeseGold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🚖 ဒရိုင်ဘာအားပြရန်:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = message.thaiPhraseForDriver,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BangkokBlueDark
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = { onSpeakThai(message.thaiPhraseForDriver) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Speak",
                                        tint = BangkokBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onShowFlashcard(message.thaiPhraseForDriver) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Visibility,
                                        contentDescription = "Flashcard",
                                        tint = TextDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Uncertainty Disclaimer footer
                if (!isUser && message.uncertaintyLabel.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ℹ️ ${message.uncertaintyLabel}",
                        fontSize = 10.sp,
                        color = TextMuted,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
