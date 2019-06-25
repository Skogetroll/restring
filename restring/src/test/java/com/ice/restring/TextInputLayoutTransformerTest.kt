package com.ice.restring

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.ice.restring.application.TestApplication
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class TextInputLayoutTransformerTest {

    private lateinit var transformer: TextInputLayoutTransformer

    private val context: Context
        get() {
            val context = Mockito.spy<Application>(RuntimeEnvironment.application)
            val resources = Mockito.spy<Resources>(context.getResources())

            doReturn(resources).`when`<Context>(context).resources
            doReturn(HINT_ATTR_VALUE).`when`(resources).getString(HINT_RES_ID)

            return context
        }

    @Before
    fun setUp() {
        transformer = TextInputLayoutTransformer()
    }

    @Test
    fun shouldTransformTextInputLayout() {
        val context = context

        var view = transformer.transform(TextInputLayout(context), getAttributeSet(false))

        assertTrue(view is TextInputLayout)
        assertEquals((view as TextInputLayout).hint, HINT_ATTR_VALUE)

        view = transformer.transform(TextInputLayout(context), getAttributeSet(true))

        assertTrue(view is TextInputLayout)
        assertEquals((view as TextInputLayout).hint, HINT_ATTR_VALUE)
    }

    @Test
    fun shouldTransformExtendedViews() {
        val context = context

        var view = transformer.transform(ExtendedTextInputLayout(context), getAttributeSet(false))

        assertTrue(view is ExtendedTextInputLayout)
        assertEquals((view as ExtendedTextInputLayout).hint, HINT_ATTR_VALUE)

        view = transformer.transform(ExtendedTextInputLayout(context), getAttributeSet(true))

        assertTrue(view is ExtendedTextInputLayout)
        assertEquals((view as ExtendedTextInputLayout).hint, HINT_ATTR_VALUE)
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
        `when`(attributeSet.attributeCount).thenReturn(HINT_ATTR_INDEX + 2)

        `when`(attributeSet.getAttributeName(anyInt())).thenReturn("other_attribute")
        `when`(attributeSet.getAttributeName(HINT_ATTR_INDEX)).thenReturn((if (withAndroidPrefix) "android:" else "") + HINT_ATTR_KEY)
        `when`(attributeSet.getAttributeValue(HINT_ATTR_INDEX)).thenReturn("@$HINT_RES_ID")
        `when`(attributeSet.getAttributeResourceValue(eq(HINT_ATTR_INDEX), anyInt())).thenReturn(HINT_RES_ID)

        return attributeSet
    }

    private class ExtendedTextInputLayout : TextInputLayout {

        constructor(context: Context) : super(context) {}

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

        constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    }

    companion object {

        private const val HINT_ATTR_INDEX = 2
        private const val HINT_RES_ID = 0x7f0f0124
        private const val HINT_ATTR_KEY = "hint"
        private const val HINT_ATTR_VALUE = "HINT_ATTR_VALUE"
    }
}
