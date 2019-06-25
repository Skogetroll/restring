package com.ice.restring

import android.support.design.widget.TextInputLayout
import android.util.AttributeSet
import android.view.View

internal class TextInputLayoutTransformer : ViewTransformerManager.Transformer<TextInputLayout> {

    override val viewType: Class<out TextInputLayout>
        get() = TextInputLayout::class.java

    override fun transform(view: View?, attrs: AttributeSet): TextInputLayout? {
        if (view as? TextInputLayout == null) {
            return null
        }
        val resources = view.context.resources

        for (index in 0 until attrs.attributeCount) {
            when (attrs.getAttributeName(index)) {
                ATTRIBUTE_ANDROID_HINT, ATTRIBUTE_HINT -> {
                    val value: String? = attrs.getAttributeValue(index)
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

    private fun setHintForView(view: TextInputLayout, text: String) {
        view.hint = text
    }

    companion object {

        private const val ATTRIBUTE_HINT = "hint"
        private const val ATTRIBUTE_ANDROID_HINT = "android:hint"
    }
}
