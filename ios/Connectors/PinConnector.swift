protocol BridgeToPinConnectorProtocol: AnyObject {
    var bridgeConnector: BridgeConnectorProtocol? { get set }
    var pinHandler: PinConnectorToPinHandler { get }
  
    func handlePinAction(_ flow: (NSString), _ action: (NSString), _ pin: (NSString)) -> Void
    func sendNotification(event: PinNotification, mode: PINEntryMode?, error: SdkError?) -> Void
}

//@todo handle change and auth flows
class PinConnector : BridgeToPinConnectorProtocol {
    var pinHandler: PinConnectorToPinHandler
    unowned var bridgeConnector: BridgeConnectorProtocol?
    
    init() {
        pinHandler = PinHandler()
    }
    
    func handlePinAction(_ flow: (NSString), _ action: (NSString), _ pin: (NSString)) -> Void {
        if(PinAction.provide.rawValue === action){
            pinHandler.onPinProvided(pin: pin)
        } else if (PinAction.cancel.rawValue === action){
            pinHandler.onCancel()
        } else {
        
        }
    }
  
    func sendNotification(event: PinNotification, mode: PINEntryMode?, error: SdkError?) {
        switch (event){
            case .open:
                sendEvent(data: ["flow": PinFlow.create.rawValue, "action": PinNotification.open.rawValue])
                break
            case .confirm:
                sendEvent(data: ["flow": PinFlow.create.rawValue, "action": PinNotification.confirm.rawValue])
                break;
            case .close:
                sendEvent(data: ["flow": PinFlow.create.rawValue, "action": PinNotification.close.rawValue])
                break;
            case .authAttempt:
                sendEvent(data: ["flow": PinFlow.create.rawValue, "action": PinNotification.authAttempt.rawValue])
                break
            case .showError:
                sendEvent(data: ["flow": PinFlow.create.rawValue, "action": PinNotification.showError.rawValue, "errorMsg": error?.errorDescription])
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
         authAttempt = "auth_attempt",
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
