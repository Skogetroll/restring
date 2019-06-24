package com.ice.restring

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.stubbing.Answer
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [16, 19, 21, 23, 24, 26])
class RestringLayoutInflaterTest {

    @Mock
    private val transformerManager: ViewTransformerManager? = null
    private var restringLayoutInflater: RestringLayoutInflater? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        `when`(transformerManager!!.transform(any(), any())).thenAnswer({ invocation -> invocation.getArgument(0) } as Answer<View>
        )
        RuntimeEnvironment.application.setTheme(R.style.Theme_AppCompat)
        restringLayoutInflater = RestringLayoutInflater(
                LayoutInflater.from(RuntimeEnvironment.application),
                RuntimeEnvironment.application,
                transformerManager,
                false
        )
    }

    @Test
    fun shouldTransformViewsOnInflatingLayouts() {
        val viewGroup = restringLayoutInflater!!.inflate(R.layout.test_layout, null, false) as ViewGroup

        val captor = ArgumentCaptor.forClass<View, View>(View::class.java)
        verify(transformerManager, atLeastOnce()).transform(captor.capture(), any())
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            captor.allValues.contains(child)
        }
    }
}