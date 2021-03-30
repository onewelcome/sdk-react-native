class MobileAuthEnrollmentErrorDomainMapping {
    func mapError(_ error: Error) -> SdkError {
        let title = "Mobile auth enrollment error"

        switch error.code {
        case ONGMobileAuthEnrollmentError.userNotAuthenticated.rawValue:
            let errorDescription = "No user is currently authenticated."
            let recoverySuggestion = "Please authenticate user and try again."
            return SdkError(title: title, errorDescription: errorDescription, recoverySuggestion: recoverySuggestion)

        case ONGMobileAuthEnrollmentError.deviceAlreadyEnrolled.rawValue:
            let errorDescription = "The device is already enrolled for mobile authentication."
            let recoverySuggestion = "Please authenticate user and try again."
            return SdkError(title: title, errorDescription: errorDescription, recoverySuggestion: recoverySuggestion)

        case ONGMobileAuthEnrollmentError.enrollmentNotAvailable.rawValue:
            let errorDescription = "Mobile authentication enrollment is not available."
            let recoverySuggestion = ""
            return SdkError(title: title, errorDescription: errorDescription, recoverySuggestion: recoverySuggestion)

        case ONGMobileAuthEnrollmentError.userAlreadyEnrolled.rawValue:
            let errorDescription = "The user is already enrolled for mobile authentication on another device."
            let recoverySuggestion = "Please disable mobile authentication on the other device and try again."
            return SdkError(title: title, errorDescription: errorDescription, recoverySuggestion: recoverySuggestion)

        case ONGMobileAuthEnrollmentError.notEnrolled.rawValue:
            let errorDescription = "The user is not enrolled for mobile authentication."
            let recoverySuggestion = "Enroll for mobile authentication."
            return SdkError(title: title, errorDescription: errorDescription, recoverySuggestion: recoverySuggestion)

        default:
            return SdkError(errorDescription: "Something went wrong.")
        }
    }
}
