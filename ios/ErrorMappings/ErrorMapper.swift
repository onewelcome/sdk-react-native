class ErrorMapper {
    func mapError(_ error: Error, pinChallenge: ONGPinChallenge? = nil, customInfo: ONGCustomInfo? = nil) -> SdkError {
        switch error.domain {
        case ONGGenericErrorDomain:
            return GenericErrorDomainMapping().mapError(error)
        case ONGPinValidationErrorDomain:
          return PinValidationErrorDomainMapping().mapError(error)
        default:
            return SdkError(errorDescription: "Something went wrong.")
        }
    }
}
