import Foundation

enum RegisterEvent {
    case registrationStarted
    case pinChallengeReceived(pinLength: Int?, error: Error?)
    case browserChallengeReceived(url: URL?, error: Error?)
    case customChallengeInitReceived(status: Int?, data: String?, error: Error?)
    case customChallengeFinishReceived(status: Int?, data: String?, error: Error?)
}

extension RegisterEvent {
    var error: Error? {
        switch self {
        case .registrationStarted: return nil
        case let .pinChallengeReceived(_, error): return error
        case let .browserChallengeReceived(_, error): return error
        case let .customChallengeInitReceived(_, _, error): return error
        case let .customChallengeFinishReceived(_, _, error): return error
        }
    }
    
    var dictionaryRepresentation: [String: Any]? {
        switch self {
        case .registrationStarted:
            return nil
        case let .pinChallengeReceived(pinLength, _):
            var value: [String: Any] = ["status": "pinChallengeReceived"]
            if let pinLength = pinLength {
                value["pinLength"] = pinLength
            }
            return value
        case let .browserChallengeReceived(url, _):
            var value: [String: Any] = ["status": "browserChallengeReceived"]
            if let url = url {
                value["url"] = url
            }
            return value
        case let .customChallengeInitReceived(status, data, _):
            var value: [String: Any] = ["status": "customChallengeInitReceived"]
            if let status = status {
                value["status"] = status
            }
            if let data = data {
                value["data"] = data
            }
            return value
        case let .customChallengeFinishReceived(status, data, _):
            var value: [String: Any] = ["status": "customChallengeFinishReceived"]
            if let status = status {
                value["status"] = status
            }
            if let data = data {
                value["data"] = data
            }
            return value
        }
    }
}

extension RegisterEvent: Event {
    static var allNames: [String] {
        [RegisterEvent.registrationStarted,
         .pinChallengeReceived(pinLength: nil, error: nil),
         .browserChallengeReceived(url: nil, error: nil),
         .customChallengeInitReceived(status: nil, data: nil, error: nil),
         .customChallengeFinishReceived(status: nil, data: nil, error: nil)
        ].map({$0.name})
    }
    
    var name: String {
        switch self {
        case .registrationStarted: return "registrationStarted"
        case .pinChallengeReceived: return "pinChallengeReceived"
        case .browserChallengeReceived: return "browserChallengeReceived"
        case .customChallengeInitReceived: return "customChallengeInitReceived"
        case .customChallengeFinishReceived: return "customChallengeFinishReceived"
        }
    }
    
    var data: [String: Any]? {
        if let error = error {
            return ["error": error]
        }
        
        return dictionaryRepresentation
    }
}


