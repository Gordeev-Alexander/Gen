package com.penzgtu.gen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import kotlin.math.pow

data class PasswordStrength(
    val hasDigit: Boolean = false,
    val hasSpecial: Boolean = false,
    val hasUppercase: Boolean = false,
    val crackTime: String = "—",
    val isInWeakList: Boolean = false
)

class PassExamViewModel : ViewModel() {

    private val _strength = MutableStateFlow(PasswordStrength())
    val strength: StateFlow<PasswordStrength> = _strength

    private var weakHashes: Set<String>? = null

    fun setPassword(password: String, context: Context) {
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        val hasUppercase = password.any { it.isUpperCase() }
        val crackTime = estimateCrackTime(password)

        viewModelScope.launch {
            val isInWeak = isInWeakList(password, context)
            _strength.value = PasswordStrength(
                hasDigit = hasDigit,
                hasSpecial = hasSpecial,
                hasUppercase = hasUppercase,
                crackTime = crackTime,
                isInWeakList = isInWeak
            )
        }
    }

    private suspend fun isInWeakList(password: String, context: Context): Boolean {
        if (password.isEmpty()) return false

        val targetHash = password.toByteArray().toSHA1().uppercase(java.util.Locale.US)

        weakHashes?.let { return targetHash in it }

        return withContext(Dispatchers.IO) {
            try {
                val hashes = context.assets
                    .open("password_hashes.txt")
                    .bufferedReader()
                    .use { it.readLines().toSet() }

                weakHashes = hashes
                targetHash in hashes
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun ByteArray.toSHA1(): String {
        return MessageDigest.getInstance("SHA-1")
            .digest(this)
            .joinToString("") { "%02x".format(it) }
    }

    private fun estimateCrackTime(password: String): String {
        if (password.isEmpty()) return ""

        val charsetSize = calculateCharsetSize(password)
        val combinations = charsetSize.toDouble().pow(password.length.toDouble())
        val attemptsPerSecond = 1e11
        val seconds = combinations / attemptsPerSecond

        return when {
            seconds < 1 -> "менее секунды"
            seconds < 60 -> "${seconds.toInt()} сек"
            seconds < 3_600 -> "${(seconds / 60).toInt()} мин"
            seconds < 86_400 -> "${(seconds / 3_600).toInt()} ч"
            seconds < 2_592_000 -> "${(seconds / 86_400).toInt()} дн"
            seconds < 31_536_000 -> "${(seconds / 2_592_000).toInt()} мес"
            else -> "${(seconds / 31_536_000).toInt()} лет"
        }
    }

    private fun calculateCharsetSize(password: String): Int {
        var size = 0
        if (password.any { it.isDigit() }) size += 10
        if (password.any { it.isLowerCase() }) size += 26
        if (password.any { it.isUpperCase() }) size += 26
        if (password.any { !it.isLetterOrDigit() }) size += 33
        return size.coerceAtLeast(1)
    }
}