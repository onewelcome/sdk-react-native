package com.onegini.mobile.sdk.reactnative.model.rn

data class OneginiReactNativeConfig(
        val configModelClassName: String?,
        val securityControllerClassName: String?,
        val identityProviders: List<ReactNativeIdentityProvider>,
        val enableMobileAuthenticationOtp: Boolean,
        val enableFingerprint: Boolean
)
