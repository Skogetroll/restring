package com.ice.restring

import android.content.Context
import android.content.ContextWrapper
import android.os.Build

/**
 * Entry point for Restring. it will be used for initializing Restring components, setting new strings,
 * wrapping activity context.
 */
class Restring

/**
 * Initialize Restring with the specified configuration.
 *
 * @param context of the application.
 * @param config  of the Restring.
 */
private constructor(context: Context, config: RestringConfig = RestringConfig.default) {

    private lateinit var stringRepository: StringRepository
    private lateinit var viewTransformerManager: ViewTransformerManager

    init {
        initStringRepository(context, config)
        initViewTransformer()
    }

    companion object {
        private var isInitialized = false
        private var INSTANCE: Restring? = null

        private val instance: Restring
            get() = INSTANCE!!

        /**
         * Initialize Restring with the specified configuration.
         *
         * @param context of the application.
         * @param config  of the Restring.
         */
        @JvmOverloads
        fun init(context: Context, config: RestringConfig = RestringConfig.default) {
            if (!isInitialized) {
                isInitialized = true
                INSTANCE = Restring(context, config)
            }
        }

        /**
         * Wraps context of an activity to provide Restring features.
         *
         * @param base context of an activity.
         * @return the Restring wrapped context.
         */
        fun wrapContext(base: Context): ContextWrapper {
            return instance.wrapContext(base)
        }

        /**
         * Set strings of a language.
         *
         * @param language   the strings are for.
         * @param newStrings the strings of the language.
         */
        fun setStrings(language: String, newStrings: Map<String, String>) {
            instance.setStrings(language, newStrings)
        }

        /**
         * Set a single string for a language.
         *
         * @param language the string is for.
         * @param key      the string key.
         * @param value    the string value.
         */
        fun setString(language: String, key: String, value: String) {
            instance.setString(language, key, value)
        }

        fun hasStrings(language: String): Boolean {
            return instance.hasStrings(language)
        }
    }

    /**
     * Wraps context of an activity to provide Restring features.
     *
     * @param base context of an activity.
     * @return the Restring wrapped context.
     */
    fun wrapContext(base: Context): ContextWrapper {
        return RestringContextWrapper.wrap(base, stringRepository, viewTransformerManager)
    }

    /**
     * Set strings of a language.
     *
     * @param language   the strings are for.
     * @param newStrings the strings of the language.
     */
    fun setStrings(language: String, newStrings: Map<String, String>) {
        stringRepository.setStrings(language, newStrings)
    }

    /**
     * Set a single string for a language.
     *
     * @param language the string is for.
     * @param key      the string key.
     * @param value    the string value.
     */
    fun setString(language: String, key: String, value: String) {
        stringRepository.setString(language, key, value)
    }

    /**
     * Check if there're string for language
     * @param language for which to check
     * @return true if string repository is not empty
     */
    fun hasStrings(language: String): Boolean {
        return stringRepository.getStrings(language).isNotEmpty()
    }

    private fun initStringRepository(context: Context, config: RestringConfig) {
        if (config.isPersist) {
            stringRepository = SharedPrefStringRepository(context)
        } else {
            stringRepository = MemoryStringRepository()
        }

        val stringsLoader = config.stringsLoader
        if (stringsLoader != null) {
            StringsLoaderTask(stringsLoader, stringRepository).run()
        }
    }

    private fun initViewTransformer() {
        viewTransformerManager = ViewTransformerManager()
        val transformerManager = viewTransformerManager
        transformerManager.registerTransformer(TextViewTransformer())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transformerManager.registerTransformer(ToolbarTransformer())
        }
        transformerManager.registerTransformer(SupportToolbarTransformer())
        transformerManager.registerTransformer(BottomNavigationViewTransformer())
        transformerManager.registerTransformer(TextInputLayoutTransformer())
    }

    /**
     * Loader of strings skeleton. Clients can implement this interface if they want to load strings on initialization.
     * First the list of languages will be asked, then strings of each language.
     */
    interface StringsLoader {

        /**
         * Get supported languages.
         *
         * @return the list of languages.
         */
        val languages: List<String>

        /**
         * Get strings of a language as keys &amp; values.
         *
         * @param language of the strings.
         * @return the strings as (key, value).
         */
        fun getStrings(language: String): Map<String, String>
    }
}
