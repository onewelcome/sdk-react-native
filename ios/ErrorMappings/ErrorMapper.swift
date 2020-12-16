class ErrorMapper {
    func mapError(_ error: Error, pinChallenge: ONGPinChallenge? = nil, customInfo: ONGCustomInfo? = nil) -> SdkError {
        switch error.domain {
            case ONGGenericErrorDomain:
                return GenericErrorDomainMapping().mapError(error)
            case ONGPinValidationErrorDomain:
                return PinValidationErrorDomainMapping().mapError(error)
            case ONGAuthenticationErrorDomain:
                if let pinChallenge = pinChallenge, error.code == ONGAuthenticationError.invalidPin.rawValue {
                    return AuthenticationErrorDomainMapping().mapErrorWithPinChallenge(pinChallenge: pinChallenge)
                } else if let customInfo = customInfo, error.code == ONGAuthenticationError.customAuthenticatorFailure.rawValue {
                    return AuthenticationErrorDomainMapping().mapErrorWithCustomInfo(customInfo)
                } else {
                    return AuthenticationErrorDomainMapping().mapError(error)
                }
            default:
                return SdkError(errorDescription: "Something went wrong.")
            }
    }
}
