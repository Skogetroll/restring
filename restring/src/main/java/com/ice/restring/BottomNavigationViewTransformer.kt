package com.ice.restring

import android.annotation.SuppressLint
import android.content.res.Resources
import android.support.design.widget.BottomNavigationView
import android.util.AttributeSet
import android.util.Pair
import android.util.Xml
import android.view.View
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.*

/**
 * A transformer which transforms BottomNavigationView: it transforms the texts coming from the menu.
 */
internal class BottomNavigationViewTransformer : ViewTransformerManager.Transformer {

    override val viewType: Class<out View>
        get() = BottomNavigationView::class.java

    override fun transform(view: View?, attrs: AttributeSet): View? {
        if (view == null || !viewType.isInstance(view)) {
            return view
        }
        val resources = view.context.resources
        val bottomNavigationView = view as BottomNavigationView

        for (index in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(index)
            when (attributeName) {
                ATTRIBUTE_APP_MENU, ATTRIBUTE_MENU -> {
                    val value = attrs.getAttributeValue(index)
                    if (value != null && value.startsWith("@")) {
                        val resId = attrs.getAttributeResourceValue(index, 0)
                        val itemStrings = getMenuItemsStrings(resources, resId)

                        for ((key, value1) in itemStrings) {

                            if (value1.title != 0) {
                                bottomNavigationView.menu.findItem(key).title = resources.getString(value1.title)
                            }
                            if (value1.titleCondensed != 0) {
                                bottomNavigationView.menu.findItem(key).titleCondensed = resources.getString(value1.titleCondensed)
                            }
                        }
                    }
                }
            }
        }

        return view
    }

    private fun getMenuItemsStrings(resources: Resources, resId: Int): Map<Int, MenuItemStrings> {
        val parser = resources.getLayout(resId)
        val attrs = Xml.asAttributeSet(parser)
        try {
            return parseMenu(parser, attrs)
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
            return HashMap()
        } catch (e: IOException) {
            e.printStackTrace()
            return HashMap()
        }

    }

    @SuppressLint("UseSparseArrays")
    @Throws(XmlPullParserException::class, IOException::class)
    private fun parseMenu(parser: XmlPullParser, attrs: AttributeSet): Map<Int, MenuItemStrings> {

        val menuItems = HashMap<Int, MenuItemStrings>()
        var eventType = parser.eventType
        var tagName: String

        // This loop will skip to the menu start tag
        do {
            if (eventType == XmlPullParser.START_TAG) {
                tagName = parser.name
                if (tagName == XML_MENU) {
                    eventType = parser.next()
                    break
                }

                throw RuntimeException("Expecting menu, got $tagName")
            }
            eventType = parser.next()
        } while (eventType != XmlPullParser.END_DOCUMENT)

        var reachedEndOfMenu = false
        var menuLevel = 0
        while (!reachedEndOfMenu) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    tagName = parser.name
                    if (tagName == XML_ITEM) {
                        val item = parseMenuItem(attrs)
                        if (item != null) {
                            menuItems[item.first] = item.second
                        }
                    } else if (tagName == XML_MENU) {
                        menuLevel++
                    }
                }

                XmlPullParser.END_TAG -> {
                    tagName = parser.name
                    if (tagName == XML_MENU) {
                        menuLevel--
                        if (menuLevel <= 0) {
                            reachedEndOfMenu = true
                        }
                    }
                }

                XmlPullParser.END_DOCUMENT -> reachedEndOfMenu = true
            }

            eventType = parser.next()
        }
        return menuItems
    }

    private fun parseMenuItem(attrs: AttributeSet): Pair<Int, MenuItemStrings>? {
        var menuId = 0
        var menuItemStrings: MenuItemStrings? = null
        val attributeCount = attrs.attributeCount
        for (index in 0 until attributeCount) {
            when (attrs.getAttributeName(index)) {
                ATTRIBUTE_ANDROID_ID, ATTRIBUTE_ID -> {
                    menuId = attrs.getAttributeResourceValue(index, 0)
                }
                ATTRIBUTE_ANDROID_TITLE, ATTRIBUTE_TITLE -> {
                    val value = attrs.getAttributeValue(index)
                    if (value != null && value.startsWith("@")) {
                        if (menuItemStrings == null) {
                            menuItemStrings = MenuItemStrings()
                        }
                        menuItemStrings.title = attrs.getAttributeResourceValue(index, 0)
                    }
                }
                ATTRIBUTE_ANDROID_TITLE_CONDENSED, ATTRIBUTE_TITLE_CONDENSED -> {
                    val value = attrs.getAttributeValue(index)
                    if (value != null && value.startsWith("@")) {
                        if (menuItemStrings == null) {
                            menuItemStrings = MenuItemStrings()
                        }
                        menuItemStrings.titleCondensed = attrs.getAttributeResourceValue(index, 0)
                    }
                }
            }
        }
        return if (menuId != 0 && menuItemStrings != null)
            Pair(menuId, menuItemStrings)
        else
            null
    }

    private class MenuItemStrings {
        var title: Int = 0
        var titleCondensed: Int = 0
    }

    companion object {

        private const val ATTRIBUTE_MENU = "menu"
        private const val ATTRIBUTE_APP_MENU = "app:menu"
        private const val ATTRIBUTE_ID = "id"
        private const val ATTRIBUTE_ANDROID_ID = "android:id"
        private const val ATTRIBUTE_TITLE = "title"
        private const val ATTRIBUTE_ANDROID_TITLE = "android:title"
        private const val ATTRIBUTE_TITLE_CONDENSED = "titleCondensed"
        private const val ATTRIBUTE_ANDROID_TITLE_CONDENSED = "android:titleCondensed"
        private const val XML_MENU = "menu"
        private const val XML_ITEM = "item"
    }
}
