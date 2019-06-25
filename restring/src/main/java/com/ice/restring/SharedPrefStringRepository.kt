package com.ice.restring

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import java.util.*

/**
 * A StringRepository which saves/loads the strings in Shared Preferences.
 * it also keeps the strings in memory by using MemoryStringRepository internally for faster access.
 *
 *
 * it's not ThreadSafe.
 */
internal class SharedPrefStringRepository(context: Context) : StringRepository {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }
    private val memoryStringRepository = MemoryStringRepository()

    init {
        loadStrings()
    }

    override fun setStrings(language: String, strings: Map<String, String>) {
        memoryStringRepository.setStrings(language, strings)
        saveStrings(language, strings)
    }

    override fun setString(language: String, key: String, value: String) {
        memoryStringRepository.setString(language, key, value)

        val keyValues = memoryStringRepository.getStrings(language)
        keyValues[key] = value
        saveStrings(language, keyValues)
    }

    override fun getString(language: String, key: String): String? {
        return memoryStringRepository.getString(language, key)
    }

    override fun getStrings(language: String): Map<String, String> {
        return memoryStringRepository.getStrings(language)
    }

    private fun loadStrings() {
        val strings = sharedPreferences.all
        for ((language, value1) in strings) {
            val value = value1 as? String ?: continue

            val keyValues = deserializeKeyValues(value)
            memoryStringRepository.setStrings(language, keyValues)
        }
    }

    private fun saveStrings(language: String, strings: Map<String, String>) {
        val content = serializeKeyValues(strings)
        sharedPreferences.edit()
                .putString(language, content)
                .apply()
    }

    private fun deserializeKeyValues(content: String): Map<String, String> {
        val keyValues = LinkedHashMap<String, String>()
        try {
            val jsonObject = JSONObject(content)
            for (key in jsonObject.keys()) {
                keyValues[key] = jsonObject[key] as String
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return keyValues
    }

    private fun serializeKeyValues(keyValues: Map<String, String>): String {
        return JSONObject(keyValues).toString()
    }

    companion object {
        private const val SHARED_PREF_NAME = "Restrings"
    }
}
