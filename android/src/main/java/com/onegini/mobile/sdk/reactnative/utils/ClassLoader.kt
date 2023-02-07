package com.onegini.mobile.sdk.reactnative.utils

import android.content.Context

class ClassLoader(context: Context) {
    private val packageName = context.packageName

    @Throws(ClassNotFoundException::class)
    fun getClassByName(className: String): Class<*> {
        return try {
            Class.forName(getPathToClassByName(className))
        } catch (e: ClassNotFoundException) {
            Class.forName(getShortenPathToClassByName(className))
        }
    }

    private fun getPathToClassByName(className: String): String {
        return "$packageName.$className"
    }

    private fun getShortenPathToClassByName(className: String): String {
        val lastDotIndex = packageName.lastIndexOf('.')
        val shortenPackageName = packageName.substring(0, lastDotIndex)
        return "$shortenPackageName.$className"
    }
}
