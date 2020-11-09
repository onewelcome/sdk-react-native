class PinValidationErrorDomainMapping {
    func mapError(_ error: Error) -> SdkError {
        let title = "Pin validation error"
        let recoverySuggestion = "Try a different one"

        switch error.code {
        case ONGPinValidationError.pinBlackListed.rawValue:
            let errorDescription = "PIN you've entered is blacklisted."
            return SdkError(title: title, errorDescription: errorDescription, recoverySuggestion: recoverySuggestion)

        case ONGPinValidationError.pinShouldNotBeASequence.rawValue:
            let errorDescription = "PIN you've entered appears to be a sequence."
            return SdkError(title: title, errorDescription: errorDescription, recoverySuggestion: recoverySuggestion)

        case ONGPinValidationError.wrongPinLength.rawValue:
            let requiredLength = String(describing: error.userInfo[ONGPinValidationErrorRequiredLengthKey]!)
            let errorDescription = "PIN has to be of \(requiredLength) characters length."
            return SdkError(title: title, errorDescription: errorDescription, recoverySuggestion: recoverySuggestion)

        case ONGPinValidationError.pinShouldNotUseSimilarDigits.rawValue:
            let maxSimilarDigits = String(describing: error.userInfo[ONGPinValidationErrorMaxSimilarDigitsKey]!)
            let errorDescription = "PIN should not use more that \(maxSimilarDigits) similar digits."
            return SdkError(title: title, errorDescription: errorDescription, recoverySuggestion: recoverySuggestion)

        default:
            return SdkError(errorDescription: "Something went wrong.")
        }
    }
}
