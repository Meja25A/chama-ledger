package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiModelOption
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.ui.theme.ChamaGoldTertiary
import com.example.ui.theme.ChamaGreenPrimary

@Composable
fun AiAdvisorScreen(
    messages: List<ChatMessage>,
    isGenerating: Boolean,
    selectedModel: String,
    isSearchGroundingEnabled: Boolean,
    onSelectModel: (String) -> Unit,
    onToggleSearchGrounding: (Boolean) -> Unit,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickPrompts = listOf(
        "Check current CBK base lending rates & inflation in Kenya",
        "How should our Chama calculate 10% loan interest?",
        "What are best practices to prevent member loan defaults?",
        "How do we transition our Chama into a registered SACCO?"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("screen_ai_advisor")
    ) {
        // 1. Model & Search Grounding Controls Bar
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                // Model Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GEMINI MODEL:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    IconButton(
                        onClick = onClearChat,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear History",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Model Options: Fast, General, Complex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AiModelOption.entries.forEach { option ->
                        FilterChip(
                            selected = selectedModel == option.modelId,
                            onClick = { onSelectModel(option.modelId) },
                            label = {
                                Text(
                                    text = "${option.displayName} (${option.badge})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            leadingIcon = {
                                val icon = when (option) {
                                    AiModelOption.FAST -> Icons.Default.Speed
                                    AiModelOption.GENERAL -> Icons.Default.AutoAwesome
                                    AiModelOption.COMPLEX -> Icons.Default.Psychology
                                }
                                Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Search Grounding Toggle Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF1A73E8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Google Search Grounding",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Live Kenyan finance & CBK rates (gemini-3.5-flash)",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isSearchGroundingEnabled,
                        onCheckedChange = onToggleSearchGrounding,
                        modifier = Modifier.testTag("switch_search_grounding"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF1A73E8),
                            checkedTrackColor = Color(0xFFD2E3FC)
                        )
                    )
                }
            }
        }

        // 2. Quick Prompt Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickPrompts.forEach { prompt ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp,
                    modifier = Modifier.testTag("chip_prompt")
                ) {
                    Text(
                        text = prompt,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .background(Color.Transparent)
                            .clickable { onSendMessage(prompt) }
                    )
                }
            }
        }

        // 3. Multi-turn Scrollable Conversation Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(messages) { message ->
                ChatMessageItem(message = message)
            }

            if (isGenerating) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = ChamaGreenPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isSearchGroundingEnabled) "Grounding with Google Search..." else "ChamaBot thinking...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // 4. Input Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask ChamaBot or search live rates...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_chat_prompt"),
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isGenerating) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(ChamaGreenPrimary, CircleShape)
                        .testTag("btn_send_chat"),
                    enabled = inputText.isNotBlank() && !isGenerating
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage) {
    val isUser = message.sender == MessageSender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(ChamaGreenPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.82f else 0.88f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) ChamaGreenPrimary else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Header for AI message with Model & Grounding badges
                    if (!isUser) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "ChamaBot",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = ChamaGreenPrimary
                            )
                            message.modelUsed?.let { model ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = model,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (message.isGroundingUsed) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFE8F0FE)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Language,
                                            contentDescription = null,
                                            modifier = Modifier.size(10.dp),
                                            tint = Color(0xFF1A73E8)
                                        )
                                        Text(
                                            text = " Google Search",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1A73E8)
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Message Text
                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                    )

                    // Grounding Sources if present
                    if (!isUser && message.searchSources.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Search Grounding Sources:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        message.searchSources.take(3).forEach { source ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFF1F3F4),
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "• $source",
                                    fontSize = 10.sp,
                                    color = Color(0xFF3C4043),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
