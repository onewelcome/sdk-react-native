protocol BridgeToChangePinConnectorProtocol: AnyObject {
    var bridgeConnector: BridgeConnectorProtocol? { get set }
    var pinView: ChangePinConnectorToViewProtocol? { get set }
  
    func handlePinAction(_ action: (NSString), _ isCreatePinFlow: (NSNumber), _ pin: (NSString)) -> Void
    func sendNotification(event: PinNotification, mode: PINEntryMode?, error: SdkError?) -> Void
}

class ChangePinConnector : BridgeToChangePinConnectorProtocol {
    unowned var bridgeConnector: BridgeConnectorProtocol?
    unowned var pinView: ChangePinConnectorToViewProtocol?
    
    func handlePinAction(_ action: (NSString), _ isCreatePinFlow: (NSNumber), _ pin: (NSString)) -> Void {
      if(PinAction.provide.rawValue === action){
          pinView?.onPinProvided(pin: pin)
      } else if (PinAction.cancel.rawValue === action){
          pinView?.onCancel()
      } else {
        
      }
    }
  
    func sendNotification(event: PinNotification, mode: PINEntryMode?, error: SdkError?) {
        switch (event){
            case .open:
                sendEvent(data: ["action": PinNotification.open.rawValue, "isCreatePinFlow": mode == PINEntryMode.registration || mode == PINEntryMode.registration])
                break
            case .confirm:
                sendEvent(data: ["action": PinNotification.confirm.rawValue])
                break;
            case .close:
                sendEvent(data: ["action": PinNotification.close.rawValue])
                break;
            case .authAttempt:
                sendEvent(data: ["action": PinNotification.authAttempt.rawValue])
                break
            case .showError:
                sendEvent(data: ["action": PinNotification.showError.rawValue, "errorMsg": error?.errorDescription])
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
