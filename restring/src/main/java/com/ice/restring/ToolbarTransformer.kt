package com.ice.restring

import android.annotation.TargetApi
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.Toolbar

/**
 * A transformer which transforms Toolbar: it transforms the text set as title.
 */
internal class ToolbarTransformer : ViewTransformerManager.Transformer<Toolbar> {

    override val viewType: Class<out Toolbar>
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        get() = Toolbar::class.java

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun transform(view: View?, attrs: AttributeSet): Toolbar? {
        if (view as? Toolbar == null) {
            return null
        }
        val resources = view.context.resources

        for (index in 0 until attrs.attributeCount) {
            when (attrs.getAttributeName(index)) {
                ATTRIBUTE_ANDROID_TITLE, ATTRIBUTE_TITLE -> {
                    val value: String? = attrs.getAttributeValue(index)
                    if (value != null && value.startsWith("@")) {
                        setTitleForView(view, resources.getString(attrs.getAttributeResourceValue(index, 0)))
                    }
                }
            }
        }
        return view
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setTitleForView(view: Toolbar, text: String) {
        view.title = text
    }

    companion object {

        private const val ATTRIBUTE_TITLE = "title"
        private const val ATTRIBUTE_ANDROID_TITLE = "android:title"
    }
}