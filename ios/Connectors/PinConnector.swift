protocol BridgeToPinConnectorProtocol: AnyObject {
    var bridgeConnector: BridgeConnectorProtocol? { get set }
    var pinHandler: PinConnectorToPinHandler { get }

    func handlePinAction(_ flow: (NSString), _ action: (NSString), _ pin: (NSString)) -> Void
    func sendNotification(event: PinNotification, flow: PinFlow?, error: NSError?, profileId: String?, userInfo: [String: Any]?, data: Any?) -> Void
}

//@todo handle change and auth flows
class PinConnector : BridgeToPinConnectorProtocol {
    var pinHandler: PinConnectorToPinHandler
    unowned var bridgeConnector: BridgeConnectorProtocol?

    init() {
        pinHandler = PinHandler()
    }

    func handlePinAction(_ flow: (NSString), _ action: (NSString), _ pin: (NSString)) -> Void {
        switch action {
            case PinAction.provide.rawValue:
                pinHandler.onPinProvided(pin: pin)
            case PinAction.cancel.rawValue:
                pinHandler.onCancel()
            default:
                sendEvent(data: ["flow": flow, "action": PinNotification.showError.rawValue, "errorMsg": "Unsupported pin action. Contact SDK maintainer."])
        }
    }

    func sendNotification(event: PinNotification, flow: PinFlow?, error: NSError?, profileId: String?, userInfo: [String: Any]? = nil, data: Any?) {
        switch (event){
            case .open:
            sendEvent(data: ["flow": flow?.rawValue ?? "", "action": PinNotification.open.rawValue, "profileId": profileId ?? "", "userInfo": userInfo ?? [:], "data": data ?? [:]])
            case .close:
                sendEvent(data: ["flow": flow?.rawValue ?? "", "action": PinNotification.close.rawValue, "profileId": profileId ?? "", "data": data ?? [:]])
            case .showError:
                sendEvent(data: ["flow": flow?.rawValue ?? "", "action": PinNotification.showError.rawValue, "errorMsg": error?.localizedDescription ?? "", "profileId": profileId ?? "", "userInfo": userInfo ?? [:], "data": data ?? [:]])
        }
    }

  private func sendEvent(data: Any!) {
      bridgeConnector?.sendBridgeEvent(eventName: OneWelcomeBridgeEvents.pinNotification, data: data)
  }
}

// Pin notification actions for RN Bridge
enum PinNotification : String {
    case open = "open",
         close = "close",
         showError = "show_error"
}

// Pin actions from RN Bridge
enum PinAction : NSString {
    case provide = "provide",
         cancel = "cancel"
}

// Pin flows from RN Bridge
enum PinFlow : NSString {
    case create = "create",
         change = "change",
         authentication = "authentication"
}
