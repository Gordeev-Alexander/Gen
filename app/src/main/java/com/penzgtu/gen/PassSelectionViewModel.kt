package com.penzgtu.gen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class PassSelectionViewModel : ViewModel() {
    private val _generatedPassword = MutableStateFlow("")
    val generatedPassword: StateFlow<String> = _generatedPassword

    private var useSemanticReplacement = true

    private var semanticMap: Map<String, List<String>> = emptyMap()

    fun loadDictionary(context: Context) {
        try {
            val inputStream = context.resources.openRawResource(R.raw.semantic_dict)
            val json = inputStream.bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<String>>>() {}.type
            semanticMap = Gson().fromJson(json, type)
        } catch (e: IOException) {
            e.printStackTrace()
            semanticMap = emptyMap()
        }
    }

    fun toggleSemanticReplacement(enabled: Boolean) {
        useSemanticReplacement = enabled
    }

    fun generateSecurePassword(input: String) {
        _generatedPassword.value = transformToSecurePassword(input.trim())
    }

    private fun transformToSecurePassword(input: String): String {
        if (input.isEmpty()) return ""

        var base = input

        if (useSemanticReplacement) {
            if (input.all { it.isLetter() } && input.length in 3..12) {
                val lowerInput = input.lowercase()
                semanticMap[lowerInput]?.let { candidates ->
                    val replacement = candidates.random()
                    base = if (input[0].isUpperCase()) {
                        replacement.replaceFirstChar { it.uppercaseChar() }
                    } else {
                        replacement
                    }
                }
            }
        }

        base = base
            .replace('a', '@').replace('A', '@')
            .replace('o', '0').replace('O', '0')
            .replace('s', '$').replace('S', '$')
            .replace('e', '3').replace('E', '3')
            .replace('i', '1').replace('I', '1')
            .replace('l', '1').replace('L', '1')

        if (base.isNotEmpty()) {
            val first = base[0]
            val newFirst = if (first.isLowerCase()) first.uppercaseChar() else first.lowercaseChar()
            base = newFirst + base.substring(1)
        }

        val numExtras = Random.nextInt(1, 6)
        val extras = "0123456789!@#\$%^&*"
        val extraPart = (1..numExtras).joinToString("") { extras.random().toString() }

        val result = if (Random.nextBoolean()) {
            extraPart + base
        } else {
            base + extraPart
        }

        return result.take(30)
    }

}