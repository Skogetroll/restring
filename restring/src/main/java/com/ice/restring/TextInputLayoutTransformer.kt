package com.ice.restring

import android.content.res.Resources
import android.support.design.widget.TextInputLayout
import android.util.AttributeSet
import android.view.View

internal class TextInputLayoutTransformer : ViewTransformerManager.Transformer {

    override val viewType: Class<out View>
        get() = TextInputLayout::class.java

    override fun transform(view: View?, attrs: AttributeSet): View? {
        if (view == null || !viewType.isInstance(view)) {
            return view
        }
        val resources = view.context.resources

        for (index in 0 until attrs.attributeCount) {
            val attributeName = attrs.getAttributeName(index)
            val value: String?
            when (attributeName) {
                ATTRIBUTE_ANDROID_HINT, ATTRIBUTE_HINT -> {
                    value = attrs.getAttributeValue(index)
                    if (value != null && value.startsWith("@")) {
                        setHintForView(view, resources.getString(
                                attrs.getAttributeResourceValue(index, 0)))

                    }
                }
                else -> {
                }
            }
        }
        return view
    }

    private fun setHintForView(view: View, text: String) {
        (view as TextInputLayout).hint = text
    }

    companion object {

        private const val ATTRIBUTE_HINT = "hint"
        private const val ATTRIBUTE_ANDROID_HINT = "android:hint"
    }
}
