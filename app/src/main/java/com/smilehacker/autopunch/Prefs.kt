package com.smilehacker.autopunch

import android.content.Context
import android.content.SharedPreferences
import com.smilehacker.Megatron.util.DLog
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by kleist on 2017/2/8.
 */

object Prefs {
    private lateinit var mContext : Context
    private val mPref by lazy { mContext.getSharedPreferences("default", 0) }
    fun init(ctx: Context) {
        mContext = ctx
    }

    var enable by LazyReadWriteProperty {BoolPreference(mPref, "enable", false)}

    class StringPreference(val pref: SharedPreferences, val key: String, val defValue: String? = null) : ReadWriteProperty<Any?, String> {
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            pref.edit().putString(key, value).apply()
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return pref.getString(key, defValue)
        }
    }
    class IntPreference(val pref: SharedPreferences, val key: String, val defValue: Int = 0) : ReadWriteProperty<Any?, Int> {
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            pref.edit().putInt(key, value).apply()
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
            return pref.getInt(key, defValue)
        }
    }
    class LongPreference(val pref: SharedPreferences, val key: String, val defValue: Long = 0L) : ReadWriteProperty<Any?, Long> {
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
            pref.edit().putLong(key, value).apply()
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): Long {
            return pref.getLong(key, defValue)
        }
    }
    class BoolPreference(private val pref: SharedPreferences, val key: String, val defValue: Boolean = false) : ReadWriteProperty<Any?, Boolean> {
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            pref.edit().putBoolean(key, value).apply()
        }

        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return pref.getBoolean(key, defValue)
        }
    }

    class LazyReadWriteProperty<T, V>(private val initializer : ()->ReadWriteProperty<T, V>)
        : ReadWriteProperty<T, V> {
        private object EMPTY
        private var mValue: Any = EMPTY

        override fun getValue(thisRef: T, property: KProperty<*>): V {
            if (mValue == EMPTY) {
                mValue = initializer()
            }
            return (mValue as ReadWriteProperty<T, V>).getValue(thisRef, property)
        }

        override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
            DLog.i("setValue")
            if (mValue == EMPTY) {
                DLog.i("setValue init")
                mValue = initializer()
            }
            (mValue as ReadWriteProperty<T, V>).setValue(thisRef, property, value)
        }

    }

}
