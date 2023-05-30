protocol BridgeToMobileAuthConnectorProtocol: AnyObject {
    var bridgeConnector: BridgeConnectorProtocol? { get set }
    var mobileAuthHandler: MobileAuthConnectorToHandlerProtocol { get }

    func sendNotification(event: MobileAuthNotification, requestMessage: String?, error: Error?)
}

class MobileAuthConnector: BridgeToMobileAuthConnectorProtocol {
    var mobileAuthHandler: MobileAuthConnectorToHandlerProtocol
    unowned var bridgeConnector: BridgeConnectorProtocol?

    init() {
        mobileAuthHandler = MobileAuthHandler()
    }

    func sendNotification(event: MobileAuthNotification, requestMessage: String?, error: Error?) {
        switch event {
        case .startAuthentication:
            sendEvent(data: ["mobileAuthenticationRequest": ["message": requestMessage], "action": MobileAuthNotification.startAuthentication.rawValue])
        case .finishAuthentication:
            sendEvent(data: ["action": MobileAuthNotification.finishAuthentication.rawValue])
        }
    }

  private func sendEvent(data: Any) {
    bridgeConnector?.sendBridgeEvent(eventName: OneWelcomeBridgeEvents.authWithOtpNotification, data: data)
  }
}

// Pin notification actions for RN Bridge
enum MobileAuthNotification: String {
    case startAuthentication
    case finishAuthentication
}
