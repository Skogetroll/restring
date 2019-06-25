package com.ice.restring

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class MemoryStringRepositoryTest {

    private lateinit var stringRepository: StringRepository

    @Before
    fun setUp() {
        stringRepository = MemoryStringRepository()
    }

    @Test
    fun shouldSetAndGetStringPairs() {
        val LANGUAGE = "en"
        val strings = generateStrings(10)

        stringRepository.setStrings(LANGUAGE, strings)

        assertEquals(strings, stringRepository.getStrings(LANGUAGE))
    }

    @Test
    fun shouldGetSingleString() {
        val LANGUAGE = "en"
        val STR_COUNT = 10
        val strings = generateStrings(STR_COUNT)
        stringRepository.setStrings(LANGUAGE, strings)

        for (i in 0 until STR_COUNT) {
            assertEquals(stringRepository.getString(LANGUAGE, "key$i"), "value$i")
        }
    }

    @Test
    fun shouldSetSingleString() {
        val LANGUAGE = "en"
        val STR_COUNT = 10
        val strings = generateStrings(STR_COUNT)

        stringRepository.setStrings(LANGUAGE, strings)
        stringRepository.setString(LANGUAGE, "key5", "aNewValue")

        assertEquals(stringRepository.getString(LANGUAGE, "key5"), "aNewValue")
    }

    private fun generateStrings(count: Int): Map<String, String> {
        val strings = LinkedHashMap<String, String>()
        for (i in 0 until count) {
            strings["key$i"] = "value$i"
        }
        return strings
    }
}