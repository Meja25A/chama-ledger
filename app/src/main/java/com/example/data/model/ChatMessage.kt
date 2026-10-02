package com.example.data.model

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String? = null,
    val isGroundingUsed: Boolean = false,
    val searchSources: List<String> = emptyList()
)

enum class MessageSender {
    USER,
    CHAMA_AI
}

enum class AiModelOption(
    val modelId: String,
    val displayName: String,
    val badge: String,
    val description: String
) {
    FAST(
        modelId = "gemini-3.1-flash-lite-preview",
        displayName = "Fast",
        badge = "Lite",
        description = "Rapid responses & quick calculation checks"
    ),
    GENERAL(
        modelId = "gemini-3.5-flash",
        displayName = "General",
        badge = "Flash",
        description = "Standard Chama advice & Google Search Grounding"
    ),
    COMPLEX(
        modelId = "gemini-3.1-pro-preview",
        displayName = "Complex",
        badge = "Pro",
        description = "Deep financial audits, risk analysis & SACCO planning"
    )
}
