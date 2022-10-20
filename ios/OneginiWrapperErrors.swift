enum WrapperError : Error {
    case profileDoesNotExist
    case identityProviderNotFound
    case malformedUrl
    case parametersNotCorrect
    case noProfileAuthenticated
    
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
        case .malformedUrl:
            return 8010
        case .noProfileAuthenticated:
            return 8011
        }
    }
}
