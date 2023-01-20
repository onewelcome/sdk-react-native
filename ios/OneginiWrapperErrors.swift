enum WrapperError: LocalizedError {
    case identityProviderNotFound
    case profileDoesNotExist
    case authenticatorDoesNotExist
    case malformedUrl
    case parametersNotCorrect(description: String)
    case noProfileAuthenticated
    case registrationNotInProgress
    case mobileAuthNotInProgress
    case authenticationNotInProgress
    case pinCreationNotInProgress
    case actionNotAllowed(description: String)
    case authenticatorNotRegistered
    
    var errorDescription: String? {
        switch self {
        case .identityProviderNotFound:
            return "Identity provider not found"
        case .profileDoesNotExist:
            return "The profile does not exist"
        case .authenticatorDoesNotExist:
            return "The authenticator does not exist"
        case .malformedUrl:
            return "The supplied url is malformed"
        case .parametersNotCorrect(let description):
            return "The supplied parameters are not correct: " + description
        case .noProfileAuthenticated:
            return "No profile is currently authenticated"
        case .registrationNotInProgress:
            return "Registration is currently not in progress"
        case .mobileAuthNotInProgress:
            return "There is currently no mobile authentication in progress"
        case .authenticationNotInProgress:
            return "Authentication is currently not in progress"
        case .pinCreationNotInProgress:
            return "Pin creation is currently not in progress"
        case .actionNotAllowed(let description):
            return description
        case .authenticatorNotRegistered:
            return "The authenticator is not registered"
        }
    }
    var code: Int {
        switch self {
        case .identityProviderNotFound:
            return 8001
        case .profileDoesNotExist:
            return 8004
        case .authenticatorDoesNotExist:
            return 8006
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
        case .pinCreationNotInProgress:
            return 8016
        case .actionNotAllowed:
            return 8017
        case .authenticatorNotRegistered:
            return 8019
        }
    }
}
