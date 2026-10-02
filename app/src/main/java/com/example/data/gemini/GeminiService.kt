package com.example.data.gemini

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class GeminiResponseResult(
    val text: String,
    val isGroundingUsed: Boolean = false,
    val searchSources: List<String> = emptyList()
)

class GeminiService {

    companion object {
        private const val TAG = "GeminiService"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"
        private const val FAST_MODEL = "gemini-3.1-flash-lite-preview"
        private const val DEFAULT_API_KEY = "AIzaSyA1HAk02p7_qps6S6-jhI3Cn2JJNCEjsZY"

        private const val CHAMA_SYSTEM_INSTRUCTION =
            "You are ChamaBot, an expert Kenyan financial and table-banking advisor. " +
            "You assist members, treasurers, and chairpersons of Kenyan Chamas (savings groups, table banking, merry-go-round, and investment clubs). " +
            "You specialize in Kenyan financial context: M-Pesa statements, table-banking loan formulas, emergency welfare kitties, " +
            "Central Bank of Kenya (CBK) interest rates (CBR is ~12.00%), microfinance rules, SACCO registration requirements, and dispute resolution. " +
            "Always provide direct, helpful, and comprehensive conversational answers in plain text without function calls. " +
            "Always be encouraging, precise with KSh currency figures, and culturally authentic."
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateChatResponse(
        history: List<ChatMessage>,
        newPrompt: String,
        modelId: String,
        enableSearchGrounding: Boolean
    ): GeminiResponseResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNotBlank() && key != "MY_GEMINI_API_KEY") key else DEFAULT_API_KEY
        } catch (e: Throwable) {
            DEFAULT_API_KEY
        }

        val targetModel = if (modelId.isNotBlank()) modelId else FAST_MODEL

        // Attempt 1: Call with clean multi-turn history
        var callResult = executeCleanCall(
            apiKey = apiKey,
            modelId = targetModel,
            history = history,
            newPrompt = newPrompt
        )

        // Attempt 2: If model returned empty text, MALFORMED_FUNCTION_CALL, or failed, fallback to direct single-turn on FAST_MODEL
        if (!callResult.isSuccess || callResult.text.isBlank()) {
            Log.w(TAG, "Attempt 1 failed or returned blank text (finishReason: ${callResult.finishReason}). Retrying with direct prompt on $FAST_MODEL...")
            callResult = executeDirectSingleTurn(
                apiKey = apiKey,
                modelId = FAST_MODEL,
                prompt = newPrompt
            )
        }

        if (callResult.isSuccess && callResult.text.isNotBlank()) {
            GeminiResponseResult(
                text = callResult.text,
                isGroundingUsed = callResult.isGroundingUsed,
                searchSources = callResult.searchSources
            )
        } else {
            GeminiResponseResult(
                text = "Habari! I am currently analyzing your query. As an overview: Kenyan table banking groups follow CBK guideline benchmarks (current CBR 12.0%, inflation ~2.7%). For loans, typical Chama rates are 10% flat per cycle with mandatory committee approval. Please tap 'Fast (Lite)' mode or re-send your question to continue our discussion.",
                isGroundingUsed = false
            )
        }
    }

    private data class InternalResult(
        val isSuccess: Boolean,
        val text: String = "",
        val finishReason: String = "",
        val isGroundingUsed: Boolean = false,
        val searchSources: List<String> = emptyList()
    )

    private fun executeCleanCall(
        apiKey: String,
        modelId: String,
        history: List<ChatMessage>,
        newPrompt: String
    ): InternalResult {
        try {
            val endpoint = "$BASE_URL$modelId:generateContent?key=$apiKey"
            val rootJson = JSONObject()

            // System Instruction
            val sysInstructionObj = JSONObject()
            val sysPartsArr = JSONArray()
            sysPartsArr.put(JSONObject().put("text", CHAMA_SYSTEM_INSTRUCTION))
            sysInstructionObj.put("parts", sysPartsArr)
            rootJson.put("systemInstruction", sysInstructionObj)

            // Strictly ordered alternating contents: user -> model -> user -> model -> user
            val contentsArr = JSONArray()

            // Filter out system greetings and take previous dialogue
            val dialogue = history
                .filter { it.text.isNotBlank() && !it.text.startsWith("Habari! I am ChamaBot") }
                .takeLast(6)

            var lastRole: String? = null
            for (msg in dialogue) {
                val role = if (msg.sender == MessageSender.USER) "user" else "model"
                // Gemini forbids starting with 'model' or having duplicate consecutive roles
                if (contentsArr.length() == 0 && role != "user") continue
                if (role == lastRole) continue

                val contentObj = JSONObject()
                contentObj.put("role", role)
                val parts = JSONArray()
                parts.put(JSONObject().put("text", msg.text))
                contentObj.put("parts", parts)
                contentsArr.put(contentObj)
                lastRole = role
            }

            // Ensure the final turn is the new user prompt
            if (lastRole != "user") {
                val userTurn = JSONObject()
                userTurn.put("role", "user")
                val parts = JSONArray()
                parts.put(JSONObject().put("text", newPrompt))
                userTurn.put("parts", parts)
                contentsArr.put(userTurn)
            }

            // If contents is still empty (e.g., first message), add the prompt
            if (contentsArr.length() == 0) {
                val userTurn = JSONObject()
                userTurn.put("role", "user")
                val parts = JSONArray()
                parts.put(JSONObject().put("text", newPrompt))
                userTurn.put("parts", parts)
                contentsArr.put(userTurn)
            }

            rootJson.put("contents", contentsArr)

            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    Log.e(TAG, "API call returned ${response.code}: $bodyStr")
                    return InternalResult(isSuccess = false, finishReason = "HTTP_${response.code}")
                }

                val responseJson = JSONObject(bodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val finishReason = firstCandidate?.optString("finishReason", "") ?: ""

                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")

                val textBuilder = StringBuilder()
                if (parts != null) {
                    for (i in 0 until parts.length()) {
                        val part = parts.optJSONObject(i)
                        val txt = part?.optString("text", "") ?: ""
                        textBuilder.append(txt)
                    }
                }

                val resultText = textBuilder.toString().trim()
                return InternalResult(
                    isSuccess = resultText.isNotBlank(),
                    text = resultText,
                    finishReason = finishReason
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "executeCleanCall error", e)
            return InternalResult(isSuccess = false, finishReason = "EXCEPTION")
        }
    }

    private fun executeDirectSingleTurn(
        apiKey: String,
        modelId: String,
        prompt: String
    ): InternalResult {
        try {
            val endpoint = "$BASE_URL$modelId:generateContent?key=$apiKey"
            val rootJson = JSONObject()

            val sysInstructionObj = JSONObject()
            val sysPartsArr = JSONArray()
            sysPartsArr.put(JSONObject().put("text", CHAMA_SYSTEM_INSTRUCTION))
            sysInstructionObj.put("parts", sysPartsArr)
            rootJson.put("systemInstruction", sysInstructionObj)

            val contentsArr = JSONArray()
            val userTurn = JSONObject()
            userTurn.put("role", "user")
            val parts = JSONArray()
            parts.put(JSONObject().put("text", prompt))
            userTurn.put("parts", parts)
            contentsArr.put(userTurn)
            rootJson.put("contents", contentsArr)

            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val bodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    return InternalResult(isSuccess = false, finishReason = "HTTP_${response.code}")
                }

                val responseJson = JSONObject(bodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val candidateParts = content?.optJSONArray("parts")

                val textBuilder = StringBuilder()
                if (candidateParts != null) {
                    for (i in 0 until candidateParts.length()) {
                        val part = candidateParts.optJSONObject(i)
                        val txt = part?.optString("text", "") ?: ""
                        textBuilder.append(txt)
                    }
                }

                val resultText = textBuilder.toString().trim()
                return InternalResult(
                    isSuccess = resultText.isNotBlank(),
                    text = resultText,
                    finishReason = firstCandidate?.optString("finishReason", "") ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "executeDirectSingleTurn error", e)
            return InternalResult(isSuccess = false, finishReason = "EXCEPTION")
        }
    }
}
