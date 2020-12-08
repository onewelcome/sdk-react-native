protocol BridgeConnectorProtocol: AnyObject {
    func sendBridgeEvent(eventName: OneginiBridgeEvents, data: Any!) -> Void
}

class BridgeConnector : BridgeConnectorProtocol {
    let toRegistrationHandler: BridgeToRegisterHandlerProtocol = RegistrationHandler()
    var toPinHandlerConnector: BridgeToPinConnectorProtocol
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
