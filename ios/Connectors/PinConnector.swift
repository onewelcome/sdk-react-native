protocol BridgeToPinConnectorProtocol: AnyObject {
    var bridgeConnector: BridgeConnectorProtocol? { get set }
    var pinHandler: PinConnectorToPinHandler { get }

    func handlePinAction(_ flow: (NSString), _ action: (NSString), _ pin: (NSString)) -> Void
    func sendNotification(event: PinNotification, flow: PinFlow?, error: NSError?) -> Void
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
                break
            case PinAction.cancel.rawValue:
                pinHandler.onCancel()
                break
            default:
                sendEvent(data: ["flow": flow, "action": PinNotification.showError.rawValue, "errorMsg": "Unsupported pin action. Contact SDK maintainer."])
                break
        }
    }

    func sendNotification(event: PinNotification, flow: PinFlow?, error: NSError?) {
        switch (event){
            case .open:
                sendEvent(data: ["flow": flow?.rawValue, "action": PinNotification.open.rawValue])
                break
            case .confirm:
                sendEvent(data: ["flow": flow?.rawValue, "action": PinNotification.confirm.rawValue])
                break;
            case .close:
                sendEvent(data: ["flow": flow?.rawValue, "action": PinNotification.close.rawValue])
                break;
            case .showError:
                sendEvent(data: ["flow": flow?.rawValue, "action": PinNotification.showError.rawValue, "errorMsg": error?.localizedDescription])
                break
        }
    }

  private func sendEvent(data: Any!) {
      bridgeConnector?.sendBridgeEvent(eventName: OneginiBridgeEvents.pinNotification, data: data)
  }
}


// Pin notification actions for RN Bridge
enum PinNotification : String {
    case open = "open",
         confirm = "confirm",
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
