package com.ice.restring

import android.util.AttributeSet
import android.view.View
import android.widget.TextView

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

import org.junit.Assert.assertSame
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.`when`

@RunWith(RobolectricTestRunner::class)
class ViewTransformerManagerTest {

    private var transformerManager: ViewTransformerManager? = null

    @Before
    fun setUp() {
        transformerManager = ViewTransformerManager()
    }

    @Test
    fun shouldTransformView() {
        val textView = TextView(RuntimeEnvironment.application)

        val transformer = Mockito.mock<ViewTransformerManager.Transformer>(ViewTransformerManager.Transformer::class.java)
        doReturn(TextView::class.java).`when`(transformer).viewType
        `when`(transformer.transform(any(), any())).thenReturn(textView)
        transformerManager!!.registerTransformer(transformer)

        val transformedView = transformerManager!!.transform(
                TextView(RuntimeEnvironment.application),
                Mockito.mock(AttributeSet::class.java)
        )

        assertSame(textView, transformedView)
    }
}