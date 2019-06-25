package com.ice.restring

import android.util.AttributeSet
import android.widget.TextView
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doReturn
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ViewTransformerManagerTest {

    private lateinit var transformerManager: ViewTransformerManager

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
        transformerManager.registerTransformer(transformer)

        val transformedView = transformerManager.transform(
                TextView(RuntimeEnvironment.application),
                Mockito.mock(AttributeSet::class.java)
        )

        assertSame(textView, transformedView)
    }
}