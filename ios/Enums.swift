// Pin notification actions for RN Bridge
enum PinAuthNotification: String {
    case open
    case close
    case incorrectPin
}

enum PinCreateNotification: String {
    case open
    case close
    case pinNotAllowed
}

// Pin flows from RN Bridge
enum PinFlow: String {
    case create = "Create"
    case authentication = "Authentication"
}
