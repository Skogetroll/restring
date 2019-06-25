package com.ice.restring

import android.util.AttributeSet
import android.view.View
import android.widget.TextView

/**
 * A transformer which transforms TextView(or any view extends it like Button, EditText, ...):
 * it transforms "text" & "hint" attributes.
 */
internal class TextViewTransformer : ViewTransformerManager.Transformer<TextView> {

    override val viewType: Class<out TextView>
        get() = TextView::class.java

    override fun transform(view: View?, attrs: AttributeSet): TextView? {
        if (view as? TextView == null) {
            return null
        }
        val resources = view.context.resources

        for (index in 0 until attrs.attributeCount) {
            when (attrs.getAttributeName(index)) {
                ATTRIBUTE_ANDROID_TEXT, ATTRIBUTE_TEXT -> {
                    val value: String? = attrs.getAttributeValue(index)
                    if (value != null && value.startsWith("@")) {
                        setTextForView(view, resources.getString(attrs.getAttributeResourceValue(index, 0)))
                    }
                }
                ATTRIBUTE_ANDROID_HINT, ATTRIBUTE_HINT -> {
                    val value: String? = attrs.getAttributeValue(index)
                    if (value != null && value.startsWith("@")) {
                        setHintForView(view, resources.getString(attrs.getAttributeResourceValue(index, 0)))
                    }
                }
            }
        }
        return view
    }

    private fun setTextForView(view: TextView, text: String) {
        view.text = text
    }

    private fun setHintForView(view: TextView, text: String) {
        view.hint = text
    }

    companion object {

        private const val ATTRIBUTE_TEXT = "text"
        private const val ATTRIBUTE_ANDROID_TEXT = "android:text"
        private const val ATTRIBUTE_HINT = "hint"
        private const val ATTRIBUTE_ANDROID_HINT = "android:hint"
    }
}
