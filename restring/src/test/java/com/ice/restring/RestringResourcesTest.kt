package com.ice.restring

import android.content.res.Resources
import android.text.Html
import android.text.TextUtils

import com.ice.restring.shadow.MyShadowAssetManager

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

import java.util.Locale

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.mockito.Mockito.doReturn

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [MyShadowAssetManager::class])
class RestringResourcesTest {

    @Mock
    private val repository: StringRepository? = null
    private var resources: Resources? = null
    private var restringResources: RestringResources? = null

    private val language: String
        get() = Locale.getDefault().language

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        resources = RuntimeEnvironment.application.resources

        restringResources = Mockito.spy(RestringResources(resources!!, repository!!))
        doReturn(STR_KEY).`when`(restringResources).getResourceEntryName(STR_RES_ID)
    }

    @Test
    fun shouldGetStringFromRepositoryIfExists() {
        doReturn(STR_VALUE).`when`(repository).getString(language, STR_KEY)

        val stringValue = restringResources!!.getString(STR_RES_ID)

        assertEquals(STR_VALUE, stringValue)
    }

    @Test
    fun shouldGetStringFromResourceIfNotExists() {
        doReturn(null).`when`(repository).getString(language, STR_KEY)

        val stringValue = restringResources!!.getString(STR_RES_ID)

        val expected = MyShadowAssetManager().getResourceText(STR_RES_ID).toString()
        assertEquals(expected, stringValue)
    }

    @Test
    fun shouldGetStringWithParamsFromRepositoryIfExists() {
        val param = "PARAM"
        doReturn(STR_VALUE_WITH_PARAM).`when`(repository).getString(language, STR_KEY)

        val stringValue = restringResources!!.getString(STR_RES_ID, param)

        assertEquals(String.format(STR_VALUE_WITH_PARAM, param), stringValue)
    }

    @Test
    fun shouldGetStringWithParamsFromResourceIfNotExists() {
        val param = "PARAM"
        doReturn(null).`when`(repository).getString(language, STR_KEY)

        val stringValue = restringResources!!.getString(STR_RES_ID, param)

        val expected = MyShadowAssetManager().getResourceText(STR_RES_ID).toString()
        assertEquals(expected, stringValue)
    }

    @Test
    fun shouldGetHtmlTextFromRepositoryIfExists() {
        doReturn(STR_VALUE_HTML).`when`(repository).getString(language, STR_KEY)

        val realValue = restringResources!!.getText(STR_RES_ID)

        val expected = Html.fromHtml(STR_VALUE_HTML, Html.FROM_HTML_MODE_COMPACT)
        assertTrue(TextUtils.equals(expected, realValue))
    }

    @Test
    fun shouldGetHtmlTextFromResourceIfNotExists() {
        doReturn(null).`when`(repository).getString(language, STR_KEY)

        val realValue = restringResources!!.getText(STR_RES_ID)

        val expected = MyShadowAssetManager().getResourceText(STR_RES_ID)
        assertTrue(TextUtils.equals(expected, realValue))
    }

    @Test
    fun shouldReturnDefaultHtmlTextFromRepositoryIfResourceIdIsInvalid() {
        val def = Html.fromHtml("<b>def</b>", Html.FROM_HTML_MODE_COMPACT)
        doReturn(null).`when`(repository).getString(language, STR_KEY)

        val realValue = restringResources!!.getText(0, def)

        assertTrue(TextUtils.equals(def, realValue))
    }

    companion object {
        private val STR_RES_ID = 0x7f0f0123
        private val STR_KEY = "STR_KEY"
        private val STR_VALUE = "STR_VALUE"
        private val STR_VALUE_WITH_PARAM = "STR_VALUE %s"
        private val STR_VALUE_HTML = "STR_<b>value</b>"
    }
}