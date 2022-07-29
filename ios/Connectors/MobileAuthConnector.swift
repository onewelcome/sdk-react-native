protocol BridgeToMobileAuthConnectorProtocol: AnyObject {
    var bridgeConnector: BridgeConnectorProtocol? { get set }
    var mobileAuthHandler: MobileAuthConnectorToHandlerProtocol { get }

    func sendNotification(event: MobileAuthNotification, requestMessage: String?, error: NSError?) -> Void
}

class MobileAuthConnector : BridgeToMobileAuthConnectorProtocol {
    var mobileAuthHandler: MobileAuthConnectorToHandlerProtocol
    unowned var bridgeConnector: BridgeConnectorProtocol?

    init() {
        mobileAuthHandler = MobileAuthHandler()
    }

    func sendNotification(event: MobileAuthNotification, requestMessage: String?, error: NSError?) {
        switch (event){
            case .startAuthentication:
                sendEvent(data: ["mobileAuthenticationRequest": ["message": requestMessage], "action": MobileAuthNotification.startAuthentication.rawValue])
                break
            case .finishAuthentication:
                sendEvent(data: ["action": MobileAuthNotification.finishAuthentication.rawValue])
                break;
        }
    }

  private func sendEvent(data: Any!) {
    bridgeConnector?.sendBridgeEvent(eventName: OneWelcomeBridgeEvents.authWithOtpNotification, data: data)
  }
}


// Pin notification actions for RN Bridge
enum MobileAuthNotification : String {
    case startAuthentication = "startAuthentication",
         finishAuthentication = "finishAuthentication"
}
