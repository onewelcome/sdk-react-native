class AuthenticationErrorDomainMapping {
    let title = "Authentication error"

    func mapErrorWithPinChallenge(pinChallenge: ONGPinChallenge) -> SdkError {
        let remainingFailureCount = String(describing: pinChallenge.remainingFailureCount)
        let errorDescription = "PIN you've entered is invalid."
        let recoverySuggestion = "You have still \(remainingFailureCount) attempts left."
        return SdkError(title: title, errorDescription: errorDescription, recoverySuggestion: recoverySuggestion)
    }

    func mapErrorWithCustomInfo(_ customInfo: ONGCustomInfo) -> SdkError {
        if customInfo.status >= 4000 && customInfo.status < 5000 {
            let message = "Authentication failed"
            return SdkError(title: title, errorDescription: message)
        } else {
            return SdkError(errorDescription: "Something went wrong.")
        }
    }

    func mapError(_ error: Error) -> SdkError {
        switch error.code {
        case ONGAuthenticationError.authenticatorDeregistered.rawValue:
            let message = "The Authenticator has been deregistered."
            let recoverySuggestion = "Please register used authenticator and try again."
            return SdkError(title: title, errorDescription: message, recoverySuggestion: recoverySuggestion)

        case ONGAuthenticationError.authenticatorInvalid.rawValue:
            let message = "The authenticator that you provided is invalid."
            let recoverySuggestion = "It may not exist, please verify whether you have supplied the correct authenticator."
            return SdkError(title: title, errorDescription: message, recoverySuggestion: recoverySuggestion)

        case ONGAuthenticationError.touchIDAuthenticatorFailure.rawValue:
            let message = "Authentication with the biometric authenticator has failed."
            return SdkError(title: title, errorDescription: message)

        default:
            return SdkError(errorDescription: "Something went wrong.")
        }
    }
}
