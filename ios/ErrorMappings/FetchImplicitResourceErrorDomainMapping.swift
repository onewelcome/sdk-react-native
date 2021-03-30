class FetchImplicitResourceErrorDomainMapping: NSObject {
    func mapError(_ error: Error) -> SdkError {
        let title = "Fetching implicit resource error"

        switch error.code {
        case ONGFetchResourceImplicitlyError.implicitResourceErrorUserNotAuthenticatedImplicitly.rawValue:
            let errorDescription = "A selected user isn't currently authenticated implicitly."
            return SdkError(title: title, errorDescription: errorDescription, recoverySuggestion: "Try select this user one more time.")

        default:
            return SdkError(errorDescription: "Something went wrong.")
        }
    }
}
