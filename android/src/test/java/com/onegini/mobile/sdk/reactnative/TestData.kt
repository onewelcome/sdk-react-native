package com.onegini.mobile.sdk.reactnative

import android.os.Parcel
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider

object TestData {

    val config: JavaOnlyMap
        get() {
            val provider1 = HashMap<String, Any>()
            provider1["id"] = "2-way-otp-api"
            provider1["isTwoStep"] = true

            val providers = JavaOnlyArray.of(provider1)

            val config = JavaOnlyMap()
            config.putString("configModelClassName", null)
            // Normally it would be app's package but here we run it in test env
            config.putString("securityControllerClassName", "com.onegini.mobile.clean.SecurityController")
            config.putBoolean("enableMobileAuthenticationOtp", true)
            config.putBoolean("enableFingerprint", true)
            config.putArray("customProviders", providers)

            return config
        }

    //

    val identityProvider1: OneginiIdentityProvider = object : OneginiIdentityProvider {
        override fun describeContents(): Int { return 0 }

        override fun writeToParcel(dest: Parcel?, flags: Int) {}

        override fun getId(): String { return "100" }

        override fun getName(): String { return "Provider_1" }
    }

    val identityProvider2: OneginiIdentityProvider = object : OneginiIdentityProvider {
        override fun describeContents(): Int { return 0 }

        override fun writeToParcel(dest: Parcel?, flags: Int) {}

        override fun getId(): String { return "200" }

        override fun getName(): String { return "Provider_2" }
    }
}
