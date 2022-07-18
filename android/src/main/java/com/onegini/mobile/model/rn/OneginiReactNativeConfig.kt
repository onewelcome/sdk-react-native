package com.onegini.mobile.model.rn

data class OneginiReactNativeConfig(val identityProviders: List<ReactNativeIdentityProvider>,
                                    val enableMobileAuthenticationOtp: Boolean,
                                    val enableFingerprint: Boolean) {
}