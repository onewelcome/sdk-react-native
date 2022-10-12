// Pin notification actions for RN Bridge
enum PinNotification: String {
    case open
    case close
    case showError
    case incorrectPin
    case pinNotAllowed
}

// Pin actions from RN Bridge
enum PinAction: String {
    case provide
    case cancel
}

// Pin flows from RN Bridge
enum PinFlow: String {
    case create = "Create"
    case authentication = "Authentication"
}
