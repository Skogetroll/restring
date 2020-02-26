package com.ice.restring

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ice.restring.application.TestApplication
import com.ice.restring.shadow.MyShadowAssetManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadow.api.Shadow
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [MyShadowAssetManager::class], application = TestApplication::class)
class RestringContextWrapperTest {

    private lateinit var restringContextWrapper: RestringContextWrapper
    private lateinit var context: Context
    private lateinit var originalResources: Resources
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
        originalResources = context.resources

        `when`(transformerManager!!.transform(any(), any())).thenAnswer { i -> i.getArgument(0) }
        restringContextWrapper = RestringContextWrapper.wrap(
                context,
                stringRepository!!,
                transformerManager
        )
    }

    @Test
    fun shouldWrapResourcesAndGetStringsFromRepository() {
        (Shadow.extract<MyShadowAssetManager>(originalResources.assets))
                .addResourceEntryNameForTesting(STR_RES_ID, STR_KEY)

        doReturn(STR_VALUE).`when`(stringRepository).getString(language, STR_KEY)

        val real = restringContextWrapper.resources.getString(STR_RES_ID)

        assertEquals(STR_VALUE, real)
    }

    @Test
    fun shouldProvideCustomLayoutInflaterToApplyViewTransformation() {
        val layoutInflater = restringContextWrapper.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        assertTrue(layoutInflater is RestringLayoutInflater)

        val viewGroup = layoutInflater.inflate(R.layout.test_layout, null, false) as ViewGroup

        val captor = ArgumentCaptor.forClass(View::class.java)
        verify(transformerManager, atLeastOnce()).transform(captor.capture(), any())
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            captor.allValues.contains(child)
        }
    }

    companion object {
        private const val STR_RES_ID = 0x7f0f0123
        private const val STR_KEY = "STR_KEY"
        private const val STR_VALUE = "STR_VALUE"
    }
}
