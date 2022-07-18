class GenericErrorDomainMapping {
    func mapError(_ error: Error) -> SdkError {
        switch error.code {
        case ONGGenericError.networkConnectivityFailure.rawValue, ONGGenericError.serverNotReachable.rawValue:
            return SdkError(title: "Connection error", errorDescription: "Failed to connect to the server.")
        case ONGGenericError.userDeregistered.rawValue:
            return SdkError(title: "User error", errorDescription: "The users account was deregistered from the device.", recoverySuggestion: "Please try to register user again.")
        case ONGGenericError.deviceDeregistered.rawValue:
            return SdkError(title: "Device error", errorDescription: "All users were disconnected from the device.", recoverySuggestion: "Please try to register user again.")
        case ONGGenericError.outdatedOS.rawValue:
            return SdkError(title: "OS error", errorDescription: "Your iOS version is no longer accepted by the application.", recoverySuggestion: "Please try to update your iOS.")
        case ONGGenericError.outdatedApplication.rawValue:
            return SdkError(title: "Application error", errorDescription: "Your application version is outdated.", recoverySuggestion: "Please try to update your application.")
        case ONGGenericError.unrecoverableDataState.rawValue:
            return SdkError(title: "Data storage error", errorDescription: "The data storage is corrupted and cannot be recovered or cleared.", recoverySuggestion: "Please remove the application manually and reinstall.")
        default:
            return SdkError(errorDescription: "Something went wrong.")
        }
    }
}
