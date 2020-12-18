protocol BridgeConnectorProtocol: AnyObject {
    func sendBridgeEvent(eventName: OneginiBridgeEvents, data: Any!) -> Void
}

class BridgeConnector : BridgeConnectorProtocol {
    let toRegistrationHandler: BridgeToRegisterHandlerProtocol = RegistrationHandler()
    let toLoginHandler: BridgeToLoginHandlerProtocol = LoginHandler()
    var toPinHandlerConnector: BridgeToPinConnectorProtocol
    var toAppToWebHandler: AppToWebHandlerProtocol = AppToWebHandler()
    
    unowned var bridge: ConnectorToRNBridgeProtocol?
    public static var shared:BridgeConnector?

    init() {
        self.toPinHandlerConnector = PinConnector()
        self.toPinHandlerConnector.bridgeConnector = self
        BridgeConnector.shared = self
    }

    func sendBridgeEvent(eventName: OneginiBridgeEvents, data: Any!) {
        bridge?.sendBridgeEvent(eventName: eventName, data: data)
    }
}
