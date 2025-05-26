package com.example.searchbook.utils

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

object TranslatorHelper {
    private val cache = mutableMapOf<String, String>()

    suspend fun translateTextCached(text: String): String {
        cache[text]?.let { return it }

        val translated = translateTextSuspend(text)
        cache[text] = translated
        return translated
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun translateTextSuspend(text: String): String {
        return suspendCancellableCoroutine { cont ->
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.RUSSIAN)
                .build()

            val translator = Translation.getClient(options)

            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    translator.translate(text)
                        .addOnSuccessListener { translated ->
                            if (cont.isActive) {
                                cont.resume(translated) {}
                            }
                        }
                        .addOnFailureListener { e ->
                            if (cont.isActive) {
                                cont.resume("Ошибка перевода: ${e.localizedMessage}") {}
                            }
                        }
                }
                .addOnFailureListener { e ->
                    if (cont.isActive) {
                        cont.resume("Ошибка загрузки модели: ${e.localizedMessage}") {}
                    }
                }
        }
    }

}