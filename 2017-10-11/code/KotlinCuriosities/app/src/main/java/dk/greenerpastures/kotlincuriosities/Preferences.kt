package dk.greenerpastures.kotlincuriosities

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * Wrapper class for [SharedPreferences] which allows you to define preferences with property
 * delegates.
 * ```
 *    class AppPrefs(context: Context) : Preferences(context, "app_prefs") {
 *        var hasData by key(defaultValue = false)
 *        var userClicks by key(keyName = "user_clicks")
 *    }
 * ```
 */
open class Preferences {
    private val prefs: SharedPreferences

    constructor(prefs: SharedPreferences) {
        this.prefs = prefs
    }

    constructor(context: Context, name: String) {
        prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    /**
     * Clears all settings in this Preferences object
     */
    fun clear() = prefs.edit().clear().apply()

    /**
     * Returns a property delegate for defining non-nullable values stored in [SharedPreferences].
     *
     * @param keyName the key to store the value. If null, the name of the property will be used.
     * @param default the value to return if the the value doesn't exist in preferences.
     */
    protected inline fun <reified T : Any> key(keyName: String? = null, default: T) =
            PrefsDelegate(keyName, default)

    /**
     * Returns a property delegate for defining nullable values
     *
     * @param keyName the key to store the value. If null, the name of the property will be used.
     */
    protected inline fun <reified T> key(keyName: String? = null) =
            PrefsDelegate<T?>(keyName, null)

    /**
     * Implementation class for property delegate. Don't use directly, use [key] to access it.
     */
    protected class PrefsDelegate<T>(private val name: String? = null, private val default: T)
        : ReadWriteProperty<Preferences, T> {
        override fun setValue(thisRef: Preferences, property: KProperty<*>, value: T) {
            val name = this.name ?: property.name
            when (value) {
                is Boolean -> thisRef.prefs.edit().putBoolean(name, value).apply()
                is Int -> thisRef.prefs.edit().putInt(name, value).apply()
                is Long -> thisRef.prefs.edit().putLong(name, value).apply()
                is Float -> thisRef.prefs.edit().putFloat(name, value).apply()
                is String? -> thisRef.prefs.edit().putString(name, value).apply()
                else -> throw NotImplementedError("Type not supported in PrefsDelegate")
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Preferences, property: KProperty<*>): T {
            val name = this.name ?: property.name
            return when (default) {
                is Boolean -> thisRef.prefs.getBoolean(name, default) as T
                is Int -> thisRef.prefs.getInt(name, default) as T
                is Long -> thisRef.prefs.getLong(name, default) as T
                is Float -> thisRef.prefs.getFloat(name, default) as T
                is String? -> thisRef.prefs.getString(name, default) as T
                else -> throw NotImplementedError("Type not supported in PrefsDelegate")
            }
        }
    }
}
