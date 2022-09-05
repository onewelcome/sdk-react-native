// Pin notification actions for RN Bridge
enum PinNotification: String {
    case open
    case close
    case showError = "show_error"
}

// Pin actions from RN Bridge
enum PinAction: String {
    case provide
    case cancel
}

// Pin flows from RN Bridge
enum PinFlow: String {
    case Create
    case Authentication
}

