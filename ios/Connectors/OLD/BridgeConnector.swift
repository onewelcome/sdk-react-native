protocol BridgeConnectorProtocol: AnyObject {
    func sendBridgeEvent(eventName: OneginiBridgeEvents, data: Any!) -> Void
}

class BridgeConnector : BridgeConnectorProtocol {
    let toRegistrationConnector: BridgeToRegistrationConnectorProtocol = OLDRegistrationConnector()
    let toPinHandlerConnector: BridgeToPinConnectorProtocol = OLDPinConnector()
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

    func sendBridgeEvent(eventName: OneginiBridgeEvents, data: Any!) {
        bridge?.sendBridgeEvent(eventName: eventName, data: data)
    }
}
