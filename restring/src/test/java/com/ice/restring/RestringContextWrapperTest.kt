package com.ice.restring

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ice.restring.application.TestApplication
import com.ice.restring.shadow.MyShadowAssetManager

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadow.api.Shadow

import java.util.Locale

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [MyShadowAssetManager::class], application = TestApplication::class)
class RestringContextWrapperTest {

    private var restringContextWrapper: RestringContextWrapper? = null
    private var context: Context? = null
    private var originalResources: Resources? = null
    @Mock
    private val stringRepository: StringRepository? = null
    @Mock
    private val transformerManager: ViewTransformerManager? = null

    private val language: String
        get() = Locale.getDefault().language

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        context = RuntimeEnvironment.application
        originalResources = context!!.resources

        `when`(transformerManager!!.transform(any(), any())).thenAnswer { i -> i.getArgument(0) }
        restringContextWrapper = RestringContextWrapper.wrap(
                context,
                stringRepository,
                transformerManager
        )
    }

    @Test
    fun shouldWrapResourcesAndGetStringsFromRepository() {
        (Shadow.extract<Any>(originalResources!!.assets) as MyShadowAssetManager)
                .addResourceEntryNameForTesting(STR_RES_ID, STR_KEY)

        doReturn(STR_VALUE).`when`(stringRepository).getString(language, STR_KEY)

        val real = restringContextWrapper!!.resources.getString(STR_RES_ID)

        assertEquals(STR_VALUE, real)
    }

    @Test
    fun shouldProvideCustomLayoutInflaterToApplyViewTransformation() {
        val layoutInflater = restringContextWrapper!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        assertTrue(layoutInflater is RestringLayoutInflater)

        val viewGroup = layoutInflater.inflate(R.layout.test_layout, null, false) as ViewGroup

        val captor = ArgumentCaptor.forClass<View, View>(View::class.java)
        verify(transformerManager, atLeastOnce()).transform(captor.capture(), any())
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            captor.allValues.contains(child)
        }
    }

    companion object {
        private val STR_RES_ID = 0x7f0f0123
        private val STR_KEY = "STR_KEY"
        private val STR_VALUE = "STR_VALUE"
    }
}
