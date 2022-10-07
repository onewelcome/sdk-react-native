enum WrapperError : Error {
    case profileDoesNotExist
    case identityProviderNotFound
    case malformedUrl
    case parametersNotCorrect
    
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
        }
    }
}
