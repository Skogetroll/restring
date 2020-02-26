package com.ice.restring

import android.content.res.Resources
<<<<<<< HEAD
import android.os.Build
import android.text.Html
=======
import android.icu.text.PluralRules
import android.os.Build
import android.support.annotation.PluralsRes
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.text.Html
import java.util.*
>>>>>>> develop

/**
 * This is the wrapped resources which will be provided by Restring.
 * For getting strings and texts, it checks the strings repository first and if there's a new string
 * that will be returned, otherwise it will fallback to the original resource strings.
 */
<<<<<<< HEAD
class RestringResources(
    res: Resources,
    private val stringRepository: StringRepository,
    private val missingTranslationHandler: Restring.MissingTranslationHandler? = null
) : Resources(res.assets, res.displayMetrics, res.configuration) {


    @Throws(NotFoundException::class)
    override fun getString(id: Int): String {
=======
internal class RestringResources(res: Resources,
                                 private val stringRepository: StringRepository) : Resources(res.assets, res.displayMetrics, res.configuration) {

    @Throws(NotFoundException::class)
    override fun getString(@StringRes id: Int): String {
>>>>>>> develop
        val value = getStringFromRepository(id)
        return value ?: super.getString(id)
    }

    @Throws(NotFoundException::class)
<<<<<<< HEAD
    override fun getString(id: Int, vararg formatArgs: Any): String {
        val value = getStringFromRepository(id)
        val missingFromRepo = value == null
        val result = if (value != null) {
            String.format(value, *formatArgs)
        } else super.getString(id, *formatArgs)
        if (missingFromRepo) {
            handleMissingString(id, result)
        }
        return result
    }

    @Throws(NotFoundException::class)
    override fun getText(id: Int): CharSequence {
        val value = getStringFromRepository(id)
        val missingFromRepo = value == null
        val res = value?.let { fromHtml(it) } ?: super.getText(id)
        if (missingFromRepo) {
            handleMissingString(id, res.toString())
        }
        return res
    }

    override fun getText(id: Int, def: CharSequence): CharSequence {
        val value = getStringFromRepository(id)
        val missingFromRepo = value == null
        val res = value?.let { fromHtml(it) } ?: super.getText(id, def)
        if (missingFromRepo) {
            handleMissingString(id, res.toString())
        }
        return res
    }

    private fun handleMissingString(id: Int, valueInOriginalResources: String) {
        missingTranslationHandler?.let {
            val lang = RestringUtil.currentLanguage
            val stringKey = getResourceEntryName(id)
            it.missingTranslation(lang, stringKey, valueInOriginalResources)
        }
    }

    private fun getStringFromRepository(id: Int): String? {
        val lang = RestringUtil.currentLanguage
        val stringKey = getResourceEntryName(id)
        return stringRepository.getString(lang, stringKey)
    }

    private fun fromHtml(source: String): CharSequence {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Html.fromHtml(source)
        } else {
            Html.fromHtml(source, Html.FROM_HTML_MODE_COMPACT)
        }
=======
    override fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
        val value = getStringFromRepository(id)
        return if (value != null) {
            String.format(value, *formatArgs)
        } else super.getString(id, *formatArgs)
    }

    @Throws(NotFoundException::class)
    override fun getText(@StringRes id: Int): CharSequence {
        val value = getStringFromRepository(id)
        return value?.let { fromHtml(it) } ?: super.getText(id)
    }

    @Throws(NotFoundException::class)
    override fun getText(@StringRes id: Int, def: CharSequence): CharSequence {
        val value = getStringFromRepository(id)
        return value?.let { fromHtml(it) } ?: super.getText(id, def)
    }

    private fun getStringFromRepository(@StringRes id: Int): String? = try {
        val stringKey = getResourceEntryName(id)
        stringRepository.getString(RestringUtil.currentLanguage, stringKey)
    } catch (ex: NotFoundException) {
        null
    }

    @Throws(NotFoundException::class)
    override fun getQuantityString(@PluralsRes id: Int, quantity: Int): String {
        val value = getQuantityStringFromRepository(id, quantity)
        return value ?: super.getQuantityString(id, quantity)
    }

    override fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any?): String {
        val value = getQuantityStringFromRepository(id, quantity)
        return if (value != null) {
            String.format(value, *formatArgs)
        } else super.getQuantityString(id, quantity, *formatArgs)
    }

    override fun getQuantityText(id: Int, quantity: Int): CharSequence {
        val value = getQuantityStringFromRepository(id, quantity)
        return value?.let { fromHtml(it) } ?: super.getQuantityText(id, quantity)
    }

    private fun getQuantityStringFromRepository(@PluralsRes id: Int, quantity: Int): String? = try {
        val currentLocale = Locale(RestringUtil.currentLanguage)
        val rule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val pluralRules = PluralRules.forLocale(currentLocale)
            pluralRules.select(quantity.toDouble()) ?: DEFAULT_PLURAL_RULE
        } else {
            DEFAULT_PLURAL_RULE
        }
        val stringKey = "${getResourceEntryName(id)}#$rule"
        stringRepository.getString(RestringUtil.currentLanguage, stringKey)
    } catch (ex: NotFoundException) {
        null
    }

    private fun fromHtml(source: String): CharSequence =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                @Suppress("DEPRECATION")
                Html.fromHtml(source)
            } else {
                Html.fromHtml(source, Html.FROM_HTML_MODE_COMPACT)
            }

    companion object {
        private const val DEFAULT_PLURAL_RULE = "other"
>>>>>>> develop
    }
}
