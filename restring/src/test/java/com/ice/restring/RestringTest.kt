package com.ice.restring

import android.support.design.widget.BottomNavigationView
import android.widget.TextView
import android.widget.Toolbar
import com.ice.restring.activity.TestActivity
import com.ice.restring.shadow.MyShadowAsyncTask
import org.hamcrest.core.StringStartsWith.startsWith
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [MyShadowAsyncTask<*, *, *>::class])
class RestringTest {

    private val language: String
        get() = Locale.getDefault().language

    @Before
    fun setUp() {
        Restring.init(
                RuntimeEnvironment.application,
                RestringConfig.Builder()
                        .persist(false)
                        .stringsLoader(MyStringLoader())
                        .build()
        )
        Robolectric.flushBackgroundThreadScheduler()
    }

    @Test
    fun shouldInflateAndTransformViewsOnActivityCreation() {
        val languages = listOf("en", "fa", "de")
        for (lang in languages) {
            Locale.setDefault(Locale(lang))
            val activityController = Robolectric.buildActivity<TestActivity>(TestActivity::class.java)
            val activity = activityController.create().start().resume().visible().get()
            val viewGroup = activity.findViewById(R.id.root_container)

            val childCount = viewGroup.getChildCount()
            for (i in 0 until childCount) {
                val view = viewGroup.getChildAt(i)
                if (view is TextView) {
                    assertThat("TextView[text]", (view as TextView).text.toString(), startsWith(language))
                    assertThat("TextView[hint]", (view as TextView).hint.toString(), startsWith(language))
                } else if (view is Toolbar) {
                    assertThat("Toolbar[title]", (view as Toolbar).title.toString(), startsWith(language))
                } else if (view is android.support.v7.widget.Toolbar) {
                    assertThat("Toolbar[title]", (view as android.support.v7.widget.Toolbar).title.toString(), startsWith(language))
                } else if (view is BottomNavigationView) {
                    val bottomNavigationView = view as BottomNavigationView
                    val itemCount = bottomNavigationView.menu.size()
                    for (item in 0 until itemCount) {
                        assertThat("BottomNavigationView#$item[title]",
                                bottomNavigationView.menu.getItem(item).title.toString(), startsWith(language))
                        assertThat("BottomNavigationView#$item[titleCondensed]",
                                bottomNavigationView.menu.getItem(item).titleCondensed.toString(), startsWith(language))
                    }
                }
            }

            activityController.pause().stop().destroy()
        }
    }

    private inner class MyStringLoader : Restring.StringsLoader {

        override val languages: List<String>
            get() = listOf("en", "fa", "de")

        override fun getStrings(language: String): Map<String, String> {
            val strings = LinkedHashMap<String, String>()
            strings["header"] = language + "_" + "header"
            strings["header_hint"] = language + "_" + "hint"
            strings["menu1title"] = language + "_" + "Menu 1"
            strings["menu1titleCondensed"] = language + "_" + "Menu1"
            strings["menu2title"] = language + "_" + "Menu 2"
            strings["menu2titleCondensed"] = language + "_" + "Menu2"
            strings["menu3title"] = language + "_" + "Menu 3"
            strings["menu3titleCondensed"] = language + "_" + "Menu3"
            return strings
        }
    }
}