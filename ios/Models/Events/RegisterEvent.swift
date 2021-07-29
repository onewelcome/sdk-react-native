import Foundation

enum RegisterEvent {
    case registrationStarted
    case pinChallengeReceived(data: [String: Any], error: Error?)
    case browserChallengeReceived(data: [String: Any], error: Error?)
    case customChallengeInitReceived(data: [String: Any], error: Error?)
    case customChallengeFinishReceived(data: [String: Any], error: Error?)
}

extension RegisterEvent {
    var error: Error? {
        switch self {
        case .registrationStarted: return nil
        case let .pinChallengeReceived(_, error): return error
        case let .browserChallengeReceived(_, error): return error
        case let .customChallengeInitReceived(_, error): return error
        case let .customChallengeFinishReceived(_, error): return error
        }
    }
    
    var dictionaryRepresentation: [String: Any]? {
        switch self {
        case .registrationStarted:
            return nil
        case let .pinChallengeReceived(data, _): return data
        case let .browserChallengeReceived(data, _): return data
        case let .customChallengeInitReceived(data, _): return data
        case let .customChallengeFinishReceived(data, _): return data
        }
    }
}

extension RegisterEvent: Event {
    var name: String {
        OneginiBridgeEvents.customRegistrationNotification.rawValue
    }
    
    var data: [String: Any]? {
        if let error = error {
            return ["error": error]
        }
        
        return dictionaryRepresentation
    }
}


