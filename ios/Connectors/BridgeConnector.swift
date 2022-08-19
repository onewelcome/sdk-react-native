protocol BridgeConnectorProtocol: AnyObject {
    func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any!) -> Void
}

class BridgeConnector : BridgeConnectorProtocol {
    let toRegistrationConnector: BridgeToRegistrationConnectorProtocol = RegistrationConnector()
    let toPinHandlerConnector: BridgeToPinConnectorProtocol = PinConnector()
    let toMobileAuthConnector: BridgeToMobileAuthConnectorProtocol = MobileAuthConnector()
    let toLoginHandler: BridgeToLoginHandlerProtocol = LoginHandler()
    let toAuthenticatorsHandler: BridgeToAuthenticatorsHandlerProtocol = AuthenticatorsHandler()
    let toAppToWebHandler: AppToWebHandlerProtocol = AppToWebHandler()
    let toResourceHandler: BridgeToResourceHandlerProtocol = ResourceHandler()
    unowned var bridge: ConnectorToRNBridgeProtocol?
    public static var shared:BridgeConnector?

    init() {
        self.toRegistrationConnector.bridgeConnector = self
        self.toPinHandlerConnector.bridgeConnector = self
        self.toMobileAuthConnector.bridgeConnector = self
        BridgeConnector.shared = self
    }

    func sendBridgeEvent(eventName: OneWelcomeBridgeEvents, data: Any!) {
        bridge?.sendBridgeEvent(eventName: eventName, data: data)
    }
}
