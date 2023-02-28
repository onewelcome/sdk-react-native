protocol BridgeConnectorProtocol: AnyObject {
    func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any)
}

class BridgeConnector: BridgeConnectorProtocol {
    let toRegistrationConnector = RegistrationConnector()
    let toLoginHandler = LoginHandler()
    let toMobileAuthConnector = MobileAuthConnector()
    let toAuthenticatorsHandler = AuthenticatorsHandler()
    let toAppToWebHandler = AppToWebHandler()
    let toResourceHandler = ResourceHandler()
    let toChangePinHandler: ChangePinHandler
    unowned var bridge: ConnectorToRNBridgeProtocol?
    public static var shared: BridgeConnector?

    init() {
        self.toChangePinHandler = ChangePinHandler(loginHandler: toLoginHandler, registrationHandler: toRegistrationConnector.registrationHandler)
        self.toRegistrationConnector.bridgeConnector = self
        self.toMobileAuthConnector.bridgeConnector = self
        BridgeConnector.shared = self
    }

    func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any) {
        bridge?.sendBridgeEvent(eventName: eventName, data: data)
    }
}
