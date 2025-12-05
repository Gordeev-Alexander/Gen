package com.penzgtu.gen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random


class PassGenViewModel : ViewModel() {

    private val _generatedPassword = MutableStateFlow("")
    val generatedPassword: StateFlow<String> = _generatedPassword

    private var useNumbers = false
    private var useUpperChars = false
    private var useSpecialSymbols = false
    private var customLength = 12

    private var isRecommendedMode = true
    fun setUseNumbers(use: Boolean) {
        useNumbers = use
    }
    fun setUpperChars(use: Boolean) {
        useUpperChars = use
    }
    fun setSpecialSymbol(use: Boolean) {
        useSpecialSymbols = use
    }
    fun setPasswordLength(length: Float) {
        customLength = length.toInt().coerceIn(8, 25)
    }

    fun setRecommendedMode(isRecommended: Boolean) {
        isRecommendedMode = isRecommended
    }

    fun generatePassword() {
        val password = if (isRecommendedMode) {
            generateRecommendedPassword()
        } else {
            generateCustomPassword()
        }
        _generatedPassword.tryEmit(password)
    }
    private fun generateRecommendedPassword(): String {
        val lower = "abcdefghijklmnopqrstuvwxyz"
        val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val digits = "0123456789"
        val symbols = "!@#\$%^&*"

        val requiredChars = mutableListOf(
            lower.random(),
            upper.random(),
            digits.random(),
            symbols.random()
        )

        val allChars = lower + upper + digits + symbols
        val targetLength = Random.nextInt(12, 21)
        val remaining = targetLength - requiredChars.size

        repeat(remaining) {
            requiredChars.add(allChars.random())
        }

        return requiredChars.shuffled().joinToString("")
    }

    private fun generateCustomPassword(): String {
        val lower = "abcdefghijklmnopqrstuvwxyz"
        var charPool = lower
        val requiredChars = mutableListOf<Char>()
        if (useUpperChars) {
            charPool += "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            requiredChars.add("ABCDEFGHIJKLMNOPQRSTUVWXYZ".random())
        }
        if (useNumbers) {
            charPool += "0123456789"
            requiredChars.add("0123456789".random())
        }
        if (useSpecialSymbols) {
            val symbols = "!@#\$%^&*()-_=+[]{}|;:,.<>?/"
            charPool += symbols
            requiredChars.add(symbols.random())
        }

        val targetLength = customLength.coerceAtLeast(1)
        val remaining = (targetLength - requiredChars.size).coerceAtLeast(0)
        val randomPart = List(remaining) { charPool.random() }

        return (requiredChars + randomPart).shuffled().joinToString("")
    }

    companion object {
        const val MIN_PASSWORD_LENGTH = 8f
    }




}