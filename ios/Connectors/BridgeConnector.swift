protocol BridgeConnectorProtocol: AnyObject {
    func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any!) -> Void
}

class BridgeConnector : BridgeConnectorProtocol {
    let toRegistrationConnector = RegistrationConnector()
    let toChangePinHandler = ChangePinHandler()
    let toMobileAuthConnector = MobileAuthConnector()
    let toLoginHandler = LoginHandler()
    let toAuthenticatorsHandler = AuthenticatorsHandler()
    let toAppToWebHandler = AppToWebHandler()
    let toResourceHandler = ResourceHandler()
    unowned var bridge: ConnectorToRNBridgeProtocol?
    public static var shared:BridgeConnector?

    init() {
        self.toRegistrationConnector.bridgeConnector = self
        self.toMobileAuthConnector.bridgeConnector = self
        BridgeConnector.shared = self
    }

    func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any!) {
        bridge?.sendBridgeEvent(eventName: eventName, data: data)
    }
}
