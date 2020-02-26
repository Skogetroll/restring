package com.ice.restring

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.Toolbar
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ToolbarTransformerTest {

    private lateinit var transformer: ToolbarTransformer

    private val context: Context
        get() {
            val context = Mockito.spy<Application>(RuntimeEnvironment.application)
            val resources = Mockito.spy<Resources>(context.getResources())

            doReturn(resources).`when`<Context>(context).resources
            doReturn(TITLE_ATTR_VALUE).`when`(resources).getString(TITLE_RES_ID)

            return context
        }

    @Before
    fun setUp() {
        transformer = ToolbarTransformer()
    }

    @Test
    fun shouldTransformToolbar() {
        val context = context

        var view = transformer.transform(Toolbar(context), getAttributeSet(false))

        assertTrue(view is Toolbar)
        assertEquals((view as Toolbar).title, TITLE_ATTR_VALUE)

        view = transformer.transform(Toolbar(context), getAttributeSet(true))

        assertTrue(view is Toolbar)
        assertEquals((view as Toolbar).title, TITLE_ATTR_VALUE)
    }

    @Test
    fun shouldRejectOtherViewTypes() {
        val context = context
        val attributeSet = getAttributeSet(false)
        val recyclerView = RecyclerView(context)

        val view = transformer.transform(recyclerView, attributeSet)

        assertSame(view, recyclerView)
        verifyZeroInteractions(attributeSet)
    }

    private fun getAttributeSet(withAndroidPrefix: Boolean): AttributeSet {
        val attributeSet = Mockito.mock<AttributeSet>(AttributeSet::class.java)
        `when`(attributeSet.attributeCount).thenReturn(TITLE_ATTR_INDEX + 2)

        `when`(attributeSet.getAttributeName(anyInt())).thenReturn("other_attribute")
        `when`(attributeSet.getAttributeName(TITLE_ATTR_INDEX)).thenReturn((if (withAndroidPrefix) "android:" else "") + TITLE_ATTR_KEY)
        `when`(attributeSet.getAttributeValue(TITLE_ATTR_INDEX)).thenReturn("@$TITLE_RES_ID")
        `when`(attributeSet.getAttributeResourceValue(eq(TITLE_ATTR_INDEX), anyInt())).thenReturn(TITLE_RES_ID)

        return attributeSet
    }

    companion object {
        private const val TITLE_ATTR_INDEX = 3
        private const val TITLE_RES_ID = 0x7f0f0123
        private const val TITLE_ATTR_KEY = "title"
        private const val TITLE_ATTR_VALUE = "TITLE_ATTR_VALUE"
    }
}
