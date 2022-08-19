package com.onegini.mobile.sdk.reactnative.mapers

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.onegini.mobile.sdk.android.model.entity.UserProfile

object UserProfileMapper {

    const val USERP_ROFILE = "userProfile"

    fun toWritableMap(profileUser: UserProfile?): WritableMap {
        val map = Arguments.createMap()
        map.putString("profileId", profileUser?.profileId)
        return map
    }

    fun toWritableMap(profiles: Set<UserProfile>): WritableArray {
        val array = Arguments.createArray()
        profiles.forEach {
            array.pushMap(toWritableMap(it))
        }
        return array
    }

    fun toUserProfile(profileId: String): UserProfile {
        return UserProfile(profileId)
    }

    fun add(writableMap: WritableMap, profileUser: UserProfile?) {
        writableMap.putMap(USERP_ROFILE, profileUser?.let { toWritableMap(it) })
    }
}
