package com.onegini.mobile.model.rn

data class OneginiReactNativeConfig(
    val configModelClassName: String?,
    val securityControllerClassName: String?,
    val identityProviders: List<ReactNativeIdentityProvider>,
    val enableMobileAuthenticationOtp: Boolean,
    val enableFingerprint: Boolean
)
