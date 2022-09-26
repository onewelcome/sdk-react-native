protocol BridgeToRegistrationConnectorProtocol: AnyObject {
    var bridgeConnector: BridgeConnectorProtocol? { get set }
    var registrationHandler: RegistrationConnectorToHandlerProtocol { get }

    func handleCustomRegistrationAction(_ action: String, _ identityProviderId: String, _ code: String?) -> Void
    func sendCustomRegistrationNotification(_ event: CustomRegistrationNotification,_ data: NSMutableDictionary?) -> Void
}

class RegistrationConnector : BridgeToRegistrationConnectorProtocol {
    var registrationHandler: RegistrationConnectorToHandlerProtocol
    unowned var bridgeConnector: BridgeConnectorProtocol?

    init() {
        registrationHandler = RegistrationHandler()
    }

    func handleCustomRegistrationAction(_ action: String, _ identityProviderId: String, _ code: String? = nil) -> Void {
        switch action {
            case CustomRegistrationAction.provide.rawValue:
                registrationHandler.processOTPCode(code: code)
                break
            case CustomRegistrationAction.cancel.rawValue:
                registrationHandler.cancelCustomRegistration()
                break
            default:
                sendEvent(data: ["action": PinNotification.showError.rawValue, "errorMsg": "Unsupported custom registration action. Contact SDK maintainer."])
                break
        }
    }

    func sendCustomRegistrationNotification(_ event: CustomRegistrationNotification,_ data: NSMutableDictionary?) {
        
        switch (event){
            case .initRegistration:
                data?.setValue(CustomRegistrationNotification.initRegistration.rawValue, forKey: "action")
                sendEvent(data: data)
                break
            case .finishRegistration:
                data?.setValue(CustomRegistrationNotification.finishRegistration.rawValue, forKey: "action")
                sendEvent(data: data)
                break;
        }
    }

  private func sendEvent(data: Any!) {
      bridgeConnector?.sendBridgeEvent(eventName: OneWelcomeBridgeEvents.customRegistrationNotification, data: data)
  }
}


// Custom registration notification actions for RN Bridge
enum CustomRegistrationNotification: String {
    case initRegistration
    case finishRegistration
}


// Custom registration actions from RN Bridge
enum CustomRegistrationAction: String {
    case provide
    case cancel
}
