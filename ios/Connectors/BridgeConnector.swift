protocol BridgeConnectorProtocol: AnyObject {
    func sendBridgeEvent(eventName: OneginiBridgeEvents, data: Any!) -> Void
}

class BridgeConnector : BridgeConnectorProtocol {
    let toRegistrationHandler: BridgeToRegisterViewProtocol
    var toChangePinConnector: BridgeToChangePinConnectorProtocol
    unowned var bridge: ConnectorToRNBridgeProtocol?
    public static var shared:BridgeConnector?
    
    init() {
        self.toRegistrationHandler = RegistrationHandler()
        self.toChangePinConnector = ChangePinConnector()
        self.toChangePinConnector.bridgeConnector = self
        BridgeConnector.shared = self
    }
  
    func sendBridgeEvent(eventName: OneginiBridgeEvents, data: Any!) {
        bridge?.sendBridgeEvent(eventName: eventName, data: data)
    }
}
