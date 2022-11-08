enum WrapperError : Error {
    case profileDoesNotExist
    case identityProviderNotFound
    case malformedUrl
    case parametersNotCorrect
    case noProfileAuthenticated
    case registrationNotInProgress
    case mobileAuthNotInProgress
    case authenticationNotInProgress
    
    var description: String {
        switch self {
        case .identityProviderNotFound:
            return "Identity provider not found"
        case .profileDoesNotExist:
            return "The profile does not exist"
        case .malformedUrl:
            return "The supplied url is malformed"
        case .parametersNotCorrect:
            return "The supplied parameters are not correct"
        case .noProfileAuthenticated:
            return "No profile is currently authenticated"
        case .registrationNotInProgress:
            return "Registration is currently not in progress"
        case .mobileAuthNotInProgress:
            return "There is currently no mobile authentication in progress"
        case .authenticationNotInProgress:
            return "Authentication is currently not in progress"
        }
    }
    var code: Int {
        switch self {
        case .identityProviderNotFound:
            return 8001
        case .profileDoesNotExist:
            return 8004
        case .parametersNotCorrect:
            return 8009
        case .registrationNotInProgress:
            return 8010
        case .noProfileAuthenticated:
            return 8012
        case .mobileAuthNotInProgress:
            return 8013
        case .malformedUrl:
            return 8014
        case .authenticationNotInProgress:
            return 8015
        }
    }
}

extension WrapperError: LocalizedError {
    public var errorDescription: String? {
        return self.description
    }
}
