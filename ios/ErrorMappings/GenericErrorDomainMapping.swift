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
            
            
        case ONGGenericError.unknown.rawValue:
            return SdkError(title: "Token server error", errorDescription: "The Token Server configuration is invalid.", recoverySuggestion: "Contact token server maintainer.")
        case ONGGenericError.configurationInvalid.rawValue:
            return SdkError(title: "Token server error", errorDescription: "The request to the Token Server was invalid. Please verify that the Token Server configuration is correct and that no reverse proxy is interfering with the connection.", recoverySuggestion: "Contact token server maintainer.")
        case ONGGenericError.requestInvalid.rawValue:
            return SdkError(title: "Token server error", errorDescription: "The device could not be registered with the Token Server, verify that the SDK configuration, Token Server configuration and security features are correctly configured.", recoverySuggestion: "Contact token server or SDK  maintainer.")
        case ONGGenericError.deviceRegistrationFailure.rawValue:
            return SdkError(title: "Token server error", errorDescription: "Updating the device registration with the Token Server failed. Verify that the SDK configuration, Token Server configuration and security features are correctly configured.", recoverySuggestion: "Contact token server or SDK  maintainer.")
        case ONGGenericError.deviceRegistrationUpdateFailure.rawValue:
            return SdkError(title: "Data storage error", errorDescription: "The data storage could not be accessed.", recoverySuggestion: "Contact SDK  maintainer.")
        case ONGGenericError.dataStorageNotAvailable.rawValue:
            return SdkError(title: "Data storage error", errorDescription: "The data storage is corrupted and cannot be recovered or cleared.", recoverySuggestion: "Contact SDK  maintainer.")
        default:
            return SdkError(errorDescription: "Something went wrong.")
        }
    }
}
