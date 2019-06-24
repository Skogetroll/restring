package com.ice.restring

import java.util.LinkedHashMap

/**
 * A StringRepository which keeps the strings ONLY in memory.
 *
 *
 * it's not ThreadSafe.
 */
internal class MemoryStringRepository : StringRepository {

    private val strings = LinkedHashMap<String, MutableMap<String, String>>()

    override fun setStrings(language: String, strings: Map<String, String>) {
        this.strings[language] = strings.toMutableMap()
    }

    override fun setString(language: String, key: String, value: String) {
        if (!strings.containsKey(language)) {
            strings[language] = LinkedHashMap()
        }
        strings[language]?.put(key, value)
    }

    override fun getString(language: String, key: String): String? {
        return if (!strings.containsKey(language) || !strings[language]!!.containsKey(key)) {
            null
        } else strings[language]?.get(key)
    }

    override fun getStrings(language: String): MutableMap<String, String> {
        return if (!strings.containsKey(language)) {
            LinkedHashMap()
        } else LinkedHashMap(strings[language])

    }
}