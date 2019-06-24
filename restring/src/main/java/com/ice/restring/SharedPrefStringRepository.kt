package com.ice.restring

import android.content.Context
import android.content.SharedPreferences

import java.util.LinkedHashMap

/**
 * A StringRepository which saves/loads the strings in Shared Preferences.
 * it also keeps the strings in memory by using MemoryStringRepository internally for faster access.
 *
 *
 * it's not ThreadSafe.
 */
internal class SharedPrefStringRepository(context: Context) : StringRepository {

    private var sharedPreferences: SharedPreferences? = null
    private val memoryStringRepository = MemoryStringRepository()

    init {
        initSharedPreferences(context)
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

    private fun initSharedPreferences(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun loadStrings() {
        val strings = sharedPreferences!!.all
        for ((language, value1) in strings) {
            if (value1 !is String) {
                continue
            }

            val value = value1 as String
            val keyValues = deserializeKeyValues(value)
            memoryStringRepository.setStrings(language, keyValues)
        }
    }

    private fun saveStrings(language: String, strings: Map<String, String>) {
        val content = serializeKeyValues(strings)
        sharedPreferences!!.edit()
                .putString(language, content)
                .apply()
    }

    private fun deserializeKeyValues(content: String): Map<String, String> {
        val keyValues = LinkedHashMap<String, String>()
        val items = content.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        for (item in items) {
            val itemKeyValue = item.split("=".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            keyValues[itemKeyValue[0]] = itemKeyValue[1].replace(",,".toRegex(), ",")
        }
        return keyValues
    }

    private fun serializeKeyValues(keyValues: Map<String, String>): String {
        val content = StringBuilder()
        for ((key, value) in keyValues) {
            content.append(key)
                    .append("=")
                    .append(value.replace(",".toRegex(), ",,"))
                    .append(",")
        }
        content.deleteCharAt(content.length - 1)
        return content.toString()
    }

    companion object {
        private val SHARED_PREF_NAME = "Restrings"
    }
}
