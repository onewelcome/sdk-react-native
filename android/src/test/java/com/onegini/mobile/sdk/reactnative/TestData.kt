package com.onegini.mobile.sdk.reactnative

import android.os.Parcel
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.OneginiClientConfigModel
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider
import com.onegini.mobile.sdk.android.model.entity.UserProfile

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

    //

    val authenticator1: OneginiAuthenticator = object : OneginiAuthenticator {
        override fun getId(): String {
            return "id1"
        }

        override fun getType(): Int {
            return OneginiAuthenticator.CUSTOM
        }

        override fun getName(): String {
            return "name1"
        }

        override fun isRegistered(): Boolean {
            return true
        }

        override fun isPreferred(): Boolean {
            return true
        }

        override fun getUserProfile(): UserProfile {
            return UserProfile("123456")
        }
    }

    val authenticator2: OneginiAuthenticator = object : OneginiAuthenticator {
        override fun getId(): String {
            return "id2"
        }

        override fun getType(): Int {
            return OneginiAuthenticator.CUSTOM
        }

        override fun getName(): String {
            return "name2"
        }

        override fun isRegistered(): Boolean {
            return false
        }

        override fun isPreferred(): Boolean {
            return false
        }

        override fun getUserProfile(): UserProfile {
            return UserProfile("234567")
        }
    }

    //

    val configModel = object : OneginiClientConfigModel {
        override fun getAppIdentifier(): String {
            return "appId"
        }

        override fun getAppPlatform(): String {
            return "iOS"
        }

        override fun getRedirectUri(): String {
            return "www.google.pl?param=123&test=2"
        }

        override fun getAppVersion(): String {
            return "1.0.0"
        }

        override fun getBaseUrl(): String {
            return "google.pl"
        }

        override fun getResourceBaseUrl(): String {
            return "resources.pl"
        }

        override fun getCertificatePinningKeyStore(): Int {
            return 123
        }

        override fun getKeyStoreHash(): String {
            return "hash123"
        }

        override fun getDeviceName(): String {
            return "Szamszung"
        }

        override fun getServerPublicKey(): String? {
            TODO("Not yet implemented")
        }
    }
}
